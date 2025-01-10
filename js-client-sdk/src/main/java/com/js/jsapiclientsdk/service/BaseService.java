package com.js.jsapiclientsdk.service;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.js.jsapiclientsdk.client.JsApiClient;
import com.js.jsapiclientsdk.exception.ApiException;
import com.js.jsapiclientsdk.exception.ErrorCode;
import com.js.jsapiclientsdk.exception.ErrorResponse;
import com.js.jsapiclientsdk.model.request.BaseRequest;
import com.js.jsapiclientsdk.model.response.ResultResponse;
import com.js.jsapiclientsdk.utils.SignUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 公共功能抽象，抽象出多个服务类共有的功能和行为
 * @author sakisaki
 * @date 2024/12/18 10:53
 */
@Slf4j
@Data
public abstract class BaseService implements ApiService{
    private JsApiClient jsApiClient;
    /**
     * 网关HOST
     */
    private String gatewayHost = "http://localhost:8090";

    @Override
    public <O, T extends ResultResponse> T request(JsApiClient jsApiClient, BaseRequest<O, T> request) throws ApiException {
        try {
            checkConfig(jsApiClient);
            return res(request);
        } catch (Exception e) {
            throw new ApiException(ErrorCode.OPERATION_ERROR, e.getMessage());
        }
    }

    @Override
    public <O, T extends ResultResponse> T request(BaseRequest<O, T> request) throws ApiException {
        return res(request);
    }

    /**
     * 检查配置
     * @param jsApiClient JsApi客户端
     * @throws ApiException 自定义异常
     */
    private void checkConfig(JsApiClient jsApiClient) throws ApiException {
        if (this.jsApiClient == null && this.getJsApiClient() == null) {
            throw new ApiException(ErrorCode.NO_AUTH_ERROR, "请先配置密钥AccessKey/SecretKey");
        }
        if (jsApiClient != null && !StringUtils.isAnyBlank(jsApiClient.getAccessKey(), jsApiClient.getSecretKey())) {
            this.setJsApiClient(jsApiClient);
        }
    }

    /**
     * 拼接Get请求
     *
     * @param request 请求
     * @param path    路径
     * @return {@link String}
     */
    private <O, T extends ResultResponse> String splicingGetRequest(BaseRequest<O, T> request, String path) {
        StringBuilder urlBuilder = new StringBuilder(gatewayHost);
        // urlBuilder最后是/结尾且path以/开头的情况下，去掉urlBuilder结尾的/
        if (urlBuilder.toString().endsWith("/") && path.startsWith("/")) {
            urlBuilder.setLength(urlBuilder.length() - 1);
        }
        urlBuilder.append(path);
        if (!request.getRequestParams().isEmpty()) {
            urlBuilder.append("?");
            for (Map.Entry<String, Object> entry : request.getRequestParams().entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue().toString();
                urlBuilder.append(key).append("=").append(value).append("&");
            }
            // 移除最后的“&”
            urlBuilder.deleteCharAt(urlBuilder.length() - 1);
        }
        log.info("GET请求路径：{}", urlBuilder);
        return urlBuilder.toString();
    }

    /**
     * 获取请求头
     *
     * @param body        请求体
     * @param jsApiClient jsApi客户端
     * @return {@link Map}<{@link String}, {@link String}>
     */
    private Map<String, String> getHeaders(String body, JsApiClient jsApiClient) {
        Map<String, String> hashMap = new HashMap<>(4);
        hashMap.put("accessKey", jsApiClient.getAccessKey());
        String encodedBody = SecureUtil.md5(body);
        hashMap.put("body", encodedBody);
        hashMap.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000));
        hashMap.put("sign", SignUtils.getSign(encodedBody, jsApiClient.getSecretKey()));
        return hashMap;
    }

    /**
     * 通过请求方法获取http响应
     *
     * @param request 要求
     * @return {@link HttpResponse}
     * @throws ApiException 自定义异常
     */
    private <O, T extends ResultResponse> HttpRequest getHttpRequestByRequestMethod(BaseRequest<O, T> request) throws ApiException {
        if (ObjectUtils.isEmpty(request)) {
            throw new ApiException(ErrorCode.OPERATION_ERROR, "请求参数错误");
        }
        String path = request.getPath().trim();
        String method = request.getMethod().trim().toUpperCase();

        if (ObjectUtils.isEmpty(method)) {
            throw new ApiException(ErrorCode.OPERATION_ERROR, "请求方法不存在");
        }
        if (StringUtils.isBlank(path)) {
            throw new ApiException(ErrorCode.OPERATION_ERROR, "请求路径不存在");
        }

        if (path.startsWith(gatewayHost)) {
            path = path.substring(gatewayHost.length());
        }
        log.info("请求方法：{}，请求路径：{}，请求参数：{}", method, path, request.getRequestParams());
        HttpRequest httpRequest;
        switch (method) {
            case "GET": {
                httpRequest = HttpRequest.get(splicingGetRequest(request, path));
                break;
            }
            case "POST": {
                httpRequest = HttpRequest.post(gatewayHost + path);
                break;
            }
            default: {
                throw new ApiException(ErrorCode.OPERATION_ERROR, "不支持该请求");
            }
        }
        return httpRequest.addHeaders(getHeaders(JSONUtil.toJsonStr(request), jsApiClient)).body(JSONUtil.toJsonStr(request.getRequestParams()));
    }

    /**
     * 执行请求
     *
     * @param request 请求
     * @return {@link HttpResponse}
     * @throws ApiException 自定义异常
     */
    private <O, T extends ResultResponse> HttpResponse doRequest(BaseRequest<O, T> request) throws ApiException {
        try (HttpResponse httpResponse = getHttpRequestByRequestMethod(request).execute()) {
            return httpResponse;
        } catch (Exception e) {
            throw new ApiException(ErrorCode.OPERATION_ERROR, e.getMessage());
        }
    }

    /**
     * 获取响应数据
     *
     * @param request 要求
     * @return {@link T}
     * @throws ApiException 业务异常
     */
    private  <O, T extends ResultResponse> T res(BaseRequest<O, T> request) throws ApiException {
        if (jsApiClient == null || StringUtils.isAnyBlank(jsApiClient.getAccessKey(), jsApiClient.getSecretKey())) {
            throw new ApiException(ErrorCode.NO_AUTH_ERROR, "请先配置密钥AccessKey/SecretKey");
        }
        T rsp;
        try {
            Class<T> clazz = request.getResponseClass();
            rsp = clazz.newInstance();
        } catch (Exception e) {
            throw new ApiException(ErrorCode.OPERATION_ERROR, e.getMessage());
        }
        HttpResponse httpResponse = doRequest(request);
        String body = httpResponse.body();
        Map<String, Object> data = new HashMap<>();
        if (httpResponse.getStatus() != 200) {
            // 将错误信息 转化为 具体对象类
            ErrorResponse errorResponse = JSONUtil.toBean(body, ErrorResponse.class);
            data.put("errorMessage", errorResponse.getMessage());
            data.put("code", errorResponse.getCode());
        } else {
            try {
                // 尝试解析为JSON对象
                data = new Gson().fromJson(body, new TypeToken<Map<String, Object>>() {
                }.getType());
            } catch (JsonSyntaxException e) {
                // 解析失败，将body作为普通字符串处理
                data.put("value", body);
            }
        }
        rsp.setData(data);
        return rsp;
    }
}
