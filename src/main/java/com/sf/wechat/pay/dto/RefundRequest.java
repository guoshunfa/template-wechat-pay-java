package com.sf.wechat.pay.dto;

import lombok.Data;

import java.util.List;

/**
 * 微信支付退款请求
 */
@Data
public class RefundRequest {

    /**
     * 微信支付订单号
     */
    private String transactionId;

    /**
     * 商户订单号
     */
    private String outTradeNo;

    /**
     * 商户退款单号
     */
    private String outRefundNo;

    /**
     * 退款原因
     */
    private String reason;

    /**
     * 通知地址
     */
    private String notifyUrl;

    /**
     * 退款金额（单位：分）
     */
    private Integer refundAmount;

    /**
     * 原订单金额（单位：分）
     */
    private Integer totalAmount;

    /**
     * 退款资金来源
     * AVAILABLE: 可用余额账户
     * UNSETTLED: 未结算资金
     */
    private String fundsAccount;

    /**
     * 退款商品
     */
    private List<GoodsDetail> goodsDetails;

    /**
     * 商品详情
     */
    @Data
    public static class GoodsDetail {
        /**
         * 商户侧商品编码
         */
        private String merchantGoodsId;

        /**
         * 微信侧商品编码
         */
        private String wechatpayGoodsId;

        /**
         * 商品名称
         */
        private String goodsName;

        /**
         * 商品单价
         */
        private Integer unitPrice;

        /**
         * 退款金额
         */
        private Integer refundAmount;

        /**
         * 退货数量
         */
        private Integer refundQuantity;
    }
} 