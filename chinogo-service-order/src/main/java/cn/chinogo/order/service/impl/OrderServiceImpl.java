package cn.chinogo.order.service.impl;

import cn.chinogo.cart.service.CartService;
import cn.chinogo.constant.Const;
import cn.chinogo.item.service.ItemService;
import cn.chinogo.mapper.TbOrderItemMapper;
import cn.chinogo.mapper.TbOrderMapper;
import cn.chinogo.order.service.OrderService;
import cn.chinogo.pojo.*;
import cn.chinogo.sso.service.UserService;
import cn.chinogo.utils.AliPayUtils;
import cn.chinogo.utils.FastJsonConvert;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.toolkit.IdWorker;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 订单Service
 *
 * @author chinotan
 */
@Service(version = Const.CHINOGO_ORDER_VERSION, timeout = 1000000, retries = 0)
@Transactional(rollbackFor = Exception.class)
public class OrderServiceImpl implements OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Reference(version = Const.CHINOGO_SSO_VERSION)
    private UserService userService;

    @Reference(version = Const.CHINOGO_ITEM_VERSION)
    private ItemService itemService;

    @Reference(version = Const.CHINOGO_CART_VERSION)
    private CartService cartService;

    @Autowired
    private TbOrderItemMapper orderItemMapper;

    @Autowired
    private TbOrderMapper orderMapper;

    @Value("${redisKey.prefix.cart_info_profix}")
    private String CART_INFO_PROFIX;
    @Value("${redisKey.prefix.PostFee}")
    private String POSTFEE;

    @Override
    public TbOrder saveOrder(String token, String addrId, String tbCartList) {

        TbUser user = getCurrentUser(token);

        final TbOrder order = new TbOrder();
        long orderId = IdWorker.getId();
        //设置订单id
        order.setOrderId(orderId + "");
        //设置用户id
        order.setUserId(user.getId());
        //设置地址id
        order.setAddrId(addrId);
        //设置邮费
        order.setPostFee(Integer.parseInt(POSTFEE));
        //设置状态
        order.setStatus(Const.NON_PAYMENT);
        //设置没有评价
        order.setBuyerRate(Const.EVALUATE_NO);
        //设置订单创建时间
        order.setCreateTime(new Date());
        order.setUpdateTime(new Date());

        Long payment = 0L;

        Integer weight = 0;

        List<TbCart> cartList = cartService.getCartItemList(user.getId().toString());

        List<TbCart> tbCarts = FastJsonConvert.convertJSONToArray(tbCartList, TbCart.class);

        // 保存订单项
        if (tbCarts.size() > 0) {
            for (int i = 0; i < tbCarts.size(); i++) {
                TbCart tbCart = tbCarts.get(i);
                TbItem item = itemService.getItemById(Long.parseLong(tbCart.getProductId()));

                TbOrderItem orderItem = new TbOrderItem();
                orderItem.setOrderId(orderId + "");
                orderItem.setItemId(item.getId() + "");
                orderItem.setTitle(item.getTitle());
                orderItem.setNum(tbCart.getProductNum());
                orderItem.setPicPath(item.getImage().split(",")[0]);
                orderItem.setPrice(item.getPrice());
                orderItem.setTotalFee(item.getPrice());
                orderItem.setWeights(item.getWeight().toString());
                // 记录日志
                orderItemMapper.insert(orderItem);

                logger.info("保存订单项,订单:" + orderItem.toString());

                payment += item.getPrice();
                weight += item.getWeight();

                // 移除购物车选中商品
                if (cartList != null && cartList.size() > 0) {
                    for (TbCart cart : cartList) {
                        if (cart.getProductId().equals(item.getId().toString())) {
                            cartService.deleteCartItem(cart.getProductId(), user.getId().toString());
                        }
                    }
                }

                // 已经购买的商品的库存数量进行相应的减少
                item.setNum(item.getNum() - tbCart.getProductNum());
                itemService.updateItemBase(item);
            }
        }

        //设置总金额
        order.setPayment(payment + Long.parseLong(POSTFEE) + "");
        //设置总重
        order.setTotalWeight(weight.toString());
        // 保存订单
        orderMapper.insert(order);

        return order;
    }

    /**
     * 根据userId得到用户的订单信息（按订单创建时间倒叙排序）
     *
     * @param token
     * @return
     */
    @Override
    public Page<ShowOrder> getOrder(String token, Page<ShowOrder> page) {

        TbUser user = getCurrentUser(token);

        Wrapper<TbOrder> wrapper = new EntityWrapper<>();
        wrapper.where("user_id={0}", user.getId());
        wrapper.orderBy("create_time", false);
        RowBounds rowBounds = new RowBounds(page.getOffset(), page.getLimit());

        List<TbOrder> tbOrders = orderMapper.selectPage(rowBounds, wrapper);

        List<ShowOrder> list = new ArrayList<>();

        for (TbOrder order : tbOrders) {
            // 查询某个订单对应的商品
            Wrapper<TbOrderItem> itemWrapper = new EntityWrapper<>();
            itemWrapper.eq("order_id", order.getOrderId());
            List<TbOrderItem> orderItems = orderItemMapper.selectList(itemWrapper);

            ShowOrder showOrder = new ShowOrder();
            showOrder.setOrder(order);
            showOrder.setOrderItems(orderItems);

            list.add(showOrder);
        }

        page.setRecords(list);
        page.setTotal(orderMapper.selectCount(wrapper));

        return page;
    }

    /**
     * 根据订单id得到订单
     *
     * @param orderId
     * @return
     */
    @Override
    public TbOrder getOrderById(String orderId) {
        TbOrder order = orderMapper.selectById(orderId);

        return order;
    }

    /**
     * 根据订单id得到订单相应的商品列表
     *
     * @param orderId
     * @return
     */
    @Override
    public List<TbOrderItem> getOrderItemById(String orderId) {
        Wrapper<TbOrderItem> wrapper = new EntityWrapper<>();
        wrapper.eq("order_id", orderId);

        List<TbOrderItem> orderItems = orderItemMapper.selectList(wrapper);

        return orderItems;
    }

    /**
     * 更新订单
     *
     * @param order
     * @return
     */
    @Override
    public int updateOrder(TbOrder order) {
        Integer integer = orderMapper.updateById(order);

        return integer;
    }

    /**
     * 刪除订单
     *
     * @param orderId
     * @return
     */
    @Override
    public int deleteOrder(String orderId) {
        Integer integer = orderMapper.deleteById(orderId);
        
        // 刪除该订单对应的订单商品
        Wrapper<TbOrderItem> wrapper = new EntityWrapper<>();
        wrapper.eq("order_id", orderId);
        orderItemMapper.delete(wrapper);

        return integer;
    }

    /**
     * 进行支付宝支付
     *
     * @param out_trade_no 商户订单号，商户网站订单系统中唯一订单号，必填
     * @param total_amount 付款金额，必填
     * @param subject      订单名称，必填
     * @param body         商品描述，可空
     * @return
     */
    @Override
    public String pay(String out_trade_no, String total_amount, String subject, String body) {
        String pay = AliPayUtils.pay(out_trade_no, total_amount, subject, body);

        return pay;
    }


    private TbUser getCurrentUser(String token) {
        ChinogoResult result = userService.token(token);
        if (result.getData() == null) {
            logger.error("用户没有登录!");
            return null;
        }

        String data = (String) result.getData();
        TbUser user = FastJsonConvert.convertJSONToObject(data, TbUser.class);

        return user;
    }
}
