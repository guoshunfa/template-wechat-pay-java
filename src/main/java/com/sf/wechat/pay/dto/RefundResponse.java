package com.sf.wechat.pay.dto;

import lombok.Data;

/**
 * 微信支付退款响应
 */
@Data
public class RefundResponse {

    /**
     * 微信支付退款单号
     */
    private String refundId;

    /**
     * 商户退款单号
     */
    private String outRefundNo;

    /**
     * 微信支付订单号
     */
    private String transactionId;

    /**
     * 商户订单号
     */
    private String outTradeNo;

    /**
     * 退款渠道
     */
    private String channel;

    /**
     * 退款入账账户
     */
    private String userReceivedAccount;

    /**
     * 退款成功时间
     */
    private String successTime;

    /**
     * 退款创建时间
     */
    private String createTime;

    /**
     * 退款状态
     * SUCCESS：退款成功
     * CLOSED：退款关闭
     * PROCESSING：退款处理中
     * ABNORMAL：退款异常
     */
    private String status;

    /**
     * 资金账户
     */
    private String fundsAccount;

    /**
     * 退款金额（单位：分）
     */
    private Integer refundAmount;

    /**
     * 原订单金额（单位：分）
     */
    private Integer totalAmount;

    /**
     * 退款出资账户及金额
     */
    private String from;
} 