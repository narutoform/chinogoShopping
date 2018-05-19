package cn.chinogo.search.controller;

import cn.chinogo.constant.Const;
import cn.chinogo.pojo.ChinogoResult;
import cn.chinogo.pojo.SearchItem;
import cn.chinogo.pojo.SearchResult;
import cn.chinogo.search.service.SearchService;
import com.alibaba.dubbo.config.annotation.Reference;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;

/**
 * 搜索 Controller
 *
 * @author chinotan
 */
@Api("搜索 Controller")
@RestController
@RequestMapping("/search")
public class SearchController {

    @Reference(version = Const.CHINOGO_SEARCH_VERSION, timeout = 1000000)
    private SearchService searchService;

    @Value("${search_result_rows}")
    private Integer SEARCH_RESULT_ROWS;

    /**
     * 根据关键字进行搜索
     *
     * @param queryString 要查询的值
     * @param page 当前页数
     * @param sort 按价格排序（1：价格从低到高 -1：价格从高到低）
     * @param priceGt 价格区间（最小）
     * @param priceLte 价格区间（最大）
     * @return
     * @throws Exception
     */
    @ApiOperation("根据关键字进行搜索")
    @GetMapping("/item")
    public Object search(@RequestParam("q") String queryString,
                         @RequestParam(defaultValue = "1") Integer page,
                         @RequestParam(required = false, defaultValue = "0") Integer sort,
                         @RequestParam(required = false, defaultValue = "0") Long priceGt,
                         @RequestParam(required = false, defaultValue = "0") Long priceLte) throws Exception {

        SearchResult search = null;

        if (StringUtils.isNotBlank(queryString)) {
            search = searchService.search(queryString, page - 1, 20, sort, priceGt, priceLte);
        }

        return search;
    }

    @ApiOperation("导入全部的item")
    @PostMapping("/item/api/importAll")
    public Object importAll() throws Exception {
        ChinogoResult chinogoResult = searchService.importAllItems();

        return chinogoResult;
    }

    @ApiOperation("对某个item进行更新")
    @PutMapping("/item")
    public Object updateItem(@RequestBody SearchItem item) {
        ChinogoResult chinogoResult = null;
        try {
            chinogoResult = searchService.updateItem(item);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return chinogoResult;
    }

    @ApiOperation("对某个item进行删除")
    @DeleteMapping("/item/{id}")
    public Object deleteItem(@PathVariable("id") String id) {
        ChinogoResult chinogoResult = null;
        try {
            chinogoResult = searchService.deleteItem(id);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return chinogoResult;
    }
}
