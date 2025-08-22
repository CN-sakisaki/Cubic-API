package com.saki.apiproject.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * @author JianShang
 * @version 1.0.0
 * @description 用户注册请求体
 * @date 2024-09-13 12:11:55
 */
@Data
public class UserRegisterRequest implements Serializable {

    private static final long serialVersionUID = 3191241716373120793L;

    private String userAccount;

    private String userPassword;

    private String checkPassword;

    private String email;
}
