package com.saki.apiproject.common;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author JianShang
 * @version 1.0.0
 * @description 删除请求
 * @date 2024-09-13 12:01:48
 */
@Data
public class DeleteRequest implements Serializable {
    /**
     * id
     */
    private List<Long> idList;

    private static final long serialVersionUID = 1L;
}
