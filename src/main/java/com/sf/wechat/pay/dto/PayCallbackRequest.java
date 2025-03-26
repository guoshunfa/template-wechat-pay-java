package com.sf.wechat.pay.dto;

import lombok.Data;

/**
 * 支付回调请求参数
 */
@Data
public class PayCallbackRequest {
    /**
     * 商户号
     */
    private String mchid;

    /**
     * 微信支付订单号
     */
    private String transactionId;

    /**
     * 商户订单号
     */
    private String outTradeNo;

    /**
     * 交易状态
     */
    private String tradeState;

    /**
     * 支付完成时间
     */
    private String successTime;

    /**
     * 交易类型
     */
    private String tradeType;

    /**
     * 订单金额
     */
    private Integer amount;
} 