package cn.chinogo.mapper;

import cn.chinogo.pojo.TbItemParam;
import cn.chinogo.pojo.TbItemParamWithCid;
import cn.chinogo.pojo.TbItemWithCategory;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;

import java.util.List;

/**
 * <p>
 * 商品规则参数 Mapper 接口
 * </p>
 *
 * @author chinotan
 * @since 2017-10-13
 */
public interface TbItemParamMapper extends BaseMapper<TbItemParam> {

    /**
     * 获得商品规格列表带着category, 默认按更新时间倒序排列
     *
     * @param page
     * @return
     */
    List<TbItemParamWithCid> getItemParamListWithCid(Pagination page);

    /**
     * 根据分类名称得到商品规格列表
     *
     * @param page
     * @param categoryName
     * @return
     */
    List<TbItemParamWithCid> getItemParamListWithCidLikeCidName(Pagination page, String categoryName);

}