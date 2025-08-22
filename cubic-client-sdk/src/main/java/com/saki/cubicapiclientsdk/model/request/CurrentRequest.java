package com.saki.cubicapiclientsdk.model.request;

import com.saki.cubicapiclientsdk.model.response.ResultResponse;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 封装业务的请求信息
 * @author sakisaki
 * @date 2024/12/17 23:37
 */
@Accessors(chain = true)
@Setter
public class CurrentRequest extends BaseRequest<Object, ResultResponse> {
    /**
     * 请求类型
     */
    private String method;
    /**
     * 请求路径
     */
    private String path;

    /**
     * 重写BaseRequest中获取请求类型的方法
     * @return String
     */
    @Override
    public String getMethod() {
        return method;
    }

    /**
     * 重写BaseRequest中获取请求路径的方法
     * @return String
     */
    @Override
    public String getPath() {
        return path;
    }

    /**
     * 重写BaseRequest中获取响应类方法
     * @return Class<ResultResponse>
     */
    @Override
    public Class<ResultResponse> getResponseClass() {
        return ResultResponse.class;
    }
}
