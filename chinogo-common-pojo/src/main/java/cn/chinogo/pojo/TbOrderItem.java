package cn.chinogo.pojo;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * <p>
 * 
 * </p>
 *
 * @author chinotan
 * 
 */
@TableName("tb_order_item")
@Data
public class TbOrderItem implements Serializable {

    private static final long serialVersionUID = 1L;

	@JsonSerialize(using = ToStringSerializer.class)
	private Long id;
    /**
     * 商品id
     */
	@TableField("item_id")
	@JsonSerialize(using = ToStringSerializer.class)
	private String itemId;
    /**
     * 订单id
     */
	@JsonSerialize(using = ToStringSerializer.class)
	@TableField("order_id")
	private String orderId;
    /**
     * 商品购买数量
     */
	private Integer num;
    /**
     * 商品标题
     */
	private String title;
    /**
     * 商品单价
     */
	private BigDecimal price;
    /**
     * 商品总金额
     */
	@TableField("total_fee")
	private BigDecimal totalFee;
    /**
     * 商品图片地址
     */
	@TableField("pic_path")
	private String picPath;
    /**
     * 总重量 单位/克
     */
	private BigDecimal weights;
}
