package com.saki.apiproject.service;


import com.baomidou.mybatisplus.extension.service.IService;

import com.saki.apiproject.model.dto.user.UserBindEmailRequest;
import com.saki.apiproject.model.dto.user.UserEmailLoginRequest;
import com.saki.apiproject.model.dto.user.UserUnBindEmailRequest;
import com.saki.apiproject.model.vo.UserVO;
import com.saki.common.model.entity.User;


import javax.servlet.http.HttpServletRequest;

/**
 * @author JianShang
 * @version 1.0.0
 * @description 用户服务
 * @date 2024-09-13 12:08:58
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @return 新用户 id
     */
    long userRegister(String userAccount, String email, String userPassword, String checkPassword);

    /**
     * 用户登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @param request
     * @return 脱敏后的用户信息
     */
    UserVO userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 用户电子邮件登录
     *
     * @param userEmailLoginRequest 用户电子邮件登录请求
     * @param request               要求
     * @return {@link UserVO}
     */
    UserVO userEmailLogin(UserEmailLoginRequest userEmailLoginRequest, HttpServletRequest request);

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 是否为管理员
     *
     * @param request
     * @return
     */
    boolean isAdmin(HttpServletRequest request);

    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    boolean userLogout(HttpServletRequest request);

    /**
     * 获取验证码
     * @param userEmail
     * @return boolean
     */
    boolean getCaptcha(String userEmail);

    UserVO userBindEmail(UserBindEmailRequest userBindEmailRequest, HttpServletRequest request);

    UserVO userUnBindEmail(UserUnBindEmailRequest userUnBindEmailRequest, HttpServletRequest request);

    /**
     * 更新凭证
     * @param user
     * @return UserVO
     */
    UserVO updateVoucher(User user);
}
