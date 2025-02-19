package com.js.project.service.impl.inner;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.js.jsapicommon.model.entity.User;
import com.js.jsapicommon.service.InnerUserService;
import com.js.project.common.ErrorCode;
import com.js.project.exception.BusinessException;
import com.js.project.mapper.UserMapper;

import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

/**
 * @author JianShang
 * @version 1.0.0
 * @description 内部用户服务实现类
 * @date 2024-09-13 12:10:07
 */
@DubboService
public class InnerUserServiceImpl implements InnerUserService {

    @Resource
    private UserMapper userMapper;

    @Override
    public User getInvokeUser(String accessKey) {
        if (StringUtils.isAnyBlank(accessKey)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("accessKey", accessKey);
        return userMapper.selectOne(queryWrapper);
    }

    @Override
    public void reduceBalance(long reduceScore, long userId, long balance) {
        User user = userMapper.selectById(userId);
        long newBalance = balance - reduceScore;
        user.setBalance(newBalance);
        userMapper.updateById(user);
    }
}
