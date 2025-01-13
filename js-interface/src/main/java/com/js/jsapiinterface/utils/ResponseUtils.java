package com.js.jsapiinterface.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.js.jsapicommon.common.ErrorCode;
import com.js.jsapiclientsdk.model.response.ResultResponse;
import com.js.jsapicommon.common.BusinessException;

import java.util.Map;
import static com.js.jsapiinterface.utils.RequestUtils.get;

/**
 * 请求响应返回结果类
 * @author sakisaki
 * @date 2025/1/11 16:18
 */
public class ResponseUtils {

    /**
     * 返回的 JSON 格式的字符串响应内容转换为键值对形式
     * @param response 响应信息
     * @return Map
     */
    public static Map<String, Object> responseToMap(String response) {
        return new Gson().fromJson(response, new TypeToken<Map<String, Object>>() {
        }.getType());
    }

    /**
     * 返回响应结果
     * @param baseUrl 请求路径
     * @param params 请求参数
     * @return ResultResponse
     */
    public static <T> ResultResponse baseResponse(String baseUrl, T params) {
        String response = null;
        try {
            response = get(baseUrl, params);
            // 将响应内容转换为字符串
            Map<String, Object> fromResponse = responseToMap(response);
            boolean success = (boolean) fromResponse.get("success");
            ResultResponse baseResponse = new ResultResponse();
            if (!success) {
                baseResponse.setData(fromResponse);
                return baseResponse;
            }
            fromResponse.remove("success");
            baseResponse.setData(fromResponse);
            return baseResponse;
        } catch (BusinessException e) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "构建url异常");
        }
    }
}
