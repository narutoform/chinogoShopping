package cn.chinogo.item.service.impl;

import cn.chinogo.constant.Const;
import cn.chinogo.exception.BasisException;
import cn.chinogo.item.service.ItemService;
import cn.chinogo.mapper.*;
import cn.chinogo.pojo.*;
import cn.chinogo.redis.service.JedisClient;
import cn.chinogo.search.service.SearchService;
import cn.chinogo.utils.FastJsonConvert;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 商品 Service 实现
 * 
 * @author chinotan
 */
@Service(version = Const.CHINOGO_ITEM_VERSION, timeout = 1000000)
@Transactional(rollbackFor = Exception.class)
public class ItemServiceImpl implements ItemService {

    private static final Logger logger = LoggerFactory.getLogger(ItemServiceImpl.class);

    @Autowired
    private TbItemMapper itemMapper;

    @Autowired
    private TbItemDescMapper itemDescMapper;

    @Autowired
    private TbItemParamItemMapper itemParamItemMapper;

    @Autowired
    private TbItemParamMapper itemParamMapper;

    @Autowired
    private TbCategoryMapper categoryMapper;

    @Reference(version = Const.CHINOGO_REDIS_VERSION, timeout = 3000000)
    private JedisClient jedisClient;
    
    @Reference(version = Const.CHINOGO_SEARCH_VERSION, timeout = 3000000)
    private SearchService searchService;

    @Value("${redisKey.prefix.item_info_profix}")
    private String ITEM_INFO_PROFIX;

    @Value("${redisKey.suffix.item_info_base_suffix}")
    private String ITEM_INFO_BASE_SUFFIX;

    @Value("${redisKey.suffix.item_info_desc_suffix}")
    private String ITEM_INFO_DESC_SUFFIX;

    @Value("${redisKey.suffix.item_info_param_suffix}")
    private String ITEM_INFO_PARAM_SUFFIX;

    @Value("${redisKey.expire_time}")
    private Integer REDIS_EXPIRE_TIME;

    @Override
    public TbItem getItemById(Long itemId) {
        String key = ITEM_INFO_PROFIX + itemId + ITEM_INFO_BASE_SUFFIX;

        try {
            String jsonItem = jedisClient.get(key);

            if (StringUtils.isNotBlank(jsonItem)) {
                return FastJsonConvert.convertJSONToObject(jsonItem, TbItem.class);
            } else {
                logger.error("Redis 查询不到 key:" + key);
            }
        } catch (Exception e) {
            logger.error("商品信息 获取缓存报错", e);
        }

        TbItem item = itemMapper.selectById(itemId);

        try {
            jedisClient.set(key, FastJsonConvert.convertObjectToJSON(item));

            jedisClient.expire(key, REDIS_EXPIRE_TIME);

            logger.info("Redis 缓存商品信息 key:" + key);

        } catch (Exception e) {
            logger.error("缓存错误商品ID:" + itemId, e);
        }

        return item;
    }

    @Override
    public TbItemDesc getItemDescById(Long itemId) {
        String key = ITEM_INFO_PROFIX + itemId + ITEM_INFO_DESC_SUFFIX;

        try {
            String jsonItem = jedisClient.get(key);

            if (StringUtils.isNotBlank(jsonItem)) {
                return FastJsonConvert.convertJSONToObject(jsonItem, TbItemDesc.class);
            } else {
                logger.error("Redis 查询不到 key:" + key);
            }
        } catch (Exception e) {
            logger.error("商品详情 获取缓存报错", e);
        }

        TbItemDesc itemDesc = itemDescMapper.selectById(itemId);
        try {
            jedisClient.set(key, FastJsonConvert.convertObjectToJSON(itemDesc));

            jedisClient.expire(key, REDIS_EXPIRE_TIME);

            logger.info("Redis 缓存 商品详情 key:" + key);

        } catch (Exception e) {
            logger.error("缓存错误商品ID:" + itemId, e);
        }
        return itemDesc;
    }

    @Override
    public TbItemParamItem getItemParamItemByItemId(Long itemId) {
        String key = ITEM_INFO_PROFIX + itemId + ITEM_INFO_PARAM_SUFFIX;

        try {
            String jsonItem = jedisClient.get(key);

            if (StringUtils.isNotBlank(jsonItem)) {
                return FastJsonConvert.convertJSONToObject(jsonItem, TbItemParamItem.class);
            } else {
                logger.error("Redis 查询不到 key:" + key);
            }
        } catch (Exception e) {
            logger.error("商品参数 获取缓存报错", e);
        }

        TbItemParamItem tbItemParamItem = new TbItemParamItem();
        tbItemParamItem.setItemId(itemId);
        TbItemParamItem itemParamItem = itemParamItemMapper.selectOne(tbItemParamItem);
        try {
            jedisClient.set(key, FastJsonConvert.convertObjectToJSON(itemParamItem));

            jedisClient.expire(key, REDIS_EXPIRE_TIME);

            logger.info("Redis 缓存 商品参数 key:" + key);

        } catch (Exception e) {
            logger.error("缓存错误商品ID:" + itemId, e);
        }
        return itemParamItem;
    }

    @Override
    public Page<TbItemWithCategory> getItemListWithCid(Page<TbItemWithCategory> page) {
        page.setRecords(itemMapper.getItemListWithCategory(page));

        return page;
    }

    @Override
    public Page<TbItemWithCategory> getItemListWithCidLikeTitle(Page<TbItemWithCategory> page, String title) {
        page.setRecords(itemMapper.getItemListWithCategoryLikeTitle(page, title));

        return page;
    }

    @Override
    public Page<TbItemParamWithCid> getItemParamListWithCid(Page<TbItemParamWithCid> page) {
        page.setRecords(itemParamMapper.getItemParamListWithCid(page));
        
        return page;
    }

    @Override
    public Page<TbItemParamWithCid> getItemParamListWithCidLikeCidName(Page<TbItemParamWithCid> page, String categoryName) {
        page.setRecords(itemParamMapper.getItemParamListWithCidLikeCidName(page, categoryName));
        
        return page;
    }

    @Override
    public Integer getItemCount() {
        Wrapper<TbItem> wrapper = new EntityWrapper<>();
        Integer integer = itemMapper.selectCount(wrapper);

        return integer;
    }

    @Override
    public Long saveItem(TbItem item, TbItemDesc itemDesc, TbItemParamItem itemParamItem) {
        Integer insert1 = 0;
        Integer insert2 = 0;
        Integer insert3 = 0;
        Date date = new Date();

        // 插入item
        item.setCreated(date);
        item.setUpdated(date);

        insert1 = itemMapper.insert(item);

        // 插入itemDesc
        itemDesc.setCreated(date);
        itemDesc.setUpdated(date);
        itemDesc.setItemId(item.getId());

        insert2 = itemDescMapper.insert(itemDesc);

        // 插入itemParamItem
        itemParamItem.setCreated(date);
        itemParamItem.setUpdated(date);
        itemParamItem.setItemId(item.getId());

        insert3 = itemParamItemMapper.insert(itemParamItem);
        
        return insert1 == 1 && insert2 == 1 && insert3 == 1 ? item.getId() : 0;
    }

    @Override
    public Long updateItem(TbItem item, TbItemDesc itemDesc, TbItemParamItem itemParamItem) {
        Date date = new Date();

        // 更新item
        if (item != null) {
            item.setUpdated(date);

            itemMapper.updateById(item);

            // 更新成功后清除缓存
            String itemKey = ITEM_INFO_PROFIX + item.getId() + ITEM_INFO_BASE_SUFFIX;
            if (StringUtils.isNotBlank(jedisClient.get(itemKey))) {
                jedisClient.del(itemKey);
            }
        }

        // 更新itemDesc
        if (itemDesc != null){
            itemDesc.setUpdated(date);
            itemDesc.setItemId(item.getId());
            
            itemDescMapper.updateById(itemDesc);

            String descKey = ITEM_INFO_PROFIX + item.getId() + ITEM_INFO_DESC_SUFFIX;
            if (StringUtils.isNotBlank(jedisClient.get(descKey))) {
                jedisClient.del(descKey);
            }
        }

        // 更新itemParamItem
        if (itemParamItem != null) {
            itemParamItem.setUpdated(date);
            itemParamItem.setItemId(item.getId());
            Wrapper<TbItemParamItem> wrapper = new EntityWrapper<>();
            wrapper.where("item_id={0}", item.getId());

            itemParamItemMapper.update(itemParamItem, wrapper);

            String paramKey = ITEM_INFO_PROFIX + item.getId() + ITEM_INFO_PARAM_SUFFIX;
            if (StringUtils.isNotBlank(jedisClient.get(paramKey))) {
                jedisClient.del(paramKey);
            }
        }
        
        return item.getId();
    }

    @Override
    public Long updateItemBase(TbItem item) {
        return this.updateItem(item, null, null);
    }

    @Override
    public Integer updateItemParam(TbItemParam itemParam) {
        Integer integer = itemParamMapper.updateById(itemParam);

        return integer;
    }

    @Override
    public Long saveItemParam(TbItemParam itemParam) {
        Date date = new Date();
        itemParam.setCreated(date);
        itemParam.setUpdated(date);

        Integer insert = itemParamMapper.insert(itemParam);
        return insert == 1 ? itemParam.getId() : 0;
    }

    @Override
    public TbItemParam getItemParamByItemCatId(Long itemCatId) throws Exception {
        Wrapper<TbItemParam> wrapper = new EntityWrapper<>();
        wrapper.eq("item_cat_id", itemCatId);

        List<TbItemParam> list = itemParamMapper.selectList(wrapper);

        if (list != null && list.size() > 1) {
            throw new BasisException("TbItemParam的itemCatId应该是唯一的");
        } else if (list != null && list.size() == 1) {
            return list.get(0);
        } else {
            return null;
        }
    }

    @Override
    public Integer deleteItemById(List<Long> ids) {
        itemMapper.deleteBatchIds(ids);

        itemDescMapper.deleteBatchIds(ids);

        Wrapper<TbItemParamItem> wrapper = new EntityWrapper<>();
        wrapper.in("item_id", ids);
        Integer delete = itemParamItemMapper.delete(wrapper);

        // 将es中数据删除
        if (delete > 0) {
            ids.parallelStream().forEach(r -> {
                try {
                    searchService.deleteItem(String.valueOf(r));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        
        return 1;
    }

    @Override
    public Integer deleteItemParamById(List<Long> ids) {
        Integer integer = itemParamMapper.deleteBatchIds(ids);

        return integer;
    }

    @Override
    public Integer updateItemStatus(TbItem item) {
        Integer integer = itemMapper.updateById(item);

        String itemKey = ITEM_INFO_PROFIX + item.getId() + ITEM_INFO_BASE_SUFFIX;
        jedisClient.del(itemKey);
            
        return integer;
    }

    @Override
    public void insertIntoES(Long id) {
        // 插入到es中
        try {
            searchService.addItem(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateIntoES(Long id) {
        SearchItem item = itemMapper.getItemListWithDescAndCidById(id);
        // 将更新存到es
        SearchItem searchItem = new SearchItem();
        searchItem.setCategoryName(item.getCategoryName());
        searchItem.setImage(item.getImage());
        searchItem.setItemDesc(item.getItemDesc());
        searchItem.setTitle(item.getTitle());
        searchItem.setSellPoint(item.getSellPoint());
        searchItem.setPrice(item.getPrice());
        searchItem.setColour(item.getColour());
        searchItem.setWeight(item.getWeight());
        searchItem.setSize(item.getSize());
        searchItem.setId(String.valueOf(id));
        try {
            searchService.updateItem(searchItem);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
