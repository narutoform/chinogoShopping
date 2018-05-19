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
 * 商品规格和商品的关系表
 * </p>
 *
 * @author chinotan
 * 
 */
@TableName("tb_item_param_item")
@Data
public class TbItemParamItem implements Serializable {

    private static final long serialVersionUID = 1L;

	@TableId(value="id")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long id;
    /**
     * 商品ID
     */
	@TableField("item_id")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long itemId;
    /**
     * 参数数据，格式为json格式
     */
	@TableField("param_data")
	private String paramData;
	private Date created;
	private Date updated;
}
