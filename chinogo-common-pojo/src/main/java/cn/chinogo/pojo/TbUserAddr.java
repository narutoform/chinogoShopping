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
 * 
 * </p>
 *
 * @author chinotan
 * 
 */
@TableName("tb_user_addr")
@Data
public class TbUserAddr implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 地址ID
     */
	@JsonSerialize(using = ToStringSerializer.class)
    @TableId("addr_id")
	private String addrId;
    /**
     * 用户ID
     */
	@TableField("user_id")
	private String userId;
    /**
     * 收货人全名
     */
	@TableField("receiver_name")
	private String receiverName;
    /**
     * 固定电话
     */
	@TableField("receiver_phone")
	private String receiverPhone;
    /**
     * 移动电话
     */
	@TableField("receiver_mobile")
	private String receiverMobile;
    /**
     * 省份
     */
	@TableField("receiver_state")
	private String receiverState;
    /**
     * 城市
     */
	@TableField("receiver_city")
	private String receiverCity;
    /**
     * 区/县
     */
	@TableField("receiver_district")
	private String receiverDistrict;
    /**
     * 收货地址，如：xx路xx号
     */
	@TableField("receiver_address")
	private String receiverAddress;
    /**
     * 邮政编码,如：110000
     */
	@TableField("receiver_zip")
	private String receiverZip;
	/**
	 * 是否默认地址 1：是 0：不是
	 */
	private Integer isDefault;
	private Date created;
	private Date updated;
}
