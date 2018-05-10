package cn.chinogo.content.controller;

import cn.chinogo.constant.Const;
import cn.chinogo.content.service.ContentService;
import cn.chinogo.pojo.EleTree;
import cn.chinogo.pojo.TbContent;
import cn.chinogo.pojo.TbContentCategory;
import cn.chinogo.pojo.TbContentWithCategory;
import com.alibaba.dubbo.config.annotation.Reference;
import com.baomidou.mybatisplus.plugins.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 前台控制器
 *
 * @author chinotan
 */
@Api("前台页面广告控制器")
@RestController
@RequestMapping("/content")
public class ContentController {

    @Reference(version = Const.CHINOGO_CONTENT_VERSION)
    private ContentService contentService;

    @Value("${big_ad_index}")
    private long Big_AD_INDEX;

    @ApiOperation("根据父类id得到内容分类")
    @GetMapping("/category")
    public Object getContentCategoryByParentId(@RequestParam(required = false, value = "parentId", defaultValue = "0") Long parentId) {
        List<TbContentCategory> ContentCategoryList = contentService.getContentCategoryByParentId(parentId);

        List<EleTree> list = new ArrayList<>();

        if (ContentCategoryList != null && ContentCategoryList.size() > 0) {
            for (TbContentCategory contentCategory : ContentCategoryList) {
                EleTree eleTree = new EleTree();
                eleTree.setId(contentCategory.getId());
                eleTree.setLabel(contentCategory.getName());
                eleTree.setIsLeaf(contentCategory.getIsParent() == 1 ? false : true);

                list.add(eleTree);
            }
        }

        return list;
    }

    @ApiOperation("插入内容分类")
    @PostMapping("/category")
    public Object insertContentCategory(@RequestBody TbContentCategory contentCategory) {
        Long id = contentService.insertContentCategory(contentCategory);

        Map<String, Object> map = new HashMap<>();
        if (id == null || id == 0) {
            map.put("status", "failed");
        } else {
            map.put("status", "success");
            map.put("id", id);
        }

        return map;
    }

    @ApiOperation("删除内容分类")
    @DeleteMapping("/category/{id}")
    public Object deleteContentCategory(@PathVariable("id") Long id) {
        Integer integer = contentService.deleteContentCategory(id);

        Map<String, Object> map = new HashMap<>();
        if (integer == null || integer == 0) {
            map.put("status", "failed");
        } else {
            map.put("status", "success");
        }

        return map;
    }

    @ApiOperation("更新内容分类")
    @PutMapping("/category")
    public Object updateContentCategory(@RequestBody TbContentCategory contentCategory) {
        Integer integer = contentService.updateContentCategory(contentCategory);

        Map<String, Object> map = new HashMap<>();
        if (integer == null || integer == 0) {
            map.put("status", "failed");
        } else {
            map.put("status", "success");
        }

        return map;
    }

    @ApiOperation("获得内容列表带着category, 默认按更新时间倒序排列")
    @GetMapping("/list")
    public Object getContentListWithCid(Long categoryId, Integer page, Integer limit) {
        Page<TbContentWithCategory> TbContentPage = contentService.getContentListWithCid(categoryId, new Page<TbContentWithCategory>(page, limit));

        return TbContentPage;
    }

    @ApiOperation(value = "添加内容")
    @PostMapping("/add")
    public Object insertContent(@RequestBody TbContent content) {
        Long i = contentService.insertContent(content);
        Map<String, String> map = new HashMap<>();
        if (i == 0) {
            map.put("status", "failed");
        } else {
            map.put("status", "success");
        }
        return map;
    }

    @ApiOperation(value = "刪除内容")
    @DeleteMapping("/delete")
    public Object deleteContent(@RequestBody List<Long> ids) {
        Integer i = contentService.deleteContent(ids);
        Map<String, String> map = new HashMap<>();
        if (i == 0) {
            map.put("status", "failed");
        } else {
            map.put("status", "success");
        }
        return map;
    }

    @ApiOperation(value = "更新内容")
    @PutMapping("/update")
    public Object updateContent(@RequestBody TbContent content) {
        Integer i = contentService.updateContent(content);
        Map<String, String> map = new HashMap<>();
        if (i == 0) {
            map.put("status", "failed");
        } else {
            map.put("status", "success");
        }
        return map;
    }
}
