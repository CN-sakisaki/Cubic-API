package com.saki.cubicapiclientsdk.utils;

import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.Digester;

/**
 * @author JianShang
 * @version 1.0.0
 * @description 签名工具
 * @date 2024-09-13 03:28:09
 */
public class SignUtils {
    /**
     * 生成签名
     * @param body
     * @param secretKey
     * @return
     */
    public static String getSign(String body, String secretKey) {
        Digester md5 = new Digester(DigestAlgorithm.SHA256);
        String content = body + "." + secretKey;
        return md5.digestHex(content);
    }
}
