package com.saki.apiproject.common;


import lombok.Data;

import java.io.Serializable;

/**
 * @author JianShang
 * @version 1.0.0
 * @description 删除请求
 * @date 2024-09-13 12:02:12
 */
@Data
public class IdRequest implements Serializable {
    /**
     * id
     */
    private Long id;

    private static final long serialVersionUID = 1L;
}