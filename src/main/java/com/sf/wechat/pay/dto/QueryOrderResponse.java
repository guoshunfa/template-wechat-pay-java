package com.sf.wechat.pay.dto;

import lombok.Data;

/**
 * 查询订单响应参数
 */
@Data
public class QueryOrderResponse {
    /**
     * 应用ID
     */
    private String appid;
    
    /**
     * 商户号
     */
    private String mchid;
    
    /**
     * 商户订单号
     */
    private String outTradeNo;
    
    /**
     * 微信支付订单号
     */
    private String transactionId;
    
    /**
     * 交易类型
     */
    private String tradeType;
    
    /**
     * 交易状态
     */
    private String tradeState;
    
    /**
     * 交易状态描述
     */
    private String tradeStateDesc;
    
    /**
     * 付款银行
     */
    private String bankType;
    
    /**
     * 附加数据
     */
    private String attach;
    
    /**
     * 支付完成时间
     */
    private String successTime;
    
    /**
     * 订单金额
     */
    private Integer total;
    
    /**
     * 货币类型
     */
    private String currency;
} 