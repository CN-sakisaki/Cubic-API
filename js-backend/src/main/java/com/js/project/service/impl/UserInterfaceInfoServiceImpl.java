package com.js.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.js.jsapicommon.model.entity.UserInterfaceInfo;
import com.js.project.common.ErrorCode;
import com.js.project.exception.BusinessException;
import com.js.project.mapper.InterfaceInfoMapper;
import com.js.project.mapper.UserInterfaceInfoMapper;
import com.js.project.mapper.UserMapper;
import com.js.project.service.UserInterfaceInfoService;

import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author JianShang
 * @version 1.0.0
 * @description 用户接口信息服务实现类
 * @date 2024-09-13 12:09:40
 */
@Service
public class UserInterfaceInfoServiceImpl extends ServiceImpl<UserInterfaceInfoMapper, UserInterfaceInfo>
    implements UserInterfaceInfoService{

    @Override
    public void validUserInterfaceInfo(UserInterfaceInfo userInterfaceInfo, boolean add) {
        if (userInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 创建时，所有参数必须非空
        if (add) {
            if (userInterfaceInfo.getInterfaceInfoId() <= 0 || userInterfaceInfo.getUserId() <= 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口或用户不存在");
            }
        }
    }

    @Override
    public boolean invokeCount(long interfaceInfoId, long userId) {
        if (userId <= 0 || interfaceInfoId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 查询该用户是否已调用过该接口
        QueryWrapper<UserInterfaceInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("interfaceInfoId", interfaceInfoId);
        queryWrapper.eq("userId", userId);
        long userInterfaceInfoCount = this.count(queryWrapper);

        if (userInterfaceInfoCount > 0) {
            UpdateWrapper<UserInterfaceInfo> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("interfaceInfoId", interfaceInfoId);
            updateWrapper.eq("userId", userId);
            updateWrapper.setSql("totalNum = totalNum + 1");
            return this.update(updateWrapper);
        } else {
            UserInterfaceInfo userInterfaceInfo = new UserInterfaceInfo();
            userInterfaceInfo.setUserId(userId);
            userInterfaceInfo.setInterfaceInfoId(interfaceInfoId);
            userInterfaceInfo.setTotalNum(1);
            return this.save(userInterfaceInfo);
        }
    }
}




