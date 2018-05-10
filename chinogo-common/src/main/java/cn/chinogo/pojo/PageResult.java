package cn.chinogo.pojo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PageResult<E> implements Serializable {
    private long total;
    private List<E> rows;
}
