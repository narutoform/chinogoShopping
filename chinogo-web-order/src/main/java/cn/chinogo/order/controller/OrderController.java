package cn.chinogo.order.controller;

import cn.chinogo.constant.Const;
import cn.chinogo.order.service.OrderService;
import cn.chinogo.pojo.ShowOrder;
import cn.chinogo.pojo.TbCart;
import cn.chinogo.pojo.TbOrder;
import cn.chinogo.pojo.TbOrderItem;
import cn.chinogo.utils.FastJsonConvert;
import com.alibaba.dubbo.config.annotation.Reference;
import com.baomidou.mybatisplus.plugins.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订单Controller
 *
 * @author chinotan
 */
@Api("订单Controller")
@RestController
@RequestMapping("/order")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Reference(version = Const.CHINOGO_ORDER_VERSION, timeout = 1000000, retries = 0)
    private OrderService orderService;


    @ApiOperation("获取订单列表")
    @PostMapping("/list")
    public Object getOrder(@CookieValue(Const.TOKEN_LOGIN) String token, @RequestParam(defaultValue = "1") Integer current,
                           @RequestParam(defaultValue = "5") Integer size) {
        Page<ShowOrder> orderPage = orderService.getOrder(token, new Page<ShowOrder>(current, size));

        return orderPage;
    }

    @ApiOperation("获取订单的商品列表")
    @PostMapping("/item/list")
    public Object getOrderItemById(String orderId) {
        List<TbOrderItem> orderItems = orderService.getOrderItemById(orderId);

        return orderItems;
    }

    @ApiOperation("删除订单")
    @PostMapping("/delete/{orderId}")
    public Object deleteOrder(@PathVariable("orderId") String orderId) {
        int i = orderService.deleteOrder(orderId);

        Map<String, Object> map = new HashMap<>();
        
        if (i == 1) {
            map.put("msg", "删除订单成功");
            map.put("status", "0");
            map.put("result", "suc");

            return map;
        }
        map.put("msg", "删除订单失败");
        map.put("status", "-1");
        map.put("result", "failed");

        return map;
    }

    /**
     * 提交订单
     *
     * @param addrId 用户地址id
     */
    @ApiOperation("提交订单")
    @PostMapping("/createOrder")
    public Object getPay(String addrId, String tbCartList, @CookieValue(Const.TOKEN_LOGIN) String token) {

        List<TbCart> tbCarts = FastJsonConvert.convertJSONToArray(tbCartList, TbCart.class);

        TbOrder order = orderService.saveOrder(token, addrId, tbCartList);

        Map<String, Object> map = new HashMap<>();

        if (order != null) {
            map.put("msg", "创建订单成功");
            map.put("status", "0");
            map.put("result", order);

            return map;
        }
        map.put("msg", "创建订单失败");
        map.put("status", "-1");
        map.put("result", "failed");

        return map;
    }

}
