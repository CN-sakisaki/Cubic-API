package com.js.jsapiinterface.controller;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.js.jsapiclientsdk.exception.ApiException;
import com.js.jsapiclientsdk.model.param.*;
import com.js.jsapiclientsdk.model.response.NameResponse;
import com.js.jsapiclientsdk.model.response.RandomWallpaperResponse;
import com.js.jsapiclientsdk.model.response.ResultResponse;
import com.js.jsapicommon.common.BusinessException;
import com.js.jsapicommon.common.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static com.js.jsapiinterface.utils.RequestUtils.buildUrl;
import static com.js.jsapiinterface.utils.RequestUtils.get;
import static com.js.jsapiinterface.utils.ResponseUtils.baseResponse;
import static com.js.jsapiinterface.utils.ResponseUtils.responseToMap;

/**
 *
 * @author sakisaki
 * @date 2025/1/11 16:55
 */
@RestController
@RequestMapping("/")
@Slf4j
public class ServiceController {
    /**
     * 获取名字
     * @param nameParams 名字
     * @return NameResponse
     */
    @GetMapping("/name")
    public NameResponse getName(NameParams nameParams) {
        return JSONUtil.toBean(JSONUtil.toJsonStr(nameParams), NameResponse.class);
    }

    @GetMapping("/poisonousChickenSoup")
    public String getPoisonousChickenSoup() {
        return get("https://api.btstu.cn/yan/api.php?charset=utf-8&encode=json");
    }

    @GetMapping("/randomWallpaper")
    public RandomWallpaperResponse randomWallpaper(RandomWallpaperParams randomWallpaperParams) throws ApiException {
        String baseUrl = "https://api.btstu.cn/sjbz/api.php";
        String url = buildUrl(baseUrl, randomWallpaperParams);
        if (StringUtils.isAllBlank(randomWallpaperParams.getLx(), randomWallpaperParams.getMethod())) {
            url = url + "?format=json";
        } else {
            url = url + "&format=json";
        }
        return JSONUtil.toBean(get(url), RandomWallpaperResponse.class);
    }

    @GetMapping("/horoscope")
    public ResultResponse getHoroscope(HoroscopeParams horoscopeParams) throws ApiException {
        String response = get("https://api.vvhan.com/api/horoscope", horoscopeParams);
        Map<String, Object> fromResponse = responseToMap(response);
        boolean success = (boolean) fromResponse.get("success");
        if (!success) {
            ResultResponse baseResponse = new ResultResponse();
            baseResponse.setData(fromResponse);
            return baseResponse;
        }
        return JSONUtil.toBean(response, ResultResponse.class);
    }

    @GetMapping("/ipInfo")
    public ResultResponse getIpInfo(IpInfoParams ipInfoParams) {
        return baseResponse("https://api.vvhan.com/api/getIpInfo", ipInfoParams);
    }

    @GetMapping("/weather")
    public ResultResponse getWeatherInfo(WeatherParams weatherParams) {
        return baseResponse("https://api.vvhan.com/api/weather", weatherParams);
    }

    @GetMapping("/weatherSoJson")
    public ResultResponse getWeatherSoJson(@RequestParam String cityCode) {
        String baseUrl = "http://t.weather.sojson.com/api/weather/city/" + cityCode;
        return baseResponse(baseUrl);
    }
}
