package com.saki.apiproject.service.impl.inner;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.saki.apiproject.mapper.UserMapper;
import com.saki.common.common.BusinessException;
import com.saki.common.common.ErrorCode;
import com.saki.common.model.entity.User;
import com.saki.common.service.InnerUserService;
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
