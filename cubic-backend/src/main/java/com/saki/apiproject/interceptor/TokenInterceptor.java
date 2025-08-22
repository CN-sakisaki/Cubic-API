package com.saki.apiproject.interceptor;// package com.js.project.interceptor;
//
// import cn.hutool.core.bean.BeanUtil;
// import com.js.jsapicommon.model.entity.User;
// import com.js.project.constant.JwtConstant;
// import com.js.project.exception.BusinessException;
// import com.js.project.model.dto.user.UserDTO;
// import com.js.project.service.JwtTokensService;
// import com.js.project.utils.UserHolder;
// import org.apache.commons.lang3.StringUtils;
// import org.springframework.data.redis.core.StringRedisTemplate;
// import org.springframework.web.servlet.HandlerInterceptor;
// import com.js.project.common.ErrorCode;
//
// import javax.servlet.http.HttpServletRequest;
// import javax.servlet.http.HttpServletResponse;
//
// /**
//  * 请求拦截器，只refresh Token，不拦截请求；
//  * @author sakisaki
//  * @date 2025/2/15 23:01
//  */
// public class TokenInterceptor implements HandlerInterceptor {
//
//     JwtTokensService jwtTokensService;
//
//     StringRedisTemplate stringRedisTemplate;
//
//
//     public TokenInterceptor(StringRedisTemplate stringRedisTemplate, JwtTokensService jwtTokensService){
//         this.jwtTokensService=jwtTokensService;
//         this.stringRedisTemplate=stringRedisTemplate;
//     }
//
//     @Override
//     public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//         //从请求头中获取JWT access_token
//         String token = request.getHeader("Authorization");
//         if (StringUtils.isEmpty(token)) {
//             throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "请先登录~");
//         }
//         try {
//             // 解析并验证JWT token是否合法
//             boolean isTokenExpired = jwtTokensService.isTokenExpired(token);
//             User user = jwtTokensService.validateToken(token);
//             if (isTokenExpired) {
//                 // 如果token过期 , 那么需要通过refresh_token生成一个新的access_token
//                 String refreshTokenKey = JwtConstant.REFRESH_TOKEN_PREFIX + user.getId();
//                 String refreshToken = stringRedisTemplate.opsForValue().get(refreshTokenKey);
//                 if (StringUtils.isEmpty(refreshToken)) {
//                     throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "missing refresh token");
//                 }
//                 if (jwtTokensService.isTokenExpired(refreshToken)) {
//                     throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "超时, 请重新登录");
//                 }
//                 // 生成新的accessToken , 同时保存到redis
//                 String accessToken = jwtTokensService.generateAccessToken(user);
//                 response.setHeader("Authorization", accessToken);
//                 // 更新token这个动作在用户看来是未知的, 更新完之后需要在ThreadLocal中添加UserDTO
//                 UserDTO userDTO = BeanUtil.copyProperties(user, UserDTO.class);
//                 UserHolder.setThreadLocal(userDTO);
//             } else {
//                 // 如果token没有过期, 那么直接添加用户的数据
//                 UserDTO userDTO = BeanUtil.copyProperties(user, UserDTO.class);
//                 UserHolder.setThreadLocal(userDTO);
//             }
//             return true;
//         } catch (Exception e) {
//             throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "token错误, 请重新登录");
//         }
//     }
//
//     @Override
//     public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
//         // 请求处理完成后，清除 ThreadLocal 中的用户信息，避免内存泄漏
//         UserHolder.removeUser();
//     }
// }
