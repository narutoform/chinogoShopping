package cn.chinogo.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 带category的item
 *
 * @author chinotan
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TbItemWithCategory extends TbItem {

    private String categoryName;

}
