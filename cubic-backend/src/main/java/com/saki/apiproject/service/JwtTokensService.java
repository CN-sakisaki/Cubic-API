package com.saki.apiproject.service;// package com.js.project.service;
//
//
// import com.js.jsapicommon.model.entity.User;
// import com.js.project.model.dto.jwt.JwtToken;
//
// /**
//  * JWT令牌接口服务
//  * @author sakisaki
//  * @date 2025/2/15 17:20
//  */
// public interface JwtTokensService {
//
//
//     /**
//      * 生成JWT访问token
//      * @param user
//      * @return
//      */
//     String generateAccessToken(User user);
//
//
//     /**
//      * 生成refreshToken
//      * @param user
//      * @return
//      */
//     String generateRefreshToken(User user);
//
//
//     /**
//      * 验证token
//      *
//      * @param token
//      * @return
//      */
//     User validateToken(String token);
//
//     /**
//      * 获取令牌中的用户id
//      * @param token
//      * @return
//      */
//     String getUserIdFromToken(String token);
//
//     /**
//      * 验证token是否过期
//      * @param token
//      * @return
//      */
//     boolean isTokenExpired(String token);
//
//
//     // /**
//     //  * 保存token到redis
//     //  * @param jwtToken
//     //  * @param threadLocal
//     //  */
//     // void save2Redis(JwtToken jwtToken, User threadLocal);
// }
