package com.saki.apiproject.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * 登录响应类
 * @author sakisaki
 * @date 2025/2/15 23:07
 */
@Data
@AllArgsConstructor
public class UserLoginResponse implements Serializable {

    private static final long serialVersionUID = 6936122932108405428L;
    private String token;
    private UserVO userVO;
}
