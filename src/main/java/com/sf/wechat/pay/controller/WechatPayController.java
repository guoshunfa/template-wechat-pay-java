package com.sf.wechat.pay.controller;

import com.sf.wechat.pay.dto.*;
import com.sf.wechat.pay.service.WechatPayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Slf4j
@RestController
@RequestMapping("/api/wechat/pay")
@CrossOrigin(origins = "*", maxAge = 3600)
public class WechatPayController {

    @Resource
    private WechatPayService wechatPayService;
    
    /**
     * 商户下单（创建一个订单）
     *
     * @param request 下单请求
     * @return 微信支付二维码图片
     */
    @PostMapping("/order")
    public PrepayResponse pay(@RequestBody PrepayRequest request) {
        return wechatPayService.prepay(request);
    }
    
    /**
     * 根据商户订单号查询
     *
     * @param outTradeNo 商户订单号
     * @return 订单详情
     */
    @GetMapping("/query/out-trade-no/{outTradeNo}")
    public QueryOrderResponse queryByOutTradeNo(@PathVariable("outTradeNo") String outTradeNo) {
        QueryOrderRequest request = new QueryOrderRequest();
        request.setOutTradeNo(outTradeNo);
        return wechatPayService.queryOrder(request);
    }

    /**
     * 退款接口
     *
     * @param request 退款请求参数
     * @return 退款结果
     */
    @PostMapping("/refund")
    public RefundResponse refund(@RequestBody RefundRequest request) {
        log.info("收到退款请求: {}", request);
        return wechatPayService.refund(request);
    }
}
