package cn.chinogo.item.service;

import cn.chinogo.pojo.*;
import com.baomidou.mybatisplus.plugins.Page;

import java.util.List;

/**
 * 商品 Service
 */
public interface ItemService {

    /**
     * 根据商品id得到商品
     *
     * @param itemId
     * @return
     */
    TbItem getItemById(Long itemId);

    /**
     * 根据商品id得到商品描述
     *
     * @param itemId
     * @return
     */
    TbItemDesc getItemDescById(Long itemId);

    /**
     * 根据商品id得到商品规格
     *
     * @param itemId
     * @return
     */
    TbItemParamItem getItemParamItemByItemId(Long itemId);

    /**
     * 得到商品列表
     *
     * @param page
     * @return
     */
    Page<TbItemWithCategory> getItemListWithCid(Page<TbItemWithCategory> page);

    /**
     * 根据标题得到商品列表
     *
     * @param page
     * @param title
     * @return
     */
    Page<TbItemWithCategory> getItemListWithCidLikeTitle(Page<TbItemWithCategory> page, String title);

    /**
     * 获得商品规格列表带着category, 默认按更新时间倒序排列
     *
     * @param page
     * @return
     */
    Page<TbItemParamWithCid> getItemParamListWithCid(Page<TbItemParamWithCid> page);

    /**
     * 根据分类名称得到商品规格列表
     * 
     * @param page
     * @param categoryName
     * @return
     */
    Page<TbItemParamWithCid> getItemParamListWithCidLikeCidName(Page<TbItemParamWithCid> page, String categoryName);

    /**
     * 获得商品数量
     *
     * @return
     */
    Integer getItemCount();

    /**
     * 存储商品
     *
     * @param item
     * @param itemDesc
     * @param itemParamItem
     * @return
     */
    Long saveItem(TbItem item, TbItemDesc itemDesc, TbItemParamItem itemParamItem);

    /**
     * 更新商品
     *
     * @param item
     * @param itemDesc
     * @param itemParamItem
     * @return
     */
    Long updateItem(TbItem item, TbItemDesc itemDesc, TbItemParamItem itemParamItem);

    /**
     * 更新基础商品
     * 
     * @param item
     * @return
     */
    Long updateItemBase(TbItem item);

    /**
     * 更新商品参数规格
     * 
     * @param itemParam
     * @return
     */
    Integer updateItemParam(TbItemParam itemParam);

    /**
     * 增加商品参数规格
     *
     * @param itemParam
     * @return
     */
    Long saveItemParam(TbItemParam itemParam);

    /**
     * 根据商品分类id得到商品参数
     *
     * @param itemCatId
     * @return
     */
    TbItemParam getItemParamByItemCatId(Long itemCatId) throws Exception;

    /**
     * 根据商品ids批量删除商品
     *
     * @param ids
     * @return
     */
    Integer deleteItemById(List<Long> ids);

    /**
     * 根据商品参数id批量删除商品参数
     * 
     * @param ids
     * @return
     */
    Integer deleteItemParamById(List<Long> ids);

    /**
     * 更新商品状态（上架，下架）
     *
     * @param item
     * @return
     */
    Integer updateItemStatus(TbItem item);

    /**
     * 插入到es
     * @param id
     * @return
     */
    void insertIntoES(Long id);

    /**
     * 更新到es
     * @param id
     * @return
     */
    void updateIntoES(Long id);

}
