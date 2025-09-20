package com.saki.apiproject.controller;


import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.saki.apiproject.annotation.AuthCheck;
import com.saki.apiproject.annotation.RedisRateLimiter;
import com.saki.apiproject.cache.InterfaceCacheService;
import com.saki.apiproject.common.DeleteRequest;
import com.saki.apiproject.common.IdRequest;
import com.saki.apiproject.constant.CommonConstant;
import com.saki.apiproject.model.dto.interfaceinfo.InterfaceInfoAddRequest;
import com.saki.apiproject.model.dto.interfaceinfo.InterfaceInfoInvokeRequest;
import com.saki.apiproject.model.dto.interfaceinfo.InterfaceInfoQueryRequest;
import com.saki.apiproject.model.dto.interfaceinfo.InterfaceInfoUpdateRequest;
import com.saki.apiproject.model.enums.InterfaceInfoStatusEnum;
import com.saki.apiproject.service.InterfaceInfoService;
import com.saki.apiproject.service.UserService;
import com.saki.common.common.BaseResponse;
import com.saki.common.common.BusinessException;
import com.saki.common.common.ErrorCode;
import com.saki.common.common.ResultUtils;
import com.saki.common.model.dto.RequestParamsField;
import com.saki.common.model.dto.ResponseParamsField;
import com.saki.common.model.entity.InterfaceInfo;
import com.saki.common.model.entity.User;
import com.saki.cubicapiclientsdk.client.CubicApiClient;
import com.saki.cubicapiclientsdk.factory.CubicApiClientFactory;
import com.saki.cubicapiclientsdk.model.request.CurrentRequest;
import com.saki.cubicapiclientsdk.model.response.ResultResponse;
import com.saki.cubicapiclientsdk.service.ApiService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author JianShang
 * @version 1.0.0
 * @description 接口管理
 * @date 2024-09-13 12:03:49
 */
@RestController
@RequestMapping("/interfaceInfo")
@Slf4j
public class InterfaceInfoController {

    @Resource
    private InterfaceInfoService interfaceInfoService;

    @Resource
    private InterfaceCacheService interfaceCacheService;

    @Resource
    private CubicApiClientFactory cubicApiClientFactory;

    @Resource
    private UserService userService;

    private final Gson gson = new Gson();

    @Resource
    private ApiService apiService;

    // region 增删改查

    /**
     * 创建
     *
     * @param interfaceInfoAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addInterfaceInfo(@RequestBody InterfaceInfoAddRequest interfaceInfoAddRequest, HttpServletRequest request) {
        if (interfaceInfoAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        if (CollectionUtils.isNotEmpty(interfaceInfoAddRequest.getRequestParams())) {
            List<RequestParamsField> requestParamsFields = interfaceInfoAddRequest.getRequestParams().stream().filter(field -> StringUtils.isNotBlank(field.getFieldName())).collect(Collectors.toList());
            String requestParams = JSONUtil.toJsonStr(requestParamsFields);
            interfaceInfo.setRequestParams(requestParams);
        }
        if (CollectionUtils.isNotEmpty(interfaceInfoAddRequest.getResponseParams())) {
            List<ResponseParamsField> responseParamsFields = interfaceInfoAddRequest.getResponseParams().stream().filter(field -> StringUtils.isNotBlank(field.getFieldName())).collect(Collectors.toList());
            String responseParams = JSONUtil.toJsonStr(responseParamsFields);
            interfaceInfo.setResponseParams(responseParams);
        }
        BeanUtils.copyProperties(interfaceInfoAddRequest, interfaceInfo);
        // 校验
        interfaceInfoService.validInterfaceInfo(interfaceInfo, true);
        User loginUser = userService.getLoginUser(request);
        interfaceInfo.setUserId(loginUser.getId());
        boolean result = interfaceInfoService.save(interfaceInfo);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        long newInterfaceInfoId = interfaceInfo.getId();
        return ResultUtils.success(newInterfaceInfoId);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteInterfaceInfo(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        List<Long> idList = deleteRequest.getIdList();
        if (idList.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        // 判断是否存在
        List<InterfaceInfo> interfaceInfoList = interfaceInfoService.listByIds(idList);
        if (interfaceInfoList == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        for (InterfaceInfo info : interfaceInfoList) {
            // 仅本人或管理员可删除
            if (!info.getUserId().equals(user.getId()) || !userService.isAdmin(request)) {
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
            }
        }
        boolean b = interfaceInfoService.removeByIds(interfaceInfoList);
        return ResultUtils.success(b);
    }

    /**
     * 更新
     *
     * @param interfaceInfoUpdateRequest
     * @param request
     * @return
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateInterfaceInfo(@RequestBody InterfaceInfoUpdateRequest interfaceInfoUpdateRequest, HttpServletRequest request) {
        if (interfaceInfoUpdateRequest == null || interfaceInfoUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        if (CollectionUtils.isNotEmpty(interfaceInfoUpdateRequest.getRequestParams())) {
            List<RequestParamsField> requestParamsFields = interfaceInfoUpdateRequest.getRequestParams()
                    .stream()
                    .filter(field -> StringUtils.isNotBlank(field.getFieldName()))
                    .collect(Collectors.toList());
            String requestParams = JSONUtil.toJsonStr(requestParamsFields);
            interfaceInfo.setRequestParams(requestParams);
        } else {
            interfaceInfo.setRequestParams("[]");
        }
        if (CollectionUtils.isNotEmpty(interfaceInfoUpdateRequest.getResponseParams())) {
            List<ResponseParamsField> responseParamsFields = interfaceInfoUpdateRequest.getResponseParams()
                    .stream()
                    .filter(field -> StringUtils.isNotBlank(field.getFieldName()))
                    .collect(Collectors.toList());
            String responseParams = JSONUtil.toJsonStr(responseParamsFields);
            interfaceInfo.setResponseParams(responseParams);
        } else {
            interfaceInfo.setResponseParams("[]");
        }
        BeanUtils.copyProperties(interfaceInfoUpdateRequest, interfaceInfo);
        // 参数校验
        interfaceInfoService.validInterfaceInfo(interfaceInfo, false);
        User user = userService.getLoginUser(request);
        long id = interfaceInfoUpdateRequest.getId();
        // 判断是否存在
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        if (oldInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 仅本人或管理员可修改
        if (!oldInterfaceInfo.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = interfaceInfoService.updateById(interfaceInfo);
        if (result) {
            // 数据库更新成功后，刷新缓存
            interfaceCacheService.refreshInterfaceCache(interfaceInfo.getId());
        }
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<InterfaceInfo> getInterfaceInfoById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfo = interfaceCacheService.getInterfaceInfoById(id);
        return ResultUtils.success(interfaceInfo);
    }

    /**
     * 获取列表（仅管理员可使用）
     *
     * @param interfaceInfoQueryRequest
     * @return
     */
    @AuthCheck(mustRole = "admin")
    @GetMapping("/list")
    public BaseResponse<List<InterfaceInfo>> listInterfaceInfo(InterfaceInfoQueryRequest interfaceInfoQueryRequest) {
        InterfaceInfo interfaceInfoQuery = new InterfaceInfo();
        if (interfaceInfoQueryRequest != null) {
            BeanUtils.copyProperties(interfaceInfoQueryRequest, interfaceInfoQuery);
        }
        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>(interfaceInfoQuery);
        List<InterfaceInfo> interfaceInfoList = interfaceInfoService.list(queryWrapper);
        return ResultUtils.success(interfaceInfoList);
    }

    /**
     * 分页获取列表
     *
     * @param interfaceInfoQueryRequest
     * @return BaseResponse<Page < InterfaceInfo>>
     */
    @GetMapping("/list/page")
    @RedisRateLimiter(value = "queueList", capacity = 5, rate = 1)
    public BaseResponse<Page<InterfaceInfo>> listInterfaceInfoByPage(InterfaceInfoQueryRequest interfaceInfoQueryRequest) {
        if (interfaceInfoQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfoQuery = new InterfaceInfo();
        BeanUtils.copyProperties(interfaceInfoQueryRequest, interfaceInfoQuery);
        long current = interfaceInfoQueryRequest.getCurrent();
        long size = interfaceInfoQueryRequest.getPageSize();
        String sortField = interfaceInfoQueryRequest.getSortField();
        String sortOrder = interfaceInfoQueryRequest.getSortOrder();
        String description = interfaceInfoQuery.getDescription();
        // description 需支持模糊搜索
        interfaceInfoQuery.setDescription(null);
        // 限制爬虫
        if (size > 50) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>(interfaceInfoQuery);
        queryWrapper.like(StringUtils.isNotBlank(description), "description", description);
        queryWrapper.orderBy(StringUtils.isNotBlank(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC), sortField);
        Page<InterfaceInfo> interfaceInfoPage = interfaceInfoService.page(new Page<>(current, size), queryWrapper);
        return ResultUtils.success(interfaceInfoPage);
    }

    // endregion

    /**
     * 发布
     *
     * @param idRequest
     * @param request
     * @return
     */
    @PostMapping("/online")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<Boolean> onlineInterfaceInfo(@RequestBody IdRequest idRequest, HttpServletRequest request) {
        if (ObjectUtils.anyNull(idRequest, idRequest.getId()) || idRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long id = idRequest.getId();
        InterfaceInfo interfaceInfo = interfaceInfoService.getById(id);
        if (interfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        interfaceInfo.setStatus(InterfaceInfoStatusEnum.ONLINE.getValue());
        return ResultUtils.success(interfaceInfoService.updateById(interfaceInfo));
    }

    /**
     * 下线
     *
     * @param idRequest
     * @param request
     * @return
     */
    @PostMapping("/offline")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<Boolean> offlineInterfaceInfo(@RequestBody IdRequest idRequest, HttpServletRequest request) {
        if (idRequest == null || idRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long id = idRequest.getId();
        // 判断是否存在
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        if (oldInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 仅本人或管理员可修改
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        interfaceInfo.setId(id);
        interfaceInfo.setStatus(InterfaceInfoStatusEnum.OFFLINE.getValue());
        boolean result = interfaceInfoService.updateById(interfaceInfo);
        return ResultUtils.success(result);
    }

    /**
     * 调用接口
     *
     * @param interfaceInfoInvokeRequest 请求参数
     * @param request
     * @return BaseResponse<Object>
     */
    @PostMapping("/invoke")
    @RedisRateLimiter(value = "invoke", capacity = 10, rate = 5)
    public BaseResponse<Object> invokeInterfaceInfo(@RequestBody InterfaceInfoInvokeRequest interfaceInfoInvokeRequest, HttpServletRequest request) {
        if (ObjectUtils.anyNull(interfaceInfoInvokeRequest, interfaceInfoInvokeRequest.getId()) || interfaceInfoInvokeRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long id = interfaceInfoInvokeRequest.getId();
        // 判断是否存在
        InterfaceInfo interfaceInfo = interfaceInfoService.getById(id);

        if (interfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        if (interfaceInfo.getStatus() == InterfaceInfoStatusEnum.OFFLINE.getValue()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口已关闭");
        }
        // 构建请求参数
        List<InterfaceInfoInvokeRequest.Field> fieldList = interfaceInfoInvokeRequest.getRequestParams();
        String requestParams = "{}";
        if (fieldList != null && !fieldList.isEmpty()) {
            JsonObject jsonObject = new JsonObject();
            for (InterfaceInfoInvokeRequest.Field field : fieldList) {
                jsonObject.addProperty(field.getFieldName(), field.getValue());
            }
            requestParams = gson.toJson(jsonObject);
        }
        Map<String, Object> params = new Gson().fromJson(requestParams, new TypeToken<Map<String, Object>>() {
        }.getType());
        
        User loginUser = userService.getLoginUser(request);
        try {
            CubicApiClient cubicApiClient = cubicApiClientFactory.newCubicClient(loginUser.getAccessKey(), loginUser.getSecretKey());
            CurrentRequest currentRequest = new CurrentRequest();
            currentRequest.setMethod(interfaceInfo.getMethod());
            currentRequest.setPath(interfaceInfo.getUrl());
            currentRequest.setRequestParams(params);
            ResultResponse response = apiService.request(cubicApiClient, currentRequest);
            return ResultUtils.success(response.getData());
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, e.getMessage());
        }
    }
}
