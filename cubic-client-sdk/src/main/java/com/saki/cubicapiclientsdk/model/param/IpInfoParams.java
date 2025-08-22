package com.saki.cubicapiclientsdk.model.param;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 获取ip地址请求参数
 * @author sakisaki
 * @date 2025/1/11 17:47
 */
@Data
@Accessors(chain = true)
public class IpInfoParams implements Serializable {

    private static final long serialVersionUID = -4208462362618082158L;
    private String ip;
}
