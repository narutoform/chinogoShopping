package cn.chinogo.pojo;

import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * 搜索Item POJO
 */
@Data
@NoArgsConstructor
public class SearchItem implements Serializable {

    private String id;
    private String image;
    private BigDecimal price;
    private String sellPoint;
    private String title;
    private String colour;
    private String size;
    private BigDecimal weight;
    private String categoryName;
    private String itemDesc;

    public SearchItem(String id, String image, BigDecimal price, String sellPoint, String title, String colour,
                      BigDecimal weight, String size) {
        this.id = id;
        this.image = image;
        this.price = price;
        this.sellPoint = sellPoint;
        this.title = title;
        this.colour = colour;
        this.weight = weight;
        this.size = size;
    }
}
