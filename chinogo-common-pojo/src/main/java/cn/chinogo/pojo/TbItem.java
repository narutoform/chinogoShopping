package cn.chinogo.pojo;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.Date;

/**
 * <p>
 * 商品表
 * </p>
 *
 * @author chinotan
 * 
 */
@TableName("tb_item")
@Data
public class TbItem implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 商品id，同时也是商品编号
     */
    @JsonSerialize(using = ToStringSerializer.class)
	private Long id;
    /**
     * 商品标题
     */
	private String title;
    /**
     * 商品卖点
     */
	@TableField("sell_point")
	private String sellPoint;
    /**
     * 商品价格，单位为：分
     */
	private Long price;
    /**
     * 库存数量
     */
	private Integer num;
    /**
     * 商品条形码
     */
	private String barcode;
    /**
     * 商品图片
     */
	private String image;
    /**
     * 所属类目，叶子类目
     */
	private Long cid;
    /**
     * 商品状态，1-正常，2-下架
     */
	private Integer status;
    /**
     * 重量
     */
	private Integer weight;
    /**
     * 创建时间
     */
	private Date created;
    /**
     * 更新时间
     */
	private Date updated;
    /**
     * 颜色
     */
	private String colour;
    /**
     * 尺寸
     */
	private String size;
    
    public String getPriceView() {
        DecimalFormat df1 = new DecimalFormat("#.00");
        df1.setGroupingUsed(false);
        String format = df1.format(price / 100.00);
        return format;
    }
}
