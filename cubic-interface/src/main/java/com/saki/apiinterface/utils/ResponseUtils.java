package com.saki.apiinterface.utils;

import cn.hutool.json.JSONObject;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.saki.cubicapiclientsdk.model.response.ResultResponse;
import com.saki.common.common.BusinessException;
import com.saki.common.common.ErrorCode;


import java.util.Map;

import static com.saki.apiinterface.utils.RequestUtils.get;


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

    /**
     * 返回响应结果
     * @param param 请求参数
     * @return ResultResponse
     */
    public static ResultResponse baseResponse(String param) {
        ResultResponse resultResponse = new ResultResponse();
        try {
            String response = get(param);
            Map<String, Object> fromResponse = responseToMap(response);
            JSONObject jsonResponse = new JSONObject(response);
            int status = jsonResponse.getInt("status");
            if (!(status == 200)) {
                resultResponse.setData(fromResponse);
                return resultResponse;
            }
            fromResponse.remove("message");
            resultResponse.setData(fromResponse);
            return resultResponse;
        } catch (BusinessException e) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "请求错误");
        }
    }
}
