package com.saki.apiproject.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户登录请求体
 * @author sakisaki
 * @date 2025/2/7 00:34
 */
@Data
public class UserEmailLoginRequest implements Serializable {

    private static final long serialVersionUID = 3191241716373120793L;

    private String emailAccount;

    private String captcha;
}
