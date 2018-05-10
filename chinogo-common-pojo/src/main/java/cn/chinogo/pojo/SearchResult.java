package cn.chinogo.pojo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 查询返回POJO
 */
@Data
public class SearchResult implements Serializable {

    //商品集合
    private List<SearchItem> itemList;
    //总记录数
    private long recordCount;
    //总页数
    private long pageCount;
    //当前页
    private long curPage;
    //每页查到的数量
    private long perNum;

}
