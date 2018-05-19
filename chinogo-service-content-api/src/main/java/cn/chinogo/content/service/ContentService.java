package cn.chinogo.content.service;

import cn.chinogo.pojo.TbContent;
import cn.chinogo.pojo.TbContentCategory;
import cn.chinogo.pojo.TbContentWithCategory;
import com.baomidou.mybatisplus.plugins.Page;

import java.util.List;

/**
 * 内容分类Service
 *
 * @author chinotan
 */
public interface ContentService {

    /**
     * 根据父类id得到内容分类(内容按sort_order排序)
     *
     * @param parentId
     * @return
     */
    List<TbContentCategory> getContentCategoryByParentId(Long parentId);

    /**
     * 插入内容分类
     *
     * @param contentCategory
     * @return 返回内容分类id
     */
    Long insertContentCategory(TbContentCategory contentCategory);

    /**
     * 删除内容分类(逻辑删除--更新)
     *
     * @param id
     * @return
     */
    Integer deleteContentCategory(Long id);

    /**
     * 更新内容分类
     *
     * @param contentCategory
     * @return
     */
    Integer updateContentCategory(TbContentCategory contentCategory);




    /**
     * 得到内容列表,带着内容分类(内容默认按更新时间倒序排序)
     *
     * @param page
     * @return
     */
    Page<TbContentWithCategory> getContentListWithCid(Long categoryId, Page<TbContentWithCategory> page);

    /**
     * 插入内容
     *
     * @param content
     * @return 返回内容id
     */
    Long insertContent(TbContent content);

    /**
     * 删除内容
     *
     * @param ids
     * @return
     */
    Integer deleteContent(List<Long> ids);

    /**
     * 更新内容
     *
     * @param content
     * @return
     */
    Integer updateContent(TbContent content);

    /**
     * 前端显示内容
     * @return
     */
    List<TbContent> showContent(Long categoryId);
}
