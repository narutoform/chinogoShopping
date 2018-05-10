package cn.chinogo.cart.service;


import cn.chinogo.pojo.ChinogoResult;
import cn.chinogo.pojo.TbCart;

import java.util.List;

/**
 * 购物车相关操作 Service
 */
public interface CartService {

    /**
     * 添加购物车
     * 
     * @param productId 商品id
     * @param productNum 购买数量
     * @param userId 用户id
     * @param LocalSync 判断是否需要将用户本地的购物车和云端相同的产品进行取舍(如何为true则取本地商品数量，false则取本地商品加云端商品数量)
     * @return
     */
    ChinogoResult addCartItem(String productId, Integer productNum, String userId, Boolean LocalSync);
    
    ChinogoResult addCartItem(String productId, Integer productNum, String userId);

    List<TbCart> getCartItemList(String uuid);

    ChinogoResult deleteCartItem(String productId, String uuid);

    /**
     * 从缓存中删除全部购物车
     * 
     * @param uuid
     * @return
     */
    ChinogoResult deleteCartAll(String uuid);

    ChinogoResult updateCarItem(String productId, Integer productNum, String checked, String uuid);

    ChinogoResult selectCartAll(boolean checkAll, String uuid);

}
