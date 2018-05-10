package cn.chinogo.cart.service.impl;

import cn.chinogo.cart.service.CartService;
import cn.chinogo.constant.Const;
import cn.chinogo.mapper.TbItemMapper;
import cn.chinogo.pojo.ChinogoResult;
import cn.chinogo.pojo.TbCart;
import cn.chinogo.pojo.TbItem;
import cn.chinogo.redis.service.JedisClient;
import cn.chinogo.utils.FastJsonConvert;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 购物车操作实现
 *
 * @author chinotan
 */
@Service(version = Const.CHINOGO_CART_VERSION, timeout = 100000, retries = 0)
@Transactional(rollbackFor = Exception.class)
public class CartServiceImpl implements CartService {

    private static final Logger logger = LoggerFactory.getLogger(CartServiceImpl.class);

    @Value("${redisKey.prefix.cart_info_profix}")
    private String CART_INFO_PROFIX;
    @Value("${redisKey.prefix.redis_cart_expire_time}")
    private Integer REDIS_CART_EXPIRE_TIME;
    @Value("${redisKey.prefix.item_info_profix}")
    private String ITEM_INFO_PROFIX;
    @Value("${redisKey.prefix.item_info_base_suffix}")
    private String ITEM_INFO_BASE_SUFFIX;

    @Autowired
    private TbItemMapper itemMapper;

    @Reference(version = Const.CHINOGO_REDIS_VERSION)
    private JedisClient jedisClient;

    @Override
    public ChinogoResult addCartItem(String productId, Integer productNum, String uuid, Boolean LocalSync) {
        String key = CART_INFO_PROFIX + uuid;
        String cartInfoString = null;
        try {
            cartInfoString = jedisClient.get(key);
        } catch (Exception e) {
            logger.error("Redis出错!", e);
        }

        TbItem item = null;

        try {
            String redisItem = jedisClient.get(ITEM_INFO_PROFIX + productId + ITEM_INFO_BASE_SUFFIX);

            if (StringUtils.isNotBlank(redisItem)) {
                item = FastJsonConvert.convertJSONToObject(redisItem, TbItem.class);
            } else {
                item = itemMapper.selectById(productId);
            }
        } catch (Exception e) {
            logger.error("Redis出错!", e);
        }

        TbCart cartInfo = new TbCart();

        cartInfo.setProductId(item.getId().toString());
        cartInfo.setProductName(item.getTitle());
        String[] split = item.getImage().split(",");
        cartInfo.setProductImg(split[0]);
        cartInfo.setColour(item.getColour());
        cartInfo.setProductNum(productNum);
        cartInfo.setProductPrice(item.getPrice());
        cartInfo.setSize(item.getSize());
        cartInfo.setWeight(item.getWeight());
        cartInfo.setChecked("1");

        if (StringUtils.isBlank(cartInfoString)) {

            ArrayList<TbCart> cartInfos = new ArrayList<>();

            cartInfos.add(cartInfo);

            try {
                jedisClient.set(key, FastJsonConvert.convertObjectToJSON(cartInfos));
                jedisClient.expire(key, REDIS_CART_EXPIRE_TIME);
            } catch (Exception e) {
                logger.error("Redis出错!", e);
            }

            return ChinogoResult.build(200, "ok", cartInfo);

        } else {
            List<TbCart> list = FastJsonConvert.convertJSONToArray(cartInfoString, TbCart.class);
            if (list != null && list.size() > 0) {
                boolean flag = true;
                for (int i = 0; i < list.size(); i++) {
                    TbCart info = list.get(i);
                    if (info.getProductId().equals(item.getId().toString())) {
                        if (!LocalSync) {
                            info.setProductNum(info.getProductNum());
                        } else {
                            info.setProductNum(info.getProductNum() + productNum);
                        }
                        list.remove(i);
                        list.add(info);
                        flag = false;

                        logger.debug("商品已经存在数量加" + productNum + "个 uuid:" + uuid);
                    }
                }
                if (flag) {
                    list.add(cartInfo);
                    logger.debug("商品不存在数量为" + productNum + "个 uuid:" + uuid);
                }
            }

            logger.debug("商品添加完成 购物车" + list.size() + "件商品 uuid:" + uuid);

            try {
                jedisClient.set(key, FastJsonConvert.convertObjectToJSON(list));
                jedisClient.expire(key, REDIS_CART_EXPIRE_TIME);
            } catch (Exception e) {
                logger.error("Redis出错!", e);
            }

            return ChinogoResult.build(200, "ok", cartInfo);
        }
    }

    @Override
    public ChinogoResult addCartItem(String productId, Integer productNum, String userId) {
        return this.addCartItem(productId, productNum, userId, false);
    }

    @Override
    public List<TbCart> getCartItemList(String uuid) {
        String cartInfoString = jedisClient.get(CART_INFO_PROFIX + uuid);

        if (StringUtils.isNotBlank(cartInfoString)) {
            List<TbCart> cartInfos = FastJsonConvert.convertJSONToArray(cartInfoString, TbCart.class);

            return cartInfos;
        }

        return null;
    }

    @Override
    public ChinogoResult deleteCartItem(String productId, String uuid) {
        String key = CART_INFO_PROFIX + uuid;

        List<TbCart> cartInfoList = getCartItemList(uuid);
        if (cartInfoList == null || cartInfoList.size() == 0) {
            return ChinogoResult.build(400, "购物车没有此商品 请不要非法操作!");
        }

        for (int i = 0; i < cartInfoList.size(); i++) {
            TbCart cart = cartInfoList.get(i);
            if (cart.getProductId().equals(productId)) {
                cartInfoList.remove(i);
            }
        }
        // 如果当前购物车没有元素的话，就从缓存中全部删除
        if (cartInfoList.size() == 0) {
            deleteCartAll(uuid);
            return ChinogoResult.ok();
        }

        jedisClient.set(key, FastJsonConvert.convertObjectToJSON(cartInfoList));
        jedisClient.expire(key, REDIS_CART_EXPIRE_TIME);

        return ChinogoResult.ok();
    }

    @Override
    public ChinogoResult deleteCartAll(String uuid) {
        String key = CART_INFO_PROFIX + uuid;
        jedisClient.del(key);
        
        return ChinogoResult.ok();
    }

    @Override
    public ChinogoResult updateCarItem(String productId, Integer productNum, String checked, String uuid) {

        String key = CART_INFO_PROFIX + uuid;

        List<TbCart> cartInfoList = getCartItemList(uuid);
        if (cartInfoList == null || cartInfoList.size() == 0) {
            return ChinogoResult.build(400, "购物车没有此商品 请不要非法操作!");
        }

        for (int i = 0; i < cartInfoList.size(); i++) {
            TbCart cart = cartInfoList.get(i);
            if (cart.getProductId().equals(productId)) {
                cart.setProductNum(productNum);
                // 判断是否在购物车中勾选商品
                if (StringUtils.isNotBlank(checked)) {
                    cart.setChecked(checked);
                }
                cartInfoList.set(i, cart);
            }
        }

        jedisClient.set(key, FastJsonConvert.convertObjectToJSON(cartInfoList));
        jedisClient.expire(key, REDIS_CART_EXPIRE_TIME);

        return ChinogoResult.ok();
    }

    @Override
    public ChinogoResult selectCartAll(boolean checkAll, String uuid) {
        String key = CART_INFO_PROFIX + uuid;

        List<TbCart> cartInfoList = getCartItemList(uuid);
        if (cartInfoList == null || cartInfoList.size() == 0) {
            return ChinogoResult.build(400, "购物车没有此商品 请不要非法操作!");
        }

        for (int i = 0; i < cartInfoList.size(); i++) {
            TbCart cart = cartInfoList.get(i);
            cart.setChecked((checkAll ? "1" : "0"));
            cartInfoList.set(i, cart);
        }

        jedisClient.set(key, FastJsonConvert.convertObjectToJSON(cartInfoList));
        jedisClient.expire(key, REDIS_CART_EXPIRE_TIME);
        
        return ChinogoResult.ok();
    }
}
