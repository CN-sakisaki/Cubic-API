package com.saki.apiproject.model.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author sakisaki
 * @date 2025/2/15 22:50
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    /**
     * id
     */
    private Long userId;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户角色
     */
    private String userRole;
}
