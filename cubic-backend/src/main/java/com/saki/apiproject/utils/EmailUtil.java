package com.saki.apiproject.utils;


import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.MessageFormat;

import static com.saki.apiproject.constant.EmailConstant.*;


/**
 * 电子邮件生成内容实用程序
 * @author sakisaki
 * @date 2025/2/6 22:23
 */
@Slf4j
public class EmailUtil {
    /**
     * 生成电子邮件内容
     *
     * @param captcha       验证码
     * @param emailHtmlPath 电子邮件html路径
     * @return {@link String}
     */
    public static String buildEmailContent(String emailHtmlPath, String captcha) {
        // 加载邮件html模板
        ClassPathResource resource = new ClassPathResource(emailHtmlPath);
        InputStream inputStream = null;
        BufferedReader fileReader = null;
        StringBuilder buffer = new StringBuilder();
        String line;
        try {
            inputStream = resource.getInputStream();
            fileReader = new BufferedReader(new InputStreamReader(inputStream));
            while ((line = fileReader.readLine()) != null) {
                buffer.append(line);
            }
        } catch (Exception e) {
            log.info("发送邮件读取模板失败{}", e.getMessage());
        } finally {
            if (fileReader != null) {
                try {
                    fileReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        // 替换html模板中的参数
        return MessageFormat.format(buffer.toString(), captcha, EMAIL_TITLE, EMAIL_TITLE_ENGLISH, PLATFORM_RESPONSIBLE_PERSON, PLATFORM_ADDRESS);
    }
}
