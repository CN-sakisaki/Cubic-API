package com.saki.apiproject.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author JianShang
 * @version 1.0.0
 * @description 用户视图
 * @date 2024-09-13 12:10:34
 */
@Data
public class UserVO implements Serializable {

    private static final long serialVersionUID = -4558534873206234257L;

    private Long id;

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
    private String gender;

    /**
     * 用户角色: user, admin
     */
    private String userRole;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * accessKey
     */
    private String accessKey;

    /**
     * secretKey
     */
    private String secretKey;

    /**
     * 帐号状态（0-正常 1-封号）
     */
    private Integer status;

    /**
     * 钱包余额,注册送30币
     */
    private Long balance;

    /**
     * 邀请码
     */
    private String invitationCode;


}