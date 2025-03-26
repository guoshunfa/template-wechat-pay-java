package com.sf.wechat.pay.service;

import com.sf.wechat.pay.dto.*;
import org.springframework.stereotype.Service;

/**
 * 微信支付服务类
 */
@Service
public interface WechatPayService {

    /**
     * 商户下单（创建一个订单），并生成二维码图片
     *
     * @param request 下单请求
     * @return 包含二维码图片的响应
     */
    PrepayResponse prepay(PrepayRequest request);

    /**
     * 查询订单信息
     *
     * @param request 查询请求
     * @return 订单信息
     */
    QueryOrderResponse queryOrder(QueryOrderRequest request);
    
    /**
     * 申请退款
     *
     * @param request 退款请求
     * @return 退款结果
     */
    RefundResponse refund(RefundRequest request);
}
