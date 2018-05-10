package cn.chinogo.mapper;

import cn.chinogo.pojo.SearchItem;
import cn.chinogo.pojo.TbItem;
import cn.chinogo.pojo.TbItemWithCategory;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;

import java.util.List;

/**
 * <p>
 * 商品表 Mapper 接口
 * </p>
 *
 * @author chinotan
 * @since 2017-10-13
 */
public interface TbItemMapper extends BaseMapper<TbItem> {

    /**
     * 带着category查询item
     *
     * @param page
     * @return
     */
    List<TbItemWithCategory> getItemListWithCategory(Pagination page);

    /**
     * 根据标题得到商品列表
     *
     * @param page
     * @param title
     * @return
     */
    List<TbItemWithCategory> getItemListWithCategoryLikeTitle(Pagination page, String title);

    /**
     * 得到item带着分类和描述
     * 
     * @return
     */
    List<SearchItem> getItemListWithDescAndCid();

}