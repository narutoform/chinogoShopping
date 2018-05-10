package cn.chinogo.pojo;

import lombok.*;

import java.io.Serializable;
import java.text.DecimalFormat;

/**
 * 搜索Item POJO
 */
@Data
@NoArgsConstructor
public class SearchItem implements Serializable {

    private String id;
    private String image;
    private Long price;
    private String sellPoint;
    private String title;
    private String colour;
    private String size;
    private String weight;
    private String categoryName;
    private String itemDesc;
    @Setter(value = AccessLevel.PRIVATE)
    private String priceView;

    public String getPriceView() {
        DecimalFormat df1 = new DecimalFormat("#.00");
        df1.setGroupingUsed(false);
        String format = df1.format(price / 100.00);
        return format;
    }

    public SearchItem(String id, String image, Long price, String sellPoint, String title, String colour, 
                      String weight, String size) {
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
