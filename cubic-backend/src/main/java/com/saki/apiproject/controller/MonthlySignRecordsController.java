package com.saki.apiproject.controller;


import com.saki.apiproject.constant.SignConstant;
import com.saki.apiproject.service.MonthlySignRecordsService;
import com.saki.apiproject.service.UserService;
import com.saki.common.common.BaseResponse;
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
@RequestMapping("/sign")
@RestController
public class MonthlySignRecordsController {

    @Resource
    private MonthlySignRecordsService monthlySignRecordsService;

    @Resource
    private UserService userService;

    /**
     * 执行签到
     */
    @PostMapping("/do")
    public BaseResponse<Boolean> monthlySign(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        boolean sign = monthlySignRecordsService.sign(loginUser.getId());
        return ResultUtils.success(sign);
    }

    /**
     * 获取总签到天数
     */
    @PostMapping("/Total")
    public BaseResponse<Integer> monthlySignTotal(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        int totalSignDays = monthlySignRecordsService.countTotalSignDays(loginUser.getId());
        return ResultUtils.success(totalSignDays);
    }

    /**
     * 获取连续签到的天数
     */
    @PostMapping("/consecutive")
    public BaseResponse<Integer> getConsecutiveSignDaysFromRedis(HttpServletRequest request) {
        User user = userService.getLoginUser(request);
        String key = String.format("%s:%d:%s", SignConstant.CONSECUTIVE_SIGN_PREFIX, user.getId(), SignConstant.CONSECUTIVE_SIGN_DAYS);
        Integer consecutiveSignDaysFromRedis = monthlySignRecordsService.getConsecutiveSignDaysFromRedis(key);
        return ResultUtils.success(consecutiveSignDaysFromRedis);
    }
}
