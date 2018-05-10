package cn.chinogo.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 商品规格列表带着category
 *
 * @author chinotan
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TbItemParamWithCid extends TbItemParam {

    private String categoryName;

}
