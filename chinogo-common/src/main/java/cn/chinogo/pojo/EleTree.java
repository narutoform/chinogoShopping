package cn.chinogo.pojo;

import lombok.Data;

import java.io.Serializable;

@Data
public class EleTree implements Serializable {
    private long id;
    private String label;
    private Boolean isLeaf;
}
