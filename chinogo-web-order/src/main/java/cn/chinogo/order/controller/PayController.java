package cn.chinogo.order.controller;

import cn.chinogo.constant.Const;
import cn.chinogo.order.service.OrderService;
import cn.chinogo.pojo.TbOrder;
import com.alibaba.dubbo.config.annotation.Reference;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * 支付Controller
 *
 * @author chinotan
 */
@Api("支付Controller")
@RestController
@RequestMapping("/pay")
public class PayController {

    private static final Logger logger = LoggerFactory.getLogger(PayController.class);

    @Reference(version = Const.CHINOGO_ORDER_VERSION, timeout = 1000000, retries = 0)
    private OrderService orderService;

    @ApiOperation("进行支付")
    @PostMapping("/aliPay")
    public Object aliPay(String out_trade_no, @RequestParam(defaultValue = "chinogo商城支付") String subject,
                         @RequestParam(required = false) String body) {

        TbOrder order = orderService.getOrderById(out_trade_no);

        Map<String, Object> map = new HashMap<>(2);

        if (order == null) {
            return map.put("result", "订单错误，请重新下订单");
        }

        String pay = orderService.pay(out_trade_no, getPriceView(Long.parseLong(order.getPayment())), subject, body);

        map.put("result", pay);
        map.put("status", 0);

        return map;
    }

    private String getPriceView(Long price) {
        DecimalFormat df1 = new DecimalFormat("#.00");
        df1.setGroupingUsed(false);
        String format = df1.format(price / 100.00);
        return format;
    }

}


