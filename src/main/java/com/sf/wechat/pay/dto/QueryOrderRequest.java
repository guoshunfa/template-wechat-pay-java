package com.sf.wechat.pay.dto;

import lombok.Data;

/**
 * 查询订单请求参数
 */
@Data
public class QueryOrderRequest {
    /**
     * 商户订单号
     */
    private String outTradeNo;

    /**
     * 微信支付订单号
     */
    private String transactionId;
} 