package cn.chinogo.item.controller;

import cn.chinogo.constant.Const;
import cn.chinogo.item.service.ItemService;
import cn.chinogo.pojo.*;
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
 * 商品 Controller
 * 
 * @author chinotan
 */
@RestController
@RequestMapping("/item")
@Api(value = "商品相关controller")
public class ItemController {

    private static final Logger logger = LoggerFactory.getLogger(ItemController.class);

    @Reference(version = Const.CHINOGO_ITEM_VERSION, timeout = 1000000)
    private ItemService itemService;

    @ApiOperation(value = "根据商品id得到商品基本信息")
    @GetMapping("/base/{itemId}")
    public Object getItemById(@PathVariable("itemId") Long itemId) {
        TbItem item = itemService.getItemById(itemId);

        Map<String, Object> map = new HashMap<>();

        if (item == null) {
            map.put("status", "none");
            map.put("item", item);
        } else {
            map.put("status", "success");
            map.put("item", item);
        }

        return map;
    }
    
    @ApiOperation(value = "根据商品id得到商品描述")
    @GetMapping("/desc/{itemId}")
    public Object getItemDescById(@PathVariable("itemId") Long itemId) {
        TbItemDesc itemDesc = itemService.getItemDescById(itemId);

        Map<String, Object> map = new HashMap<>();

        if (itemDesc == null) {
            map.put("status", "none");
            map.put("itemDesc", itemDesc);
        } else {
            map.put("status", "success");
            map.put("itemDesc", itemDesc);
        }

        return map;
    }

    @ApiOperation(value = "根据商品id得到商品规格")
    @GetMapping("/paramItem/{itemId}")
    public Object getItemParamItemByItemId(@PathVariable("itemId") Long itemId) {
        TbItemParamItem itemParamItem = itemService.getItemParamItemByItemId(itemId);

        Map<String, Object> map = new HashMap<>();

        if (itemParamItem == null) {
            map.put("status", "none");
            map.put("itemParamItem", itemParamItem);
        } else {
            map.put("status", "success");
            map.put("itemParamItem", itemParamItem);
        }

        return map;
    }

    @ApiOperation(value = "根据标题得到商品列表, 默认按更新时间倒序排列")
    @GetMapping("/list/likeTitle")
    public Object getItemListWithCidLikeTitle(Integer page, Integer limit, String title) {
        Page<TbItemWithCategory> TbItemPage = itemService.getItemListWithCidLikeTitle
                (new Page<TbItemWithCategory>(page, limit), title);

        return TbItemPage;
    }

    @ApiOperation(value = "获得商品列表带着category, 默认按更新时间倒序排列")
    @GetMapping("/list")
    public Object getItemListWithCid(Integer page, Integer limit) {
        // Page的页数是从1开始的，如果page是小于0的话也是1
        Page<TbItemWithCategory> TbItemPage = itemService.getItemListWithCid(new Page<TbItemWithCategory>(page, limit));

        return TbItemPage;
    }

    @ApiOperation(value = "获得商品规格列表带着category, 默认按更新时间倒序排列")
    @GetMapping("/param/list")
    public Object getItemParamListWithCid(Integer page, Integer limit) {
        Page<TbItemParamWithCid> listWithCidPage = itemService.getItemParamListWithCid(new Page<TbItemParamWithCid>(page, limit));

        return listWithCidPage;
    }

    @ApiOperation(value = "根据分类名字得到商品规格, 默认按更新时间倒序排列")
    @GetMapping("/param/list/likeCidName")
    public Object getItemListWithCidLikeCidName(Integer page, Integer limit, String categoryName) {
        Page<TbItemParamWithCid> itemParamListWithCidLikeCidNamePage = itemService.getItemParamListWithCidLikeCidName
                (new Page<TbItemParamWithCid>(page, limit), categoryName);

        return itemParamListWithCidLikeCidNamePage;
    }

    @ApiOperation(value = "添加商品")
    @PostMapping("/add")
    public Object saveItemAll(@RequestBody TbItemAll itemAll) {
        Long i = itemService.saveItem(itemAll.getTbItem(), itemAll.getTbItemDesc(), itemAll.getTbItemParamItem());
        Map<String, String> map = new HashMap<>();
        if (i == 0) {
            map.put("status", "failed");
        } else {
            // 插入到es中
            itemService.insertIntoES(i);
            map.put("status", "success");
        }
        return map;
    }

    @ApiOperation(value = "更新商品")
    @PutMapping("/update")
    public Object updateItemAll(@RequestBody TbItemAll itemAll) {
        Long i = itemService.updateItem(itemAll.getTbItem(), itemAll.getTbItemDesc(), itemAll.getTbItemParamItem());
        Map<String, String> map = new HashMap<>();
        if (i == null || i == 0) {
            map.put("status", "failed");
        } else {
            // 更新到es
            itemService.updateIntoES(i);
            map.put("status", "success");
        }
        return map;
    }

    @ApiOperation(value = "更新商品参数规格")
    @PutMapping("/param/update")
    public Object updateItemParam(@RequestBody TbItemParam itemParam) {
        Integer integer = itemService.updateItemParam(itemParam);
        Map<String, String> map = new HashMap<>();
        if (integer == 0) {
            map.put("status", "failed");
        } else {
            map.put("status", "success");
        }
        return map;
    }

    @ApiOperation(value = "刪除商品")
    @DeleteMapping("/delete")
    public Object deleteItemAll(@RequestBody List<Long> ids) {
        Integer i = itemService.deleteItemById(ids);
        Map<String, String> map = new HashMap<>();
        if (i == 0) {
            map.put("status", "failed");
        } else {
            map.put("status", "success");
        }
        return map;
    }

    @ApiOperation(value = "刪除商品参数")
    @DeleteMapping("/param/delete")
    public Object deleteItemParam(@RequestBody List<Long> ids) {
        Integer i = itemService.deleteItemParamById(ids);
        Map<String, String> map = new HashMap<>();
        if (i == 0) {
            map.put("status", "failed");
        } else {
            map.put("status", "success");
        }
        return map;
    }

    @ApiOperation(value = "添加商品参数")
    @PostMapping("/param/add")
    public Object saveItemParam(@RequestBody TbItemParam itemParam) {
        Long i = itemService.saveItemParam(itemParam);
        Map<String, String> map = new HashMap<>();
        if (i == 0) {
            map.put("status", "failed");
        } else {
            map.put("status", "success");
        }
        return map;
    }

    @ApiOperation(value = "根据商品分类id得到商品参数,判断是否存在")
    @GetMapping("/param/checkCid/{itemCatId}")
    public Object checkItemParamByItemCatId(@PathVariable("itemCatId") Long itemCatId) {
        Map<String, String> map = new HashMap<>();
        TbItemParam itemParam = null;
        try {
            itemParam = itemService.getItemParamByItemCatId(itemCatId);
        } catch (Exception e) {
            map.put("status", "failed");
            return map;
        }

        if (itemParam == null) {
            map.put("status", "success");
        } else {
            map.put("status", "failed");
        }
        return map;
    }

    @ApiOperation(value = "根据cid得到商品规格")
    @GetMapping("/param/{itemCatId}")
    public Object getItemParamByCid(@PathVariable("itemCatId") Long itemCatId) {
        Map<String, Object> map = new HashMap<>();
        TbItemParam itemParam = null;
        try {
            itemParam = itemService.getItemParamByItemCatId(itemCatId);
        } catch (Exception e) {
            map.put("status", "failed");
            map.put("itemParam", itemParam);
            return map;
        }

        if (itemParam == null) {
            map.put("status", "none");
            map.put("itemParam", itemParam);
        } else {
            map.put("status", "success");
            map.put("itemParam", itemParam);
        }
        return map;
    }

    @ApiOperation(value = "更新商品状态（上架，下架）")
    @PutMapping("/status/update")
    public Object updateItemStatus(@RequestBody TbItem item) {
        Integer i = itemService.updateItemStatus(item);
        Map<String, String> map = new HashMap<>();
        if (i == 0) {
            map.put("status", "failed");
        } else {
            map.put("status", "success");
        }
        return map;
    }

}
