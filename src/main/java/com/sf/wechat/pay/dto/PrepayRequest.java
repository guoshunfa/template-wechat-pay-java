package com.sf.wechat.pay.dto;

import lombok.Data;

/**
 * 商户下单请求参数
 */
@Data
public class PrepayRequest {
    /**
     * 商品描述
     */
    private String description;

    /**
     * 商户订单号
     */
    private String outTradeNo;

    /**
     * 订单金额（单位：分）
     */
    private Integer totalFee = 1; // 默认1分钱

    /**
     * 通知地址
     */
    private String notifyUrl = "https://www.weixin.qq.com/wxpay/pay.php"; // 默认回调地址
} 