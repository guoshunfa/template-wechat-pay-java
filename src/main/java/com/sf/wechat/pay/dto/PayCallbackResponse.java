package com.sf.wechat.pay.dto;

import lombok.Data;

/**
 * 支付回调响应参数
 */
@Data
public class PayCallbackResponse {
    /**
     * 返回码
     */
    private String code;

    /**
     * 返回信息
     */
    private String message;

    /**
     * 构建成功响应
     */
    public static PayCallbackResponse success() {
        PayCallbackResponse response = new PayCallbackResponse();
        response.setCode("SUCCESS");
        response.setMessage("成功");
        return response;
    }

    /**
     * 构建失败响应
     */
    public static PayCallbackResponse fail(String message) {
        PayCallbackResponse response = new PayCallbackResponse();
        response.setCode("FAIL");
        response.setMessage(message);
        return response;
    }
} 