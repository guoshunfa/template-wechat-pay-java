package com.sf.wechat.pay.service.impl;

import com.sf.wechat.pay.base.WechatPayBase;
import com.sf.wechat.pay.dto.*;
import com.sf.wechat.pay.service.WechatPayService;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.wechat.pay.java.core.Config;
import com.wechat.pay.java.core.RSAAutoCertificateConfig;
import com.wechat.pay.java.core.exception.ServiceException;
import com.wechat.pay.java.service.payments.model.Transaction;
import com.wechat.pay.java.service.payments.nativepay.NativePayService;
import com.wechat.pay.java.service.payments.nativepay.model.Amount;
import com.wechat.pay.java.service.payments.nativepay.model.QueryOrderByOutTradeNoRequest;
import com.wechat.pay.java.service.refund.RefundService;
import com.wechat.pay.java.service.refund.model.AmountReq;
import com.wechat.pay.java.service.refund.model.CreateRequest;
import com.wechat.pay.java.service.refund.model.Refund;
import com.wechat.pay.java.service.refund.model.ReqFundsAccount;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 微信支付服务类实现
 */
@Slf4j
@Service
public class WechatPayServiceImpl implements WechatPayService {

    @Resource
    private WechatPayBase wechatPayBase;

    @Override
    public PrepayResponse prepay(PrepayRequest request) {
        try {
            // 1. 构建配置
            log.info("开始构建配置...");
            Config config = buildConfig();
            log.info("配置构建完成");

            // 2. 构建支付服务
            log.info("开始构建支付服务...");
            NativePayService service = new NativePayService.Builder().config(config).build();
            log.info("支付服务构建完成");

            // 3. 构建下单请求
            log.info("开始构建下单请求...");
            com.wechat.pay.java.service.payments.nativepay.model.PrepayRequest prepayRequest =
                new com.wechat.pay.java.service.payments.nativepay.model.PrepayRequest();

            // 设置支付金额
            Amount amount = new Amount();
            amount.setTotal(request.getTotalFee());
            amount.setCurrency("CNY");
            prepayRequest.setAmount(amount);
            
            // 设置商户订单号
            prepayRequest.setOutTradeNo(request.getOutTradeNo());
            
            // 设置商品描述
            prepayRequest.setDescription(request.getDescription());
            
            // 设置回调通知地址
            prepayRequest.setNotifyUrl(request.getNotifyUrl());
            
            // 设置APPID
            String appId = checkPlaceholder(wechatPayBase.getAppId(), "wxd678efh567hg6787");
            prepayRequest.setAppid(appId);
            log.info("使用的AppID: {}", appId);
            
            // 设置商户号
            String mchId = checkPlaceholder(wechatPayBase.getMerchantId(), "1900000109");
            prepayRequest.setMchid(mchId);
            log.info("使用的商户号: {}", mchId);
            log.info("下单请求构建完成: {}", prepayRequest);

            // 4. 调用下单接口
            try {
                log.info("开始调用下单接口...");
                com.wechat.pay.java.service.payments.nativepay.model.PrepayResponse wxResponse = service.prepay(prepayRequest);
                log.info("下单接口调用成功: {}", wxResponse);
                
                // 5. 获取支付二维码链接并生成二维码图片
                String codeUrl = wxResponse.getCodeUrl();
                String qrCodeImage = generateQRCode(codeUrl);
                
                // 6. 构建响应
                PrepayResponse response = new PrepayResponse();
                response.setCodeUrl(codeUrl);
                response.setQrCodeImage(qrCodeImage);
                response.setOutTradeNo(request.getOutTradeNo());
                
                return response;
            } catch (ServiceException e) {
                // 处理API请求失败
                log.error("微信支付下单API调用失败：code=[{}], message=[{}], response=[{}]", 
                          e.getErrorCode(), e.getErrorMessage(), e.getResponseBody(), e);
                throw new RuntimeException("微信支付下单失败：" + e.getErrorMessage());
            }
        } catch (Exception e) {
            log.error("微信支付下单失败", e);
            throw new RuntimeException("微信支付下单失败: " + e.getMessage(), e);
        }
    }

    @Override
    public QueryOrderResponse queryOrder(QueryOrderRequest request) {
        try {
            // 1. 构建配置
            Config config = buildConfig();

            // 2. 构建支付服务
            NativePayService service = new NativePayService.Builder().config(config).build();

            Transaction transaction;
            
            // 3. 根据传入的参数类型决定使用哪种查询方式
            if (request.getTransactionId() != null && !request.getTransactionId().isEmpty()) {
                // 使用微信支付订单号查询
                com.wechat.pay.java.service.payments.nativepay.model.QueryOrderByIdRequest queryRequest = 
                    new com.wechat.pay.java.service.payments.nativepay.model.QueryOrderByIdRequest();
                queryRequest.setMchid(wechatPayBase.getMerchantId());
                queryRequest.setTransactionId(request.getTransactionId());
                transaction = service.queryOrderById(queryRequest);
            } else if (request.getOutTradeNo() != null && !request.getOutTradeNo().isEmpty()) {
                // 使用商户订单号查询
                QueryOrderByOutTradeNoRequest queryRequest = new QueryOrderByOutTradeNoRequest();
                queryRequest.setMchid(wechatPayBase.getMerchantId());
                queryRequest.setOutTradeNo(request.getOutTradeNo());
                transaction = service.queryOrderByOutTradeNo(queryRequest);
            } else {
                throw new IllegalArgumentException("微信支付订单号和商户订单号不能同时为空");
            }
            
            // 4. 构建响应
            QueryOrderResponse response = new QueryOrderResponse();
            response.setAppid(transaction.getAppid());
            response.setMchid(transaction.getMchid());
            response.setOutTradeNo(transaction.getOutTradeNo());
            response.setTransactionId(transaction.getTransactionId());
            response.setTradeType(transaction.getTradeType() != null ? transaction.getTradeType().toString() : null);
            response.setTradeState(transaction.getTradeState() != null ? transaction.getTradeState().toString() : null);
            response.setTradeStateDesc(transaction.getTradeStateDesc());
            response.setBankType(transaction.getBankType());
            response.setAttach(transaction.getAttach());
            response.setSuccessTime(transaction.getSuccessTime());
            
            if (transaction.getAmount() != null) {
                response.setTotal(transaction.getAmount().getTotal());
                response.setCurrency(transaction.getAmount().getCurrency());
            }
            
            return response;
        } catch (ServiceException e) {
            log.error("微信支付查询订单API调用失败：code=[{}], message=[{}]", 
                      e.getErrorCode(), e.getErrorMessage(), e);
            throw new RuntimeException("微信支付查询订单失败：" + e.getErrorMessage());
        } catch (Exception e) {
            log.error("微信支付查询订单失败", e);
            throw new RuntimeException("微信支付查询订单失败", e);
        }
    }
    
    @Override
    public RefundResponse refund(RefundRequest request) {
        try {
            log.info("开始处理退款请求: {}", request);
            // 1. 构建配置
            Config config = buildConfig();
            
            // 2. 构建退款服务
            RefundService refundService = new RefundService.Builder().config(config).build();
            
            // 3. 创建退款请求
            CreateRequest createRequest = new CreateRequest();
            
            // 根据参数设置订单号（微信支付订单号或商户订单号，二选一）
            if (request.getTransactionId() != null && !request.getTransactionId().isEmpty()) {
                createRequest.setTransactionId(request.getTransactionId());
            } else if (request.getOutTradeNo() != null && !request.getOutTradeNo().isEmpty()) {
                createRequest.setOutTradeNo(request.getOutTradeNo());
            } else {
                throw new IllegalArgumentException("微信支付订单号和商户订单号不能同时为空");
            }
            
            // 设置退款单号
            createRequest.setOutRefundNo(request.getOutRefundNo());
            
            // 设置退款原因
            if (request.getReason() != null && !request.getReason().isEmpty()) {
                createRequest.setReason(request.getReason());
            }
            
            // 设置退款结果通知url
            if (request.getNotifyUrl() != null && !request.getNotifyUrl().isEmpty()) {
                createRequest.setNotifyUrl(request.getNotifyUrl());
            }
            
            // 设置金额信息
            AmountReq amount = new AmountReq();
            amount.setRefund(Long.valueOf(request.getRefundAmount()));
            amount.setTotal(Long.valueOf(request.getTotalAmount()));
            amount.setCurrency("CNY");
            createRequest.setAmount(amount);
            
            // 设置退款资金来源，可选
            if (request.getFundsAccount() != null && !request.getFundsAccount().isEmpty()) {
                createRequest.setFundsAccount(ReqFundsAccount.valueOf(request.getFundsAccount()));
            }
            
            // 设置退款商品信息，可选
            if (request.getGoodsDetails() != null && !request.getGoodsDetails().isEmpty()) {
                createRequest.setGoodsDetail(
                        request.getGoodsDetails().stream()
                                .map(this::convertGoodsDetail)
                                .collect(Collectors.toList())
                );
            }

            // 4. 调用退款接口
            log.info("调用微信支付退款API, 请求参数: {}", createRequest);
            Refund refund = refundService.create(createRequest);
            log.info("微信支付退款API返回: {}", refund);
            
            // 5. 构建响应
            RefundResponse response = new RefundResponse();
            response.setRefundId(refund.getRefundId());
            response.setOutRefundNo(refund.getOutRefundNo());
            response.setTransactionId(refund.getTransactionId());
            response.setOutTradeNo(refund.getOutTradeNo());
            response.setChannel(String.valueOf(refund.getChannel()));
            response.setUserReceivedAccount(refund.getUserReceivedAccount());
            response.setSuccessTime(refund.getSuccessTime());
            response.setCreateTime(refund.getCreateTime());
            response.setStatus(String.valueOf(refund.getStatus()));
            response.setFundsAccount(String.valueOf(refund.getFundsAccount()));
            
            if (refund.getAmount() != null) {
                response.setRefundAmount(Math.toIntExact(refund.getAmount().getRefund()));
                response.setTotalAmount(Math.toIntExact(refund.getAmount().getTotal()));
                // 关于from字段的处理，实际上微信返回的是一个复杂结构
                // 这里简化为字符串，实际使用时可能需要改为对象
                if (refund.getAmount().getFrom() != null && !refund.getAmount().getFrom().isEmpty()) {
                    response.setFrom("资金来源信息已返回"); // 实际使用时应该转换为更有意义的内容
                }
            }
            
            return response;
        } catch (ServiceException e) {
            log.error("微信支付退款API调用失败：code=[{}], message=[{}], response=[{}]", 
                      e.getErrorCode(), e.getErrorMessage(), e.getResponseBody(), e);
            throw new RuntimeException("微信支付退款失败：" + e.getErrorMessage());
        } catch (Exception e) {
            log.error("处理退款请求失败", e);
            throw new RuntimeException("处理退款请求失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 转换商品详情对象
     */
    private com.wechat.pay.java.service.refund.model.GoodsDetail convertGoodsDetail(RefundRequest.GoodsDetail goodsDetail) {
        com.wechat.pay.java.service.refund.model.GoodsDetail detail = new com.wechat.pay.java.service.refund.model.GoodsDetail();
        detail.setMerchantGoodsId(goodsDetail.getMerchantGoodsId());
        detail.setWechatpayGoodsId(goodsDetail.getWechatpayGoodsId());
        detail.setGoodsName(goodsDetail.getGoodsName());
        detail.setUnitPrice(Long.valueOf(goodsDetail.getUnitPrice()));
        detail.setRefundAmount(Long.valueOf(goodsDetail.getRefundAmount()));
        detail.setRefundQuantity(goodsDetail.getRefundQuantity());
        return detail;
    }
    
    /**
     * 构建微信支付配置
     */
    private Config buildConfig() {
        try {
            // 获取私钥内容而不是私钥路径
            String privateKeyContent = wechatPayBase.getPrivateKeyContent();
            
            // 检查配置值是否是占位符，如果是则替换为测试值
            String merchantId = checkPlaceholder(wechatPayBase.getMerchantId(), "1900000109");
            String merchantSerialNumber = checkPlaceholder(wechatPayBase.getMerchantSerialNumber(), "5157F09EFDC096DE15EBE81A47057A72XXXXXXXXX");
            String apiV3Key = checkPlaceholder(wechatPayBase.getApiV3Key(), "ABCDEFGHIJKLMNOPQRSTUVWXYZ123456");
            
            return new RSAAutoCertificateConfig.Builder()
                    .merchantId(merchantId)
                    .privateKey(privateKeyContent)  // 使用私钥内容
                    .merchantSerialNumber(merchantSerialNumber)
                    .apiV3Key(apiV3Key)
                    .build();
        } catch (Exception e) {
            log.error("构建微信支付配置失败", e);
            throw new RuntimeException("构建微信支付配置失败", e);
        }
    }
    
    /**
     * 检查参数是否为中文占位符，如果是则替换为测试值
     * 
     * @param value 配置值
     * @param defaultValue 默认值
     * @return 处理后的值
     */
    private String checkPlaceholder(String value, String defaultValue) {
        // 检查是否包含中文字符
        if (value == null || value.matches(".*[\\u4e00-\\u9fa5]+.*")) {
            log.warn("配置值[{}]包含中文，替换为测试值", value);
            return defaultValue;
        }
        return value;
    }
    
    /**
     * 生成二维码图片（Base64编码）
     */
    private String generateQRCode(String content) throws WriterException, IOException {
        int width = 300;
        int height = 300;
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
        hints.put(EncodeHintType.MARGIN, 1);
        
        BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints);
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(MatrixToImageWriter.toBufferedImage(bitMatrix), "PNG", outputStream);
        
        return "data:image/png;base64," + Base64.getEncoder().encodeToString(outputStream.toByteArray());
    }
}
