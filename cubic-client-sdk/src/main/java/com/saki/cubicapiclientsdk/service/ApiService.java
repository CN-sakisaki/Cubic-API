package com.saki.cubicapiclientsdk.service;

import com.saki.cubicapiclientsdk.client.CubicApiClient;
import com.saki.cubicapiclientsdk.exception.ApiException;
import com.saki.cubicapiclientsdk.model.request.BaseRequest;
import com.saki.cubicapiclientsdk.model.response.ResultResponse;

/**
 * 请求接口
 * @author sakisaki
 * @date 2024/12/19 23:25
 */
public interface ApiService {

    /**
     * 通用请求
     *
     * @param cubicApiClient api客户端
     * @param request     要求
     * @return {@link T}
     * @throws ApiException 业务异常
     */
    <O, T extends ResultResponse> T request(CubicApiClient cubicApiClient, BaseRequest<O, T> request) throws ApiException;

}
