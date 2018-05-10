package cn.chinogo.order.service;

import cn.chinogo.pojo.ShowOrder;
import cn.chinogo.pojo.TbItem;
import cn.chinogo.pojo.TbOrder;
import cn.chinogo.pojo.TbOrderItem;
import com.baomidou.mybatisplus.plugins.Page;

import java.util.List;

/**
 * @author chinotan
 */
public interface OrderService {
    /**
     * 提交订单
     *
     * @param userCookieValue 用户登录Cookie
     * @param addrId          用户地址id
     * @return
     */
    TbOrder saveOrder(String userCookieValue, String addrId, String tbCartList);

    /**
     * 根据userId得到用户的订单信息（按订单创建时间倒叙排序）
     *
     * @param token
     * @return
     */
    Page<ShowOrder> getOrder(String token, Page<ShowOrder> page);

    /**
     * 根据订单id得到订单
     *
     * @param orderId
     * @return
     */
    TbOrder getOrderById(String orderId);

    /**
     * 根据订单id得到订单相应的商品列表
     *
     * @param orderId
     * @return
     */
    List<TbOrderItem> getOrderItemById(String orderId);

    /**
     * 更新订单
     *
     * @param order
     * @return
     */
    int updateOrder(TbOrder order);

    /**
     * 刪除订单
     *
     * @param orderId
     * @return
     */
    int deleteOrder(String orderId);

    /**
     * 进行支付宝支付
     *
     * @param out_trade_no 商户订单号，商户网站订单系统中唯一订单号，必填
     * @param total_amount 付款金额，必填
     * @param subject      订单名称，必填
     * @param body         商品描述，可空
     * @return
     */
    String pay(String out_trade_no, String total_amount, String subject, String body);

}
