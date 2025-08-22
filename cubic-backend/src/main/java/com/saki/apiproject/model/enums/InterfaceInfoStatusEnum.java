package com.saki.apiproject.model.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author JianShang
 * @version 1.0.0
 * @description 接口信息状态枚举
 * @date 2024-09-13 12:11:06
 */
@Getter
public enum InterfaceInfoStatusEnum {

    OFFLINE("关闭", 0),
    ONLINE("上线", 1);

    private final String text;

    private final int value;

    InterfaceInfoStatusEnum(String text, int value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 获取值列表
     *
     * @return
     */
    public static List<Integer> getValues() {
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }

}
