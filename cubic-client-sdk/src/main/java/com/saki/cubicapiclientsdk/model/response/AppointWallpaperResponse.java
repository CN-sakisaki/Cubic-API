package com.saki.cubicapiclientsdk.model.response;

import lombok.Data;

import java.io.Serializable;

/**
 * 指定壁纸响应类
 * @author sakisaki
 * @date 2025/2/5 23:34
 */
@Data
public class AppointWallpaperResponse implements Serializable {

    private static final long serialVersionUID = -645574753504596830L;

    private String url;

    private Long with;

    private Long height;
}
