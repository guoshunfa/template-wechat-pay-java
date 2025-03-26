package com.sf.wechat.pay.dto;

import lombok.Data;

/**
 * 商户下单响应参数
 */
@Data
public class PrepayResponse {
    /**
     * 二维码图片（Base64编码）
     */
    private String qrCodeImage;

    /**
     * 二维码链接
     */
    private String codeUrl;

    /**
     * 商户订单号
     */
    private String outTradeNo;
}