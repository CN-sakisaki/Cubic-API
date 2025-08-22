package com.saki.apiproject.constant;

/**
 *
 * @author sakisaki
 * @date 2025/2/6 21:04
 */
public interface EmailConstant {

    /**
     * 电子邮件html内容路径 resources目录下
     */
    String EMAIL_HTML_CONTENT_PATH = "email.html";

    /**
     * captcha缓存键
     */
    String CAPTCHA_CACHE_KEY = "api:captcha:";

    /**
     * 电子邮件主题
     */
    String EMAIL_SUBJECT = "验证码邮件";

    /**
     * 电子邮件标题
     */
    String EMAIL_TITLE = "Js-API 接口开放平台";

    /**
     * 电子邮件标题英语
     */
    String EMAIL_TITLE_ENGLISH = "Js-API Open Interface Platform";

    /**
     * 平台负责人
     */
    String PLATFORM_RESPONSIBLE_PERSON = "sakisaki";

    /**
     * 平台地址
     */
    String PLATFORM_ADDRESS = "<a href='https://api.qimuu.icu/'>请联系我们</a>";

}
