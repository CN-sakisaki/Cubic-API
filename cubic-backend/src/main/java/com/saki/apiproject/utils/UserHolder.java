package com.saki.apiproject.utils;


import com.saki.apiproject.model.dto.user.UserDTO;

/**
 *
 * @author sakisaki
 * @date 2025/2/15 22:51
 */
public class UserHolder {

    private static final ThreadLocal<UserDTO> THREAD_LOCAL = new ThreadLocal<>();

    public static UserDTO getThreadLocal() {
        try {
            return THREAD_LOCAL.get();
        } catch (Exception e) {
            // 记录日志或进行其他处理
            e.printStackTrace();
            return null;
        }
    }

    public static void setThreadLocal(UserDTO userDTO) {
        try {
            THREAD_LOCAL.set(userDTO);
        } catch (Exception e) {
            // 记录日志或进行其他处理
            e.printStackTrace();
        }
    }

    public static void removeUser() {
        THREAD_LOCAL.remove();
    }
}
