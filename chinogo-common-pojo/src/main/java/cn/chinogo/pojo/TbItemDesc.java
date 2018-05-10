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
 * 商品描述表
 * </p>
 *
 * @author chinotan
 * @since 2017-10-13
 */
@TableName("tb_item_desc")
@Data
public class TbItemDesc implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 商品ID
     */
    @TableId("item_id")
    @JsonSerialize(using = ToStringSerializer.class)
	private Long itemId;
    /**
     * 商品描述
     */
	@TableField("item_desc")
	private String itemDesc;
    /**
     * 创建时间
     */
	private Date created;
    /**
     * 更新时间
     */
	private Date updated;
}
