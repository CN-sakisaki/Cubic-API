package com.saki.apiproject.model.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户帐户状态枚举
 * @author sakisaki
 * @date 2025/2/7 00:39
 */
@Getter
public enum UserAccountStatusEnum {

    /**
     * 正常
     */
    NORMAL("正常", 0),
    /**
     * 封号
     */
    BAN("封禁", 1);

    private final String text;

    private final int value;

    UserAccountStatusEnum(String text, int value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 获取值
     * 获取值列表
     *
     * @return {@link List}<{@link Integer}>
     */
    public static List<Integer> getValues() {
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }

}
