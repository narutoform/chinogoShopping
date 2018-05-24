package cn.chinogo.pojo;

import com.baomidou.mybatisplus.annotations.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 购物车
 * </p>
 *
 * @author chinotan
 * @since 2017-11-09
 */
@TableName("tb_cart")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TbCart implements Serializable{

    private static final long serialVersionUID = 1L;

	private String productId;
	private BigDecimal productPrice;
	private String productName;
	private String productImg;
	/**
	 * 是否选中，1：选中，0：没选中
	 */
	private String checked;
	private String colour;
	private String size;
	private BigDecimal weight;
	private Integer productNum;
}
