package com.saki.apiproject.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * @author JianShang
 * @version 1.0.0
 * @description 用户创建请求
 * @date 2024-09-13 12:12:17
 */
@Data
public class UserAddRequest implements Serializable {

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 性别
     */
    private Integer gender;

    /**
     * 用户角色: user, admin
     */
    private String userRole;

    /**
     * 密码
     */
    private String userPassword;

    private static final long serialVersionUID = 1L;
}