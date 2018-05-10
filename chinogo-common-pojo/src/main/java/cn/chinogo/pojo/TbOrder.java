package cn.chinogo.pojo;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author chinotan
 * @since 2017-10-13
 */
@TableName("tb_order")
@Data
public class TbOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 订单id
     */
    @TableId(value = "order_id", type = IdType.INPUT)
	@JsonSerialize(using = ToStringSerializer.class)
	private String orderId;
    /**
     * 用户id
     */
	@JsonSerialize(using = ToStringSerializer.class)
	@TableField("user_id")
	private Long userId;
    /**
     * 地址id
     */
	@TableField("addr_id")
	private String addrId;
    /**
     * 实付金额。精确到2位小数;单位:元。如:200.07，表示:200元7分
     */
	private String payment;
    /**
     * 支付类型，1、货到付款，2、在线支付，3、微信支付，4、支付宝支付
     */
	@TableField("payment_type")
	private Integer paymentType;
    /**
     * 邮费。单位:元
     */
	@TableField("post_fee")
	private Integer postFee;
    /**
     * 状态：1、未付款，2、已付款，3、未发货，4、已发货，5、交易成功，6、交易关闭
     */
	private Integer status;
    /**
     * 物流名称
     */
	@TableField("shipping_name")
	private String shippingName;
    /**
     * 物流单号
     */
	@TableField("shipping_code")
	private String shippingCode;
    /**
     * 订单总重 单位/克
     */
	@TableField("total_weight")
	private String totalWeight;
    /**
     * 买家是否已经评价
     */
	@TableField("buyer_rate")
	private Integer buyerRate;
    /**
     * 交易关闭时间
     */
	@TableField("close_time")
	private Date closeTime;
    /**
     * 交易完成时间
     */
	@TableField("end_time")
	private Date endTime;
    /**
     * 付款时间
     */
	@TableField("payment_time")
	private Date paymentTime;
    /**
     * 发货时间
     */
	@TableField("consign_time")
	private Date consignTime;
    /**
     * 订单创建时间
     */
	@TableField("create_time")
	private Date createTime;
    /**
     * 订单更新时间
     */
	@TableField("update_time")
	private Date updateTime;
}
