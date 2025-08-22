package com.saki.cubicapiclientsdk.model.param;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 获取名字请求参数
 * @author sakisaki
 * @date 2025/1/11 17:28
 */
@Data
@Accessors(chain = true)
public class NameParams implements Serializable {
    private static final long serialVersionUID = 2439946342316624126L;
    private String name;
}
