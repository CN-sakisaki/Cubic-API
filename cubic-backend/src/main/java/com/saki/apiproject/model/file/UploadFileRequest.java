package com.saki.apiproject.model.file;

import lombok.Data;

import java.io.Serializable;

/**
 * 文件上传请求
 * @author sakisaki
 * @date 2025/2/7 16:30
 */
@Data
public class UploadFileRequest implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 业务
     */
    private String biz;
}