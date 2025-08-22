package com.saki.apiinterface.controller;

import cn.hutool.json.JSONUtil;
import com.saki.cubicapiclientsdk.exception.ApiException;
import com.saki.cubicapiclientsdk.model.param.*;
import com.saki.cubicapiclientsdk.model.response.NameResponse;
import com.saki.cubicapiclientsdk.model.response.RandomWallpaperResponse;
import com.saki.cubicapiclientsdk.model.response.ResultResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static com.saki.apiinterface.utils.RequestUtils.buildUrl;
import static com.saki.apiinterface.utils.RequestUtils.get;
import static com.saki.apiinterface.utils.ResponseUtils.baseResponse;
import static com.saki.apiinterface.utils.ResponseUtils.responseToMap;

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
    @GetMapping("/loversPrattle")
    public String getLoversPrattle() {
        return get("https://api.zxki.cn/api/twqh");
    }

    // @GetMapping("/appointWallpaper")
    // public AppointWallpaperResponse appointWallpaper(AppointWallpaperParams appointWallpaperParams) throws ApiException {
    //     String baseUrl = "https://wp.upx8.com/api.php";
    //     String url = buildUrl(baseUrl, appointWallpaperParams);
    //     // if (StringUtils.isAllBlank(appointWallpaperParams.getContent())) {
    //     //     url = url + "?format=json";
    //     // } else {
    //     //     url = url + "&format=json";
    //     // }
    //     return JSONUtil.toBean(get(url), AppointWallpaperResponse.class);
    // }

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
        String response = get("http://v2.xxapi.cn/api/horoscope", horoscopeParams);
        Map<String, Object> fromResponse = responseToMap(response);
        // boolean success = (boolean) fromResponse.get("request_id");
        fromResponse.remove("msg");
        fromResponse.remove("request_id");
        fromResponse.remove("code");
        ResultResponse baseResponse = new ResultResponse();
        baseResponse.setData(fromResponse);
        return baseResponse;
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
