package com.saki.apiproject.model.dto.jwt;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;


/**
 * 仅仅用于判断token是否过期 ,具体的通过token查询数据是通过jwtUtils来解密token
 * @author sakisaki
 * @date 2025/2/15 17:24
 */
@Data
@AllArgsConstructor
public class JwtToken implements Serializable {
    private static final long serialVersionUID = 3919174857151260003L;
    private String token;
    private String refreshToken;
}