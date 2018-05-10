package cn.chinogo.mapper;

import cn.chinogo.pojo.TbContent;
import cn.chinogo.pojo.TbContentWithCategory;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;

import java.util.List;

/**
 * <p>
  *  Mapper 接口
 * </p>
 *
 * @author chinotan
 * @since 2017-10-13
 */
public interface TbContentMapper extends BaseMapper<TbContent> {

    /**
     * 带着category查询content
     *
     * @param page
     * @return
     */
    List<TbContentWithCategory> getContentListWithCid(Pagination page, Long categoryId);

}