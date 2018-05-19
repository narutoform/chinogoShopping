package cn.chinogo.pojo;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author chinotan
 * 
 */
@TableName("tb_content")
@Data
public class TbContent implements Serializable {

    private static final long serialVersionUID = 1L;

	@TableId(value="id", type= IdType.AUTO)
	private Long id;
    /**
     * 内容类目ID
     */
	@TableField("category_id")
	private Long categoryId;
    /**
     * 内容标题
     */
	private String title;
    /**
     * 子标题
     */
	@TableField("sub_title")
	private String subTitle;
    /**
     * 标题描述
     */
	@TableField("title_desc")
	private String titleDesc;
    /**
     * 链接
     */
	private String url;
    /**
     * 图片绝对路径
     */
	private String pic;
    /**
     * 内容
     */
	private String content;
	private Date created;
	private Date updated;
}
