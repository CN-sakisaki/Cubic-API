package com.js.project.controller;

import cn.hutool.core.io.FileUtil;
import com.js.jsapicommon.model.entity.User;
import com.js.project.common.BaseResponse;
import com.js.project.common.ErrorCode;
import com.js.project.common.ResultUtils;
import com.js.project.config.CosClientConfig;
import com.js.project.manager.CosManager;
import com.js.project.model.enums.FileUploadBizEnum;
import com.js.project.model.enums.ImageStatusEnum;
import com.js.project.model.file.UploadFileRequest;
import com.js.project.model.vo.ImageVo;
import com.js.project.service.UserService;
import com.qcloud.cos.model.PutObjectResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.Arrays;

/**
 * 文件操作
 * @author sakisaki
 * @date 2025/2/7 17:18
 */
@RestController
@RequestMapping("/file")
@Slf4j
public class FileController {
    private static final long ONE_M = 2 * 1024 * 1024L;
    @Resource
    private UserService userService;
    @Resource
    private CosManager cosManager;
    @Resource
    private CosClientConfig cosClientConfig;

    /**
     * 上传文件
     *
     * @param multipartFile     多部分文件
     * @param uploadFileRequest 上传文件请求
     * @param request           请求
     * @return {@link BaseResponse}<{@link ImageVo}>
     */
    @PostMapping("/upload")
    public BaseResponse<ImageVo> uploadFile(@RequestPart("file") MultipartFile multipartFile, UploadFileRequest uploadFileRequest, HttpServletRequest request) {
        String biz = uploadFileRequest.getBiz();
        FileUploadBizEnum fileUploadBizEnum = FileUploadBizEnum.getEnumByValue(biz);
        ImageVo imageVo = new ImageVo();
        if (fileUploadBizEnum == null) {
            return uploadError(imageVo, multipartFile, "上传失败,情重试.");
        }
        String result = validFile(multipartFile, fileUploadBizEnum);
        if (!"success".equals(result)) {
            return uploadError(imageVo, multipartFile, result);
        }
        User loginUser = userService.getLoginUser(request);
        // 文件目录：根据业务、用户来划分
        String uuid = RandomStringUtils.randomAlphanumeric(8);
        String filename = uuid + "-" + multipartFile.getOriginalFilename();
        String filepath = String.format("/%s/%s/%s", fileUploadBizEnum.getValue(), loginUser.getId(), filename);
        File file = null;

        try {
            // 上传文件
            file = File.createTempFile(filepath, null);
            multipartFile.transferTo(file);
            cosManager.putObject(filepath, file);
            imageVo.setName(multipartFile.getOriginalFilename());
            imageVo.setUid(RandomStringUtils.randomAlphanumeric(8));
            imageVo.setStatus(ImageStatusEnum.SUCCESS.getValue());
            imageVo.setUrl(cosClientConfig.getCosHost() + filepath);
            // 返回可访问地址
            return ResultUtils.success(imageVo);
        } catch (Exception e) {
            log.error("file upload error, filepath = {}", filepath, e);
            return uploadError(imageVo, multipartFile, "上传失败,情重试");
        } finally {
            if (file != null) {
                // 删除临时文件
                boolean delete = file.delete();
                if (!delete) {
                    log.error("file delete error, filepath = {}", filepath);
                }
            }
        }
    }

    private BaseResponse<ImageVo> uploadError(ImageVo imageVo, MultipartFile multipartFile, String message) {
        imageVo.setName(multipartFile.getOriginalFilename());
        imageVo.setUid(RandomStringUtils.randomAlphanumeric(8));
        imageVo.setStatus(ImageStatusEnum.ERROR.getValue());
        return ResultUtils.error(imageVo, ErrorCode.OPERATION_ERROR, message);
    }

    /**
     * 有效文件
     * 校验文件
     *
     * @param fileUploadBizEnum 业务类型
     * @param multipartFile     多部分文件
     */
    private String validFile(MultipartFile multipartFile, FileUploadBizEnum fileUploadBizEnum) {
        // 文件大小
        long fileSize = multipartFile.getSize();
        // 文件后缀
        String fileSuffix = FileUtil.getSuffix(multipartFile.getOriginalFilename());
        if (FileUploadBizEnum.USER_AVATAR.equals(fileUploadBizEnum)) {
            if (fileSize > ONE_M) {
                return "文件大小不能超过 1M";
            }
            if (!Arrays.asList("jpeg", "jpg", "svg", "png", "webp", "jfif").contains(fileSuffix)) {
                return "文件类型错误";
            }
        }
        return "success";
    }
}
