package com.js.project.controller;

import com.js.jsapicommon.model.entity.User;
import com.js.project.common.BaseResponse;
import com.js.project.common.ErrorCode;
import com.js.project.common.ResultUtils;
import com.js.project.exception.BusinessException;
import com.js.project.service.MonthlySignRecordsService;
import com.js.project.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 用户签到控制类
 * @author sakisaki
 * @date 2025/1/17 21:10
 */
@RequestMapping("/")
@RestController
public class MonthlySignRecordsController {

    @Resource
    private MonthlySignRecordsService monthlySignRecordsService;

    @Resource
    private UserService userService;


    @PostMapping("/sign")
    public BaseResponse<Boolean> monthlySign(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "请先登录");
        }
        boolean sign = monthlySignRecordsService.sign(loginUser.getId());
        return ResultUtils.success(sign);
    }

    @PostMapping("/signTotal")
    public BaseResponse<Integer> monthlySignTotal(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "请先登录");
        }
        int totalSignDays = monthlySignRecordsService.countTotalSignDays(loginUser.getId());
        return ResultUtils.success(totalSignDays);
    }
}
