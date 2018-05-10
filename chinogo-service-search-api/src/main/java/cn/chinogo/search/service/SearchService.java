package cn.chinogo.search.service;


import cn.chinogo.pojo.ChinogoResult;
import cn.chinogo.pojo.SearchItem;
import cn.chinogo.pojo.SearchResult;

import java.io.IOException;

/**
 * @author chinotan
 */
public interface SearchService {

    /**
     * 导入全部商品索引
     *
     * @return
     */
    ChinogoResult importAllItems() throws IOException;

    /**
     * 查询商品
     * 根据关键词，function score query 权重分分页查询
     *
     * @param queryString 查询条件
     * @param page        第几页
     * @param rows        每页几条
     * @param sort 按价格排序（1：价格从低到高 -1：价格从高到低）
     * @param priceGt 价格区间（最小）
     * @param priceLte 价格区间（最大）
     * @return 返回商品Json
     * @throws Exception
     */

    SearchResult search(String queryString, Integer page, Integer rows, Integer sort, Long priceGt, Long priceLte) throws Exception;

    /**
     * 更新 item
     *
     * @param item
     * @return
     * @throws Exception
     */
    ChinogoResult updateItem(SearchItem item) throws Exception;

    /**
     * 删除item
     * 
     * @param id
     * @return
     * @throws Exception
     */
    ChinogoResult deleteItem(String id) throws Exception;
}
