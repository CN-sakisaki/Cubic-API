package com.saki.apiproject.service.impl;// package com.js.project.service.impl;
//
//
// import com.js.jsapicommon.model.entity.User;
// import com.js.project.common.ErrorCode;
// import com.js.project.exception.BusinessException;
// import com.js.project.service.JwtTokensService;
// import io.jsonwebtoken.Claims;
// import io.jsonwebtoken.Jwts;
// import io.jsonwebtoken.SignatureAlgorithm;
// import io.jsonwebtoken.security.Keys;
// import org.springframework.data.redis.core.StringRedisTemplate;
// import org.springframework.stereotype.Service;
//
// import javax.crypto.SecretKey;
// import java.util.Date;
// import java.util.concurrent.TimeUnit;
//
// import static com.js.project.constant.JwtConstant.*;
//
//
// /**
//  * JWT服务实现类
//  * @author sakisaki
//  * @date 2025/2/15 17:32
//  */
// @Service
// public class JwtTokensServiceImpl implements JwtTokensService {
//
//     private final StringRedisTemplate stringRedisTemplate;
//
//     public JwtTokensServiceImpl(StringRedisTemplate stringRedisTemplate) {
//         this.stringRedisTemplate = stringRedisTemplate;
//     }
//
//     /**
//      * 生成用户的访问令牌（JWT），并将其存储到 Redis 中
//      *
//      * @param user 包含用户信息的 User 对象
//      * @return 生成的访问令牌字符串
//      */
//     @Override
//     public String generateAccessToken(User user) {
//         Date now = new Date();
//         // 计算访问令牌的过期时间，通过当前时间加上预先定义的过期时长
//         Date expirationDate = new Date(now.getTime() + EXPIRATION_TIME);
//         return getToken(user, now, expirationDate);
//     }
//
//     /**
//      * 生成 Refresh Token，用于向服务器请求新的访问令牌
//      * @param user 用户信息
//      * @return String
//      */
//     @Override
//     public String generateRefreshToken(User user) {
//         Date now = new Date();
//         // 计算访问令牌的过期时间，通过当前时间加上预先定义的过期时长
//         Date expirationDate = new Date(now.getTime() + EXPIRATION_TIME * 10);
//         return getToken(user, now, expirationDate);
//     }
//
//     /**
//      *
//      * @param user 用户信息
//      * @param now 当前时间
//      * @param expirationDate 有效期时间
//      * @return String
//      */
//     private String getToken(User user, Date now, Date expirationDate) {
//         // 创建 JWT 的声明对象，并设置主题为用户的 ID
//         Claims claims = Jwts.claims().setSubject(String.valueOf(user.getId()));
//         claims.put("userName", user.getUserName());
//         claims.put("userRole", user.getUserRole());
//         claims.put("userAvatar", user.getUserAvatar());
//         // Map<String, Object> claims = new HashMap<>();
//         // claims.put("id", String.valueOf(threadLocal.getId()));
//
//         // 将字符串类型的密钥转换为 SecretKey 类型，使用 HMAC-SHA 算法生成密钥
//         SecretKey key = getSecretKey();
//         // 构建 JWT 对象
//         String accessToken = Jwts.builder()
//                 // 设置 JWT 的声明信息
//                 .setClaims(claims)
//                 // 设置 JWT 的签发时间为当前时间
//                 .setIssuedAt(now)
//                 // 设置 JWT 的过期时间
//                 .setExpiration(expirationDate)
//                 // 使用生成的 SecretKey 和指定的签名算法（HS256）对 JWT 进行签名
//                 .signWith(key, SignatureAlgorithm.HS256)
//                 // 将 JWT 各个部分组合成一个完整的字符串
//                 .compact();
//
//         // 构建存储在 Redis 中的键，使用预先定义的前缀加上用户 ID
//         String redisKey = ACCESS_TOKEN_PREFIX + user.getId();
//         // 将生成的访问令牌存储到 Redis 中，并设置过期时间
//         stringRedisTemplate.opsForValue().set(redisKey, accessToken, EXPIRATION_TIME, TimeUnit.SECONDS);
//         return accessToken;
//     }
//
//
//     @Override
//     public User validateToken(String token) {
//         try {
//             SecretKey key = getSecretKey();
//             // 使用 parserBuilder() 方法构建解析器
//             Claims claims = getClaims(token, key);
//
//             String userId = getUserIdFromToken(token);
//             String userName = (String) claims.get("userName");
//             String userAvatar = (String) claims.get("userAvatar");
//             String userRole = (String) claims.get("userRole");
//             String storedToken = stringRedisTemplate.opsForValue().get(key);
//
//             if (storedToken != null && storedToken.equals(token)) {
//                 // 如果Redis中存储的令牌与传入的令牌匹配，则验证通过
//                 User user = new User();
//                 user.setId(Long.parseLong(userId));
//                 user.setUserName(userName);
//                 user.setUserAvatar(userAvatar);
//                 user.setUserRole(userRole);
//                 return user;
//             }
//         } catch (Exception e) {
//             throw new BusinessException(ErrorCode.OPERATION_ERROR, "解析失败" + e.getMessage());
//         }
//         return null;
//     }
//
//     /**
//      * 从 JWT 令牌中获取用户 ID
//      *
//      * @param token JWT 令牌
//      * @return 用户 ID
//      */
//     @Override
//     public String getUserIdFromToken(String token) {
//         // 将字符串类型的密钥转换为 SecretKey 类型
//         SecretKey key = getSecretKey();
//         // 使用 parserBuilder() 方法构建解析器
//         Claims claims = getClaims(token, key);
//         // 返回 JWT 主题，通常为用户 ID
//         return claims.getSubject();
//     }
//
//
//     /**
//      * 判断 JWT 令牌是否已过期
//      *
//      * @param token JWT 令牌
//      * @return 如果令牌已过期返回 true，否则返回 false
//      */
//     @Override
//     public boolean isTokenExpired(String token) {
//         // 将字符串类型的密钥转换为 SecretKey 类型
//         SecretKey key = getSecretKey();
//         Claims claims = getClaims(token, key);
//         // 获取令牌的过期时间
//         Date expirationDate = claims.getExpiration();
//         // 判断过期时间是否在当前时间之前
//         return expirationDate.before(new Date());
//     }
//
//
//     /**
//      *
//      * @param token 令牌
//      * @param key 密钥
//      * @return Claims
//      */
//     private static Claims getClaims(String token, SecretKey key) {
//         Claims claims = Jwts.parserBuilder()
//                 // 设置签名密钥
//                 .setSigningKey(key)
//                 .build()
//                 // 解析 JWT 令牌
//                 .parseClaimsJws(token)
//                 // 获取 JWT 的负载部分（Claims）
//                 .getBody();
//         return claims;
//     }
//
//     /**
//      * 将字符串类型的密钥转换为 SecretKey 类型
//      */
//     private static SecretKey getSecretKey() {
//         SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
//         return key;
//     }
// }
//
//
//
//
