package com.js.project.controller;

import com.js.project.service.MonthlySignRecordsService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class MonthlySignRecordsControllerTest {

    @Resource
    private MonthlySignRecordsService monthlySignRecordsService;

    @Test
    public void testSignAndIsSigned() {
        Long userId = 2L;


        // 首次签到测试
        assertTrue(monthlySignRecordsService.sign(userId));


        // 再次签到测试，应该失败
        assertFalse(monthlySignRecordsService.sign(userId));


        // 检查已签到情况
        assertTrue(monthlySignRecordsService.isSigned(userId, LocalDate.now().getDayOfMonth()));


        // 检查未来日期，应该未签到
        assertFalse(monthlySignRecordsService.isSigned(userId, LocalDate.now().getDayOfMonth() + 1));


        // 测试不同用户
        Long anotherUserId = 1L;
        assertTrue(monthlySignRecordsService.sign(anotherUserId));
        assertTrue(monthlySignRecordsService.isSigned(anotherUserId, LocalDate.now().getDayOfMonth()));
    }
}