package cn.chinogo.pojo;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 用户表
 * </p>
 *
 * @author chinotan
 * 
 */
@TableName("tb_admin_user")
@Data
public class TbAdminUser implements Serializable {

    private static final long serialVersionUID = 1L;

	@TableId(value="id", type= IdType.AUTO)
	private Long id;
    /**
     * 用户名
     */
	private String username;
    /**
     * 密码，加密存储
     */
	private String password;
    /**
     * 注册手机号
     */
	private String phone;
    /**
     * 注册邮箱
     */
	private String email;
    /**
     * 用户头像
     */
	private String avatar;
	private Date created;
	private Date updated;
}
