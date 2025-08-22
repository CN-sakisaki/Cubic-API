package com.saki.cubicapiclientsdk.model.param;

import lombok.Data;

import java.io.Serializable;

/**
 * 指定壁纸参数
 * @author sakisaki
 * @date 2025/2/5 23:27
 */
@Data
public class AppointWallpaperParams implements Serializable {

    private static final long serialVersionUID = -6393883784131705915L;

    private String content;
}
