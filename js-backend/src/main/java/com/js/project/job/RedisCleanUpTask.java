package com.js.project.job;

import com.js.project.constant.SignConstant;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author sakisaki
 * @date 2025/1/18 18:05
 */
@Component
public class RedisCleanUpTask {

    private final StringRedisTemplate stringRedisTemplate;

    public RedisCleanUpTask(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /*
        0（秒）：表示在每分钟的第 0 秒执行
        0（分钟）：表示在每小时的第 0 分钟执行
        0（小时）：表示在每天的第 0 小时（即午夜）执行
        1（天）：表示在每月的第 1 天执行
        *（月）：表示在每个月执行
        *（星期几）：表示不限制星期几，任何星期几都可以执行
    */

    /**
     * 定时任务：每月 1 号执行，删除上个月的数据
     * */
    // 每月1号 00:00:00 执行
    @Scheduled(cron = "0 0 0 1 * *")
    public void cleanUpOldMonthData() {
        LocalDate now = LocalDate.now();
        // 获取上个月
        LocalDate lastMonth = now.minusMonths(1);
        String lastMonthPrefix = SignConstant.SIGN_KEY_PREFIX + lastMonth.format(DateTimeFormatter.ofPattern(SignConstant.DATE_FORMAT));

        // 使用 scan 命令来查找所有匹配的键
        Cursor<byte[]> cursor = stringRedisTemplate.executeWithStickyConnection(connection -> {
            return connection.scan(ScanOptions.scanOptions().match(lastMonthPrefix + "*").count(1000).build());
        });

        // 遍历并删除这些匹配的键
        while (cursor != null && cursor.hasNext()) {
            byte[] keyBytes = cursor.next();
            String key = new String(keyBytes, StandardCharsets.UTF_8);
            stringRedisTemplate.delete(key);
        }

        // 关闭 cursor 资源
        if (cursor != null) {
            cursor.close();
        }

    }
}
