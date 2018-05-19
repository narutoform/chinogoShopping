package cn.chinogo.pojo;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 商品规则参数
 * </p>
 *
 * @author chinotan
 * 
 */
@TableName("tb_item_param")
@Data
public class TbItemParam implements Serializable {

    private static final long serialVersionUID = 1L;

	@TableId(value="id")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long id;
    /**
     * 商品类目ID
     */
	@TableField("item_cat_id")
	private Long itemCatId;
    /**
     * 参数数据，格式为json格式
     */
	@TableField("param_data")
	private String paramData;
	private Date created;
	private Date updated;
}
