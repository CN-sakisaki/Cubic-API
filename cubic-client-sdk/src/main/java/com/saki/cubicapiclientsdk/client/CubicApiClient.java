package com.saki.cubicapiclientsdk.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author JianShang
 * @version 1.0.0
 * @description 调用第三方接口的客户端
 * @date 2024-09-13 03:27:50
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CubicApiClient {

    /**
     * 访问密钥
     */
    private String accessKey;

    /**
     * 密钥
     */
    private String secretKey;

    // public String getNameByGet(String name) {
    //     // 可以单独传入http参数，这样参数会自动做URL编码，拼接在URL中
    //     HashMap<String, Object> paramMap = new HashMap<>();
    //     paramMap.put("name", name);
    //     String result = HttpUtil.get(GATEWAY_HOST + "/api/name/", paramMap);
    //     System.out.println(result);
    //     return result;
    // }
    //
    // public String getNameByPost(String name) {
    //     // 可以单独传入http参数，这样参数会自动做URL编码，拼接在URL中
    //     HashMap<String, Object> paramMap = new HashMap<>();
    //     paramMap.put("name", name);
    //     String result = HttpUtil.post(GATEWAY_HOST + "/api/name/", paramMap);
    //     System.out.println(result);
    //     return result;
    // }
    //
    // private Map<String, String> getHeaderMap(String body) {
    //     Map<String, String> hashMap = new HashMap<>();
    //     hashMap.put("accessKey", accessKey);
    //     // 一定不能直接发送
    //     // hashMap.put("secretKey", secretKey);
    //     hashMap.put("nonce", RandomUtil.randomNumbers(4));
    //     hashMap.put("body", body);
    //     hashMap.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000));
    //     hashMap.put("sign", getSign(body, secretKey));
    //     return hashMap;
    // }
    //
    // public String getUsernameByPost(User user) {
    //     String json = JSONUtil.toJsonStr(user);
    //     HttpResponse httpResponse = HttpRequest.post(GATEWAY_HOST + "/api/name/user")
    //             .addHeaders(getHeaderMap(json))
    //             .body(json)
    //             .execute();
    //     System.out.println(httpResponse.getStatus());
    //     String result = httpResponse.body();
    //     System.out.println(result);
    //     return result;
    // }
}
