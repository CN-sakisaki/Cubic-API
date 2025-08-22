package com.saki.apiproject.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 *
 * @author sakisaki
 * @date 2025/2/6 21:20
 */
@Data
public class UserEmailRequest implements Serializable {

    private static final long serialVersionUID = 6059224522563264841L;

    private String emailAccount;
}
