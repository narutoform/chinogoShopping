package cn.chinogo.base.service;

import cn.chinogo.pojo.TbCategory;

import java.util.List;

/**
 * 分类维护
 */
public interface CategoryService {
    List<TbCategory> getCategoryByParentId(Long parentId);
}
