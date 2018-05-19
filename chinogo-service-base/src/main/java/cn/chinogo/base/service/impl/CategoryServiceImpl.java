package cn.chinogo.base.service.impl;

import cn.chinogo.base.service.CategoryService;
import cn.chinogo.constant.Const;
import cn.chinogo.mapper.TbCategoryMapper;
import cn.chinogo.pojo.TbCategory;
import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 分类维护 Service
 * 
 * @author chinotan
 */
@Service(version = Const.CHINOGO_CATEGORY_VERSION, timeout = 1000000)
@Transactional(rollbackFor = Exception.class)
public class CategoryServiceImpl implements CategoryService {

    private static final Logger logger = LoggerFactory.getLogger(CategoryService.class);

    @Autowired
    private TbCategoryMapper categoryMapper;

    @Override
    public List<TbCategory> getCategoryByParentId(Long parentId) {
        Wrapper<TbCategory> wrapper = new EntityWrapper<>();
        wrapper.eq("parent_id", parentId);
        List<TbCategory> list = categoryMapper.selectList(wrapper);
        if(list.size() > 0 && list != null){
            return list;
        }else {
            return null;
        }
    }
}
