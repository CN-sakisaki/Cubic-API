package com.js.project.model.vo;

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
    /**
     * id
     */
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
     * 性别
     */
    private Integer gender;

    /**
     * 用户角色: user, admin
     */
    private String userRole;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;


}