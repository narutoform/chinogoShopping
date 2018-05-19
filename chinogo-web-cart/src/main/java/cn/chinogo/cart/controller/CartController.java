package cn.chinogo.cart.controller;

import cn.chinogo.cart.service.CartService;
import cn.chinogo.constant.Const;
import cn.chinogo.pojo.ChinogoResult;
import cn.chinogo.pojo.TbCart;
import cn.chinogo.pojo.TbUser;
import cn.chinogo.redis.service.JedisClient;
import cn.chinogo.utils.FastJsonConvert;
import com.alibaba.dubbo.config.annotation.Reference;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 购物车 Controller
 *
 * @author chinotan
 */
@Api("购物车操作")
@RestController
@RequestMapping("/cart")
public class CartController {

    private static final Logger logger = LoggerFactory.getLogger(CartController.class);

    @Reference(version = Const.CHINOGO_CART_VERSION, retries = 0, timeout = 1000000)
    private CartService cartService;

    @Reference(version = Const.CHINOGO_REDIS_VERSION, retries = 0, timeout = 1000000)
    private JedisClient jedisClient;

    @Value("${redisKey.prefix.user_session}")
    private String USER_SESSION;

    @ApiOperation("获取购物车列表")
    @GetMapping("/list")
    public Object showCart(@CookieValue(Const.TOKEN_LOGIN) String token) {

        TbUser user = getUser(token);

        List<TbCart> cartInfos = new ArrayList<>();
        if (user != null) {
            cartInfos = cartService.getCartItemList(user.getId().toString());
        }

        Map<String, Object> map = new HashMap<>();

        if (cartInfos == null || cartInfos.size() == 0) {
            map.put("count", 0);
            map.put("msg", "购物车为空");
            map.put("status", "0");
            map.put("result", cartInfos);

            return map;
        }

        map.put("count", cartInfos.size());
        map.put("msg", "suc");
        map.put("status", "1");
        map.put("result", cartInfos);

        return map;
    }

    @ApiOperation("加入购物车")
    @PostMapping("/add")
    public Object addCart(String productId,
                          @RequestParam(required = false, defaultValue ="1", value = "productNum") Integer productNum, 
                          @CookieValue(Const.TOKEN_LOGIN) String token) {
        TbUser user = getUser(token);

        Map<String, Object> map = new HashMap<>();

        if (user == null) {
            map.put("msg", "加入失败");
            map.put("status", "-1");
            map.put("result", "failed");

            return map;
        } else {
            ChinogoResult result = cartService.addCartItem(productId, productNum, user.getId().toString(), false);

            if (result.isOK()) {
                map.put("msg", "加入成功");
                map.put("status", "0");
                map.put("result", "suc");
            }

            return map;
        }
    }

    @ApiOperation("加入购物车1")
    @PostMapping("/add1")
    public Object addCart1(@RequestBody String productMsg, @CookieValue(Const.TOKEN_LOGIN) String token) {
        TbUser user = getUser(token);

        List<Map> lists = FastJsonConvert.convertJSONToArray(productMsg, Map.class);

        for (Map<String, Object> map : lists) {
            String productId = map.get("productId").toString();
            Integer productNum = Integer.parseInt(map.get("productNum").toString());
            cartService.addCartItem(productId, productNum, user.getId().toString());
        }

        Map<String, Object> map = new HashMap<>();
        map.put("msg", "加入成功");
        map.put("status", "0");
        map.put("result", "suc");

        return map;
    }

    /**
     * 根据商品id和数量对购物车增加商品或减少商品
     *
     * @return
     */
    @ApiOperation("根据商品id和数量对购物车增加商品或减少商品")
    @PostMapping("/decreOrIncre")
    public Object decreOrIncre(String productId, Integer productNum, String checked, @CookieValue(Const.TOKEN_LOGIN) String token) {
        TbUser user = getUser(token);
        Map<String, Object> map = new HashMap<>();

        if (user == null) {
            map.put("msg", "更新失败");
            map.put("status", "-1");
            map.put("result", "failed");

            return map;
        } else {
            ChinogoResult result = cartService.updateCarItem(productId, productNum, checked, user.getId().toString());

            if (result.isOK()) {
                map.put("msg", "更新成功");
                map.put("status", "0");
                map.put("result", "suc");

                return map;
            }
            map.put("msg", "更新失败");
            map.put("status", "-1");
            map.put("result", "failed");

            return map;
        }
    }

    /**
     * 全选全部购物车
     *
     * @return
     */
    @ApiOperation("全选全部购物车")
    @PostMapping("/select/all")
    public Object selectCartAll(boolean checkAll, @CookieValue(Const.TOKEN_LOGIN) String token) {
        TbUser user = getUser(token);
        Map<String, Object> map = new HashMap<>();

        if (user == null) {
            map.put("msg", "全选失败");
            map.put("status", "-1");
            map.put("result", "failed");

            return map;
        } else {
            ChinogoResult result = cartService.selectCartAll(checkAll, user.getId().toString());

            if (result.isOK()) {
                map.put("msg", "全选全部购物车成功");
                map.put("status", "0");
                map.put("result", "suc");

                return map;
            }
            map.put("msg", "全选失败");
            map.put("status", "-1");
            map.put("result", "failed");

            return map;
        }
    }

    /**
     * 删除购物车
     *
     * @return
     */
    @ApiOperation("删除购物车")
    @PostMapping("/delete/{productId}")
    public Object deleteCartItem(@PathVariable("productId") String productId, @CookieValue(Const.TOKEN_LOGIN) String token) {
        TbUser user = getUser(token);
        Map<String, Object> map = new HashMap<>();

        if (user == null) {
            map.put("msg", "删除失败");
            map.put("status", "-1");
            map.put("result", "failed");

            return map;
        } else {
            ChinogoResult result = cartService.deleteCartItem(productId, user.getId().toString());

            if (result.isOK()) {
                map.put("msg", "删除成功");
                map.put("status", "0");
                map.put("result", "suc");

                return map;
            }
            map.put("msg", "删除失败");
            map.put("status", "-1");
            map.put("result", "failed");

            return map;
        }
    }

    /**
     * 删除全部购物车
     *
     * @return
     */
    @ApiOperation("删除购物车")
    @PostMapping("/delete/all")
    public Object deleteCartAll(@CookieValue(Const.TOKEN_LOGIN) String token) {
        TbUser user = getUser(token);
        Map<String, Object> map = new HashMap<>();

        if (user == null) {
            map.put("msg", "删除失败");
            map.put("status", "-1");
            map.put("result", "failed");

            return map;
        } else {
            ChinogoResult result = cartService.deleteCartAll(user.getId().toString());

            if (result.isOK()) {
                map.put("msg", "删除全部购物车成功");
                map.put("status", "0");
                map.put("result", "suc");

                return map;
            }
            map.put("msg", "删除失败");
            map.put("status", "-1");
            map.put("result", "failed");

            return map;
        }
    }

    private TbUser getUser(String token) {
        TbUser user = null;
        String userJson = null;

        if (StringUtils.isNoneEmpty(token)) {
            try {
                userJson = jedisClient.get(USER_SESSION + token);
            } catch (Exception e) {
                logger.error("Redis 错误", e);
            }

            if (StringUtils.isNoneEmpty(userJson)) {
                user = FastJsonConvert.convertJSONToObject(userJson, TbUser.class);
            }
        }

        return user;
    }
}
