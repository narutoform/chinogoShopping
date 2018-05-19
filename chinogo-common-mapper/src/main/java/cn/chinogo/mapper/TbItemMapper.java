package cn.chinogo.mapper;

import cn.chinogo.pojo.SearchItem;
import cn.chinogo.pojo.TbItem;
import cn.chinogo.pojo.TbItemWithCategory;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 商品表 Mapper 接口
 * </p>
 *
 * @author chinotan
 * 
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

    /**
     * 得到item带着分类和描述(通过id)
     *
     * @return
     */
    SearchItem getItemListWithDescAndCidById(@Param("id") Long id);

}