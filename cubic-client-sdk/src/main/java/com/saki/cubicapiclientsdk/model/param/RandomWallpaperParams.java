package com.saki.cubicapiclientsdk.model.param;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 随机壁纸参数
 * @author sakisaki
 * @date 2025/1/11 17:41
 */
@Data
@Accessors(chain = true)
public class RandomWallpaperParams implements Serializable {

    private static final long serialVersionUID = 3148772152095414606L;
    private String lx;
    private String method;
}
