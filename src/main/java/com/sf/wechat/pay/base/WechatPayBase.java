package com.sf.wechat.pay.base;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * 微信支付基础类
 */
@Component
public class WechatPayBase {

    @Value("${wechat.pay.merchantId}")
    private String merchantId;

    @Value("${wechat.pay.privateKeyPath}")
    private String privateKeyPath;

    @Value("${wechat.pay.merchantSerialNumber}")
    private String merchantSerialNumber;

    @Value("${wechat.pay.apiV3Key}")
    private String apiV3Key;

    @Value("${wechat.pay.appId}")
    private String appId;

    public String getMerchantId() {
        return merchantId;
    }

    public String getPrivateKeyPath() {
        return privateKeyPath;
    }

    public String getMerchantSerialNumber() {
        return merchantSerialNumber;
    }

    public String getApiV3Key() {
        return apiV3Key;
    }

    public String getAppId() {
        return appId;
    }
    
    /**
     * 读取私钥文件内容
     *
     * @return 私钥文件内容
     */
    public String getPrivateKeyContent() {
        try {
            // 从classpath中加载资源
            ClassPathResource resource = new ClassPathResource(privateKeyPath.replaceAll("^classpath:", ""));
            InputStream inputStream = resource.getInputStream();
            return StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("无法加载私钥文件: " + privateKeyPath, e);
        }
    }
}
