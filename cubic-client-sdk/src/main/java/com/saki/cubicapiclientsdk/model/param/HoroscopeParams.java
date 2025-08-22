package com.saki.cubicapiclientsdk.model.param;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 十二星座请求参数
 * @author sakisaki
 * @date 2025/1/11 17:29
 */
@Data
@Accessors(chain = true)
public class HoroscopeParams implements Serializable {
    private static final long serialVersionUID = 3815188540434269370L;
    /**
     * 十二星座对应英文小写，aries, taurus, gemini, cancer, leo, virgo, libra, scorpio, sagittarius, capricorn, aquarius, pisces
     */
    private String type;
    /**
     * 十二星座对应英文小写，aries, taurus, gemini, cancer, leo, virgo, libra, scorpio, sagittarius, capricorn, aquarius, pisces
     */
    private String time;
}
