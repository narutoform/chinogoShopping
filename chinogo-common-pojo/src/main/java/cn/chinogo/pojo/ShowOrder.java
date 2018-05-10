package cn.chinogo.pojo;

import lombok.Data;

import java.util.List;

/**
 * @author chinotan
 * 在用户页面显示的order
 */
@Data
public class ShowOrder {
    
    private TbOrder order;
    
    private List<TbOrderItem> orderItems;
    
}
