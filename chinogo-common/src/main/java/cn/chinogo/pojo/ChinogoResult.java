package cn.chinogo.pojo;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.Serializable;

/**
 * 自定义响应结构
 */
public class ChinogoResult implements Serializable {

    // 定义jackson对象
    private static final ObjectMapper MAPPER = new ObjectMapper();

    // 响应业务状态
    private Integer status;

    // 响应消息
    private String msg;

    // 响应中的数据
    private Object data;

    public static ChinogoResult build(Integer status, String msg, Object data) {
        return new ChinogoResult(status, msg, data);
    }

    public static ChinogoResult ok(Object data) {
        return new ChinogoResult(data);
    }

    public static ChinogoResult ok() {
        return new ChinogoResult(null);
    }

    public ChinogoResult() {

    }

    public static ChinogoResult build(Integer status, String msg) {
        return new ChinogoResult(status, msg, null);
    }

    public ChinogoResult(Integer status, String msg, Object data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    public ChinogoResult(Object data) {
        this.status = 200;
        this.msg = "OK";
        this.data = data;
    }

    public Boolean isOK() {
        return this.status == 200;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }



}
