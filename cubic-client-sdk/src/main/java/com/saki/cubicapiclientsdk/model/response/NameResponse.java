package com.saki.cubicapiclientsdk.model.response;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 获取名字响应类
 * @author sakisaki
 * @date 2025/1/11 17:33
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class NameResponse extends ResultResponse {
    private static final long serialVersionUID = -4146085473250339916L;
    private String name;
}
