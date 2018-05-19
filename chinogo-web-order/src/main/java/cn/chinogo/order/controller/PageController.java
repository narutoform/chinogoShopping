package cn.chinogo.order.controller;

import cn.chinogo.config.AliPayConfig;
import cn.chinogo.constant.Const;
import cn.chinogo.order.service.OrderService;
import cn.chinogo.pojo.TbOrder;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Api("支付结果页面处理及跳转")
@Controller
@RequestMapping("/pay/page")
public class PageController {

    private static final Logger logger = LoggerFactory.getLogger(PageController.class);

    @Reference(version = Const.CHINOGO_ORDER_VERSION, timeout = 1000000, retries = 0)
    private OrderService orderService;
    
    @Value("${aliPay.returnUrlSuccess}")
    String returnUrlSuccess;

    @Value("${aliPay.returnUrlFailed}")
    String returnUrlFailed;

    @ApiOperation("获取支付宝GET过来反馈信息")
    @GetMapping("/returnUrl")
    public String returnUrl(HttpServletRequest request) {
        //获取支付宝GET过来反馈信息
        Map<String, String> params = new HashMap<String, String>();
        Map<String, String[]> requestParams = request.getParameterMap();
        for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用
            try {
                valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            params.put(name, valueStr);
        }

        //调用SDK验证签名
        boolean signVerified = false;
        try {
            signVerified = AlipaySignature.rsaCheckV1(params, AliPayConfig.alipay_public_key, AliPayConfig.charset, AliPayConfig.sign_type);
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }

        //支付宝交易号
        String trade_no = null;

        //商户订单号
        String out_trade_no = null;
        
        //付款金额
        String total_amount = null;
        
        // 判断是否支付成功
        if (signVerified) {
            try {
                out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            try {
                trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            try {
                total_amount = new String(request.getParameter("total_amount").getBytes("ISO-8859-1"), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            // 更新订单状态
            TbOrder order = new TbOrder();
            order.setOrderId(out_trade_no);
            // 设置状态
            order.setStatus(Const.PAYMENT);
            // 设置支付类型
            order.setPaymentType(Const.ALIPAY_PAYMENT);
            Date date = new Date();
            // 设置支付时间
            order.setPaymentTime(date);
            // 设置更新时间
            order.setUpdateTime(date);
            orderService.updateOrder(order);
            
        } else {
            return "redirect:" + returnUrlFailed;
        }

        return "redirect:" + returnUrlSuccess + "?price=" + total_amount;
    }

}
