package com.saki.apiproject.model.vo;



import com.saki.common.model.entity.InterfaceInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author JianShang
 * @version 1.0.0
 * @description 接口信息封装视图
 * @date 2024-09-13 12:10:52
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class InterfaceInfoVO extends InterfaceInfo {

    /**
     * 调用次数
     */
    private long totalNum;

    private static final long serialVersionUID = 1L;
}