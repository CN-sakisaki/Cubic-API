package com.saki.apiproject.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.saki.apiproject.config.EmailConfig;
import com.saki.apiproject.exception.SentinelGlobalBlockHandler;
import com.saki.apiproject.mapper.UserMapper;
import com.saki.apiproject.model.dto.user.UserBindEmailRequest;
import com.saki.apiproject.model.dto.user.UserEmailLoginRequest;
import com.saki.apiproject.model.dto.user.UserUnBindEmailRequest;
import com.saki.apiproject.model.enums.UserAccountStatusEnum;
import com.saki.apiproject.model.vo.UserVO;
import com.saki.apiproject.service.UserService;
import com.saki.common.common.BusinessException;
import com.saki.common.common.ErrorCode;
import com.saki.common.model.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import static com.saki.apiproject.constant.EmailConstant.*;
import static com.saki.apiproject.constant.UserConstant.ADMIN_ROLE;
import static com.saki.apiproject.constant.UserConstant.USER_LOGIN_STATE;
import static com.saki.apiproject.utils.EmailUtil.buildEmailContent;


/**
 * @author JianShang
 * @version 1.0.0
 * @description 用户服务实现类
 * @date 2024-09-13 12:09:31
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private JavaMailSender mailSender;

    @Resource
    private EmailConfig emailConfig;

    @Resource
    private RedissonClient redissonClient;

    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 盐值，混淆密码
     */
    private static final String SALT = "js";

    public UserServiceImpl(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public long userRegister(String userAccount, String email, String userPassword, String checkPassword) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }
        // 密码和校验密码相同
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        }
        synchronized (userAccount.intern()) {
            // 账户不能重复
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("userAccount", userAccount);
            long count = userMapper.selectCount(queryWrapper);
            if (count > 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");
            }
            // 2. 加密
            String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
            // 3. 分配 accessKey, secretKey
            String accessKey = DigestUtil.md5Hex(SALT + userAccount + RandomUtil.randomNumbers(5));
            String secretKey = DigestUtil.md5Hex(SALT + userAccount + RandomUtil.randomNumbers(8));
            // 4. 插入数据
            User user = new User();
            user.setUserAccount(userAccount);
            user.setEmail(email);
            user.setUserPassword(encryptPassword);
            user.setAccessKey(accessKey);
            user.setSecretKey(secretKey);
            boolean saveResult = this.save(user);
            if (!saveResult) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败，数据库错误");
            }
            return user.getId();
        }
    }

    @Override
    @SentinelResource(blockHandler = "handler", blockHandlerClass = SentinelGlobalBlockHandler.class)
    public UserVO userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号错误");
        }
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码错误");
        }
        // 2. 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPassword);
        User user = userMapper.selectOne(queryWrapper);
        // 用户不存在
        if (user == null) {
            log.info("user login failed, userAccount cannot match userPassword");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在或密码错误");
        }
        // 3. 记录用户的登录态
        request.getSession().setAttribute(USER_LOGIN_STATE, user);

        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }

    /**
     * 用户电子邮件登录
     *
     * @param userEmailLoginRequest 用户电子邮件登录请求
     * @param request               要求
     * @return {@link UserVO}
     */
    @Override
    public UserVO userEmailLogin(UserEmailLoginRequest userEmailLoginRequest, HttpServletRequest request) {
        String emailAccount = userEmailLoginRequest.getEmailAccount();
        String captcha = userEmailLoginRequest.getCaptcha();

        validateEmailAndCaptcha(emailAccount, captcha);
        // 查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", emailAccount);
        User user = userMapper.selectOne(queryWrapper);

        // 用户不存在
        if (user == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "该邮箱未绑定账号，请先绑定账号");
        }

        if (user.getStatus().equals(UserAccountStatusEnum.BAN.getValue())) {
            throw new BusinessException(ErrorCode.PROHIBITED);
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        // 3. 记录用户的登录态
        request.getSession().setAttribute(USER_LOGIN_STATE, userVO);
        return userVO;
    }

    /**
     * 绑定邮箱
     * @param userBindEmailRequest 绑定请求类
     * @return UserVO
     */
    @Override
    public UserVO userBindEmail(UserBindEmailRequest userBindEmailRequest, HttpServletRequest request) {
        String emailAccount = userBindEmailRequest.getEmailAccount();
        String captcha = userBindEmailRequest.getCaptcha();
        validateEmailAndCaptcha(emailAccount, captcha);

        // 查询用户是否绑定该邮箱
        User loginUser = this.getLoginUser(request);
        if (!loginUser.getEmail().isEmpty() || emailAccount.equals(loginUser.getEmail())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "该账号已绑定邮箱,请先解绑邮箱！");
        }
        // 查询邮箱是否已经绑定
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", emailAccount);
        User user = this.getOne(queryWrapper);
        if (user != null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "此邮箱已被绑定,请更换新的邮箱！");
        }
        loginUser.setEmail(emailAccount);
        boolean bindEmailResult = this.updateById(loginUser);
        if (!bindEmailResult) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "邮箱绑定失败,请稍后再试！");
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(loginUser, userVO);
        return userVO;
    }

    /**
     * 解绑邮箱
     * @param userUnBindEmailRequest 解绑请求类
     * @return UserVO
     */
    @Override
    public UserVO userUnBindEmail(UserUnBindEmailRequest userUnBindEmailRequest, HttpServletRequest request) {
        String emailAccount = userUnBindEmailRequest.getEmailAccount();
        String captcha = userUnBindEmailRequest.getCaptcha();
        validateEmailAndCaptcha(emailAccount, captcha);

        // 查询用户是否绑定该邮箱
        User user = this.getLoginUser(request);
        if (user.getEmail() == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "该账号未绑定邮箱");
        }
        user.setEmail("");
        boolean bindEmailResult = this.updateById(user);
        if (!bindEmailResult) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "邮箱解绑失败,请稍后再试！");
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }

    @Override
    public UserVO updateVoucher(User user) {
        String accessKey = DigestUtil.md5Hex(SALT + user.getUserAccount() + RandomUtil.randomNumbers(5));
        String secretKey = DigestUtil.md5Hex(SALT + user.getUserAccount() + RandomUtil.randomNumbers(8));
        user.setAccessKey(accessKey);
        user.setSecretKey(secretKey);
        boolean result = this.updateById(user);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }

    /**
     * 验证邮箱与验证码
     * @param emailAccount 邮箱帐号
     * @param captcha 验证码
     */
    private void validateEmailAndCaptcha(String emailAccount, String captcha) {
        if (StringUtils.isAnyBlank(emailAccount, captcha)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        if (!Pattern.matches(emailPattern, emailAccount)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "不合法的邮箱地址！");
        }
        String cacheCaptcha = stringRedisTemplate.opsForValue().get(CAPTCHA_CACHE_KEY + emailAccount);
        if (StringUtils.isBlank(cacheCaptcha)) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "验证码已过期,请重新获取");
        }
        captcha = captcha.trim();
        if (!cacheCaptcha.equals(captcha)) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "验证码输入有误");
        }
    }

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    @Override
    public User getLoginUser(HttpServletRequest request) {
        // 先判断是否已登录
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null || currentUser.getId() == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        // 从数据库查询（追求性能的话可以注释，直接走缓存）
        long userId = currentUser.getId();
        currentUser = this.getById(userId);
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return currentUser;
    }

    /**
     * 是否为管理员
     *
     * @param request
     * @return
     */
    @Override
    public boolean isAdmin(HttpServletRequest request) {
        // 仅管理员可查询
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObj;
        return user != null && ADMIN_ROLE.equals(user.getUserRole());
    }

    /**
     * 用户注销
     *
     * @param request
     */
    @Override
    public boolean userLogout(HttpServletRequest request) {
        if (request.getSession().getAttribute(USER_LOGIN_STATE) == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "未登录");
        }
        // 移除登录态
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return true;
    }

    /**
     * 获取验证码
     * @param userEmail 邮箱帐号
     * @return boolean
     */
    @Override
    public boolean getCaptcha(String userEmail) {
        String emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        if (!Pattern.matches(emailPattern, userEmail)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "不合法的邮箱地址！");
        }
        stringRedisTemplate.delete(CAPTCHA_CACHE_KEY + userEmail);
        String captcha = RandomUtil.randomNumbers(6);
        RLock lock = redissonClient.getLock("captcha:" + userEmail);
        try {
            // 尝试获取锁，等待时间为5秒
            if (lock.tryLock(5, TimeUnit.SECONDS)) {
                try {
                    sendEmail(userEmail, captcha);
                    stringRedisTemplate.opsForValue().set(CAPTCHA_CACHE_KEY + userEmail, captcha, 5, TimeUnit.MINUTES);
                    return true;
                } catch (Exception e) {
                    throw new BusinessException(ErrorCode.OPERATION_ERROR, "验证码获取失败");
                } finally {
                    lock.unlock();
                }
            } else {
                log.warn("无法获取锁，可能正在处理其他请求");
                return false;
            }
        } catch (Exception e) {
            log.error("【发送验证码失败】{}", e.getMessage());
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "验证码获取失败");
        }
    }

    /**
     * 发送邮件
     * @param emailAccount 邮箱
     * @param captcha 验证码
     */
    private void sendEmail(String emailAccount, String captcha) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        // 邮箱发送内容组成
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setSubject(EMAIL_SUBJECT);
        helper.setText(buildEmailContent(EMAIL_HTML_CONTENT_PATH, captcha), true);
        helper.setTo(emailAccount);
        helper.setFrom(EMAIL_TITLE + '<' + emailConfig.getEmailFrom() + '>');
        mailSender.send(message);
    }
}




