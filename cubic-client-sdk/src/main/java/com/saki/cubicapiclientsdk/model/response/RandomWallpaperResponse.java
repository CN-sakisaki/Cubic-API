package com.saki.cubicapiclientsdk.model.response;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 随机壁纸请求响应
 * @author sakisaki
 * @date 2025/1/11 17:43
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RandomWallpaperResponse extends ResultResponse {
    private static final long serialVersionUID = -6467312483425078539L;
    private String imgurl;
    private String width;
    private String height;
}
