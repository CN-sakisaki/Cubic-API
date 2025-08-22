package com.saki.cubicapiclientsdk.model.param;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 获取天气请求参数
 * @author sakisaki
 * @date 2025/1/11 17:48
 */
@Data
@Accessors(chain = true)
public class WeatherParams implements Serializable {

    private static final long serialVersionUID = -6923311147836437943L;
    private String ip;
    private String city;
    private String type;
}
