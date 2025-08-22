package com.saki.common.service;


import com.saki.common.model.entity.User;

/**
 * @author JianShang
 * @version 1.0.0
 * @description 内部用户服务
 * @date 2024-09-23 09:35:03
 */
public interface InnerUserService {

    /**
     * 数据库中查是否已分配给用户秘钥（accessKey）
     * @param accessKey
     * @return
     */
    User getInvokeUser(String accessKey);

    void reduceBalance(long reduceScore, long userId, long balance);
}
