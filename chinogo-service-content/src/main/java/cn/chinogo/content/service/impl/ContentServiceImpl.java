package cn.chinogo.content.service.impl;

import cn.chinogo.constant.Const;
import cn.chinogo.content.service.ContentService;
import cn.chinogo.mapper.TbContentCategoryMapper;
import cn.chinogo.mapper.TbContentMapper;
import cn.chinogo.pojo.TbContent;
import cn.chinogo.pojo.TbContentCategory;
import cn.chinogo.pojo.TbContentWithCategory;
import cn.chinogo.redis.service.JedisClient;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 首页内容Service
 *
 * @author chinotan
 */
@Service(version = Const.CHINOGO_CONTENT_VERSION, timeout = 1000000)
@Transactional(rollbackFor = Exception.class)
public class ContentServiceImpl implements ContentService {

    private static Logger logger = Logger.getLogger(ContentServiceImpl.class);

    @Autowired
    private TbContentMapper contentMapper;

    @Autowired
    private TbContentCategoryMapper contentCategoryMapper;

    @Reference(version = Const.CHINOGO_REDIS_VERSION)
    private JedisClient jedisClient;

    @Override
    public List<TbContentCategory> getContentCategoryByParentId(Long parentId) {
        Wrapper<TbContentCategory> wrapper = new EntityWrapper<>();
        wrapper.eq("parent_id", parentId);
        wrapper.eq("status", 1);
        wrapper.orderBy("sort_order", true);
        List<TbContentCategory> list = contentCategoryMapper.selectList(wrapper);
        if(list.size() > 0 && list != null){
            return list;
        }else {
            return null;
        }
    }

    @Override
    public Long insertContentCategory(TbContentCategory contentCategory) {
        Date date = new Date();
        contentCategory.setCreated(date);
        contentCategory.setUpdated(date);
        contentCategoryMapper.insert(contentCategory);
        return contentCategory.getId();
    }

    @Override
    public Integer deleteContentCategory(Long id) {
        TbContentCategory contentCategory = new TbContentCategory();
        contentCategory.setId(id);
        contentCategory.setStatus(2);
        Integer integer = this.updateContentCategory(contentCategory);

        // 如果删除后，当前的节点的父节点没有子节点的话，就将该节点的父节点设置为子节点
        TbContentCategory contentCid = contentCategoryMapper.selectById(id);
        List<TbContentCategory> list = this.getContentCategoryByParentId(contentCid.getParentId());
        if (list != null && list.size() > 0){
            return integer;
        } else {
            TbContentCategory parentContentCategory = contentCategoryMapper.selectById(contentCid.getParentId());
            parentContentCategory.setIsParent(0);
            this.updateContentCategory(parentContentCategory);
        }

        return integer;
    }

    @Override
    public Integer updateContentCategory(TbContentCategory contentCategory) {
        Date date = new Date();
        contentCategory.setUpdated(date);
        Integer integer = contentCategoryMapper.updateById(contentCategory);
        return integer;
    }

    @Override
    public Page<TbContentWithCategory> getContentListWithCid(Long categoryId, Page<TbContentWithCategory> page) {
        List<TbContentWithCategory> list = contentMapper.getContentListWithCid(page, categoryId);
        page.setRecords(list);
        
        return page;
    }

    @Override
    public Long insertContent(TbContent content) {
        Date date = new Date();
        
        content.setCreated(date);
        content.setUpdated(date);
        contentMapper.insert(content);
        
        return content.getId();
    }

    @Override
    public Integer deleteContent(List<Long> ids) {
        Integer integer = contentMapper.deleteBatchIds(ids);
        
        return integer;
    }

    @Override
    public Integer updateContent(TbContent content) {
        Date date = new Date();
        content.setUpdated(date);
        Integer integer = contentMapper.updateById(content);
        
        return integer;
    }

    /**
     * 前端显示内容
     *
     * @return
     */
    @Override
    public List<TbContent> showContent(Long categoryId) {
        Wrapper<TbContent> wrapper = new EntityWrapper<>();
        wrapper.eq("category_id", categoryId);
        List<TbContent> tbContents = contentMapper.selectList(wrapper);
        return tbContents;
    }
}
