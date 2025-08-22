package com.saki.apiinterface.utils;

import cn.hutool.http.HttpRequest;

import com.saki.common.common.BusinessException;
import com.saki.common.common.ErrorCode;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;

/**
 * 发起 HTTP GET 请求
 * @author sakisaki
 * @date 2025/1/11 16:50
 */
@Slf4j
public class RequestUtils {

    /**
     * 生成url
     *
     * @param baseUrl 基本url
     * @param params  params
     * @return {@link String}
     */
    public static <T> String buildUrl(String baseUrl, T params) {
        StringBuilder url = new StringBuilder(baseUrl);
        // 获取传入参数对象 params 的所有声明字段（包括私有字段）
        Field[] fields = params.getClass().getDeclaredFields();
        boolean isFirstParam = true;
        for (Field field : fields) {
            // 如果字段是私有的，这个设置可以让后续能正常访问该字段的值，避免出现访问权限问题
            field.setAccessible(true);
            String name = field.getName();
            // 跳过serialVersionUID属性
            if ("serialVersionUID".equals(name)) {
                continue;
            }
            try {
                Object value = field.get(params);
                if (value != null) {
                    if (isFirstParam) {
                        url.append("?").append(name).append("=").append(value);
                        isFirstParam = false;
                    } else {
                        url.append("&").append(name).append("=").append(value);
                    }
                }
            } catch (IllegalAccessException e) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "构建url异常");
            }
        }
        return url.toString();
    }

    /**
     * get请求
     *
     * @param baseUrl 基本url
     * @param params  params
     * @return {@link String}
     */
    public static <T> String get(String baseUrl, T params) throws BusinessException {
        String url = buildUrl(baseUrl, params);
        return get(url);
    }

    /**
     * 执行get请求
     *
     * @param url url
     * @return {@link String}
     */
    public static String get(String url) {
        String body = HttpRequest.get(url).execute().body();
        log.info("【interface】：请求地址：{}，响应数据：{}", url, body);
        return body;
    }
}
