package cn.chinogo.category.controller;

import cn.chinogo.category.service.CategoryService;
import cn.chinogo.constant.Const;
import cn.chinogo.pojo.EleTree;
import cn.chinogo.pojo.TbCategory;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * Category 商品分类Controller
 *
 * @author chinotan
 */
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Reference(version = Const.CHINOGO_CATEGORY_VERSION)
    private CategoryService categoryService;

    @GetMapping("/children/list")
    public List<EleTree> getCategoryByParentId(@RequestParam(required = false, value = "parentId", defaultValue = "0") Long parentId) {
        List<TbCategory> categoryList = categoryService.getCategoryByParentId(parentId);
        List<EleTree> list = new ArrayList<>();
        if (categoryList != null && categoryList.size() > 0) {
            for (TbCategory category : categoryList) {
                EleTree eleTree = new EleTree();
                eleTree.setId(category.getId());
                eleTree.setLabel(category.getName());
                eleTree.setIsLeaf(category.getIsParent() == 1 ? false : true);

                list.add(eleTree);
            }
        }

        return list;
    }
}
