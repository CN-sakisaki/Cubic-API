package com.saki.apiproject.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户绑定电子邮件请求
 * @author sakisaki
 * @date 2025/2/7 15:02
 */
@Data
public class UserBindEmailRequest implements Serializable {

    private static final long serialVersionUID = 3191241716373120793L;

    private String emailAccount;

    private String captcha;
}
