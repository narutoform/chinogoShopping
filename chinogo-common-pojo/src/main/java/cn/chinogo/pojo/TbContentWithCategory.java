package cn.chinogo.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 带category的TbContent
 *
 * @author chinotan
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TbContentWithCategory extends TbContent {

    private String categoryName;

}
