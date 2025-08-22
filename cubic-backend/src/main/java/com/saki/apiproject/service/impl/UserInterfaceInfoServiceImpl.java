package com.saki.apiproject.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.saki.apiproject.mapper.UserInterfaceInfoMapper;
import com.saki.apiproject.service.UserInterfaceInfoService;
import com.saki.common.common.BusinessException;
import com.saki.common.common.ErrorCode;
import com.saki.common.model.entity.UserInterfaceInfo;
import org.springframework.stereotype.Service;

/**
 * @author JianShang
 * @version 1.0.0
 * @description 用户接口信息服务实现类
 * @date 2024-09-13 12:09:40
 */
@Service
public class UserInterfaceInfoServiceImpl extends ServiceImpl<UserInterfaceInfoMapper, UserInterfaceInfo>
    implements UserInterfaceInfoService {

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




