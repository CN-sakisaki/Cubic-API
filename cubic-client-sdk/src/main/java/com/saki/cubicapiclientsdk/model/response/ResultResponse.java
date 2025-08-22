package com.saki.cubicapiclientsdk.model.response;


import com.fasterxml.jackson.annotation.JsonAnyGetter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 通用的响应结果封装类
 *
 * @author sakisaki
 * @date 2024/12/17 23:42
 */
@Setter
@NoArgsConstructor
public class ResultResponse implements Serializable {

    private static final long serialVersionUID = 4844062154623399125L;

    private Map<String, Object> data = new HashMap<>();

    @JsonAnyGetter
    public Map<String, Object> getData() {
        return data;
    }
}
