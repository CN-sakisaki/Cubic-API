package com.saki.apiproject.controller;



import com.saki.apiproject.constant.SignConstant;
import com.saki.apiproject.service.MonthlySignRecordsService;
import com.saki.apiproject.service.UserService;
import com.saki.common.common.BaseResponse;
import com.saki.common.common.BusinessException;
import com.saki.common.common.ErrorCode;
import com.saki.common.common.ResultUtils;
import com.saki.common.model.entity.User;
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
        User loginUser = getUser(request);
        boolean sign = monthlySignRecordsService.sign(loginUser.getId());
        return ResultUtils.success(sign);
    }

    @PostMapping("/signTotal")
    public BaseResponse<Integer> monthlySignTotal(HttpServletRequest request) {
        User loginUser = getUser(request);
        int totalSignDays = monthlySignRecordsService.countTotalSignDays(loginUser.getId());
        return ResultUtils.success(totalSignDays);
    }

    @PostMapping("/getConsecutiveSignDays")
    public BaseResponse<Integer> getConsecutiveSignDaysFromRedis(HttpServletRequest request) {
        User user = getUser(request);
        String key = SignConstant.CONSECUTIVE_SIGN_PREFIX + user.getId() + SignConstant.CONSECUTIVE_SIGN_DAYS;
        Integer consecutiveSignDaysFromRedis = monthlySignRecordsService.getConsecutiveSignDaysFromRedis(key);
        return ResultUtils.success(consecutiveSignDaysFromRedis);
    }

    private User getUser(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "请先登录");
        }
        return loginUser;
    }
}
