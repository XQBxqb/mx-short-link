package cn.mx.link.vo;

import lombok.Data;

import java.io.Serializable;

@Data

public class ResultResponse<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    private String msg;
    private Integer code;
    private T data;

    public ResultResponse() {
    }

    public ResultResponse(String msg, Integer code, T data) {
        this.msg = msg;
        this.code = code;
        this.data = data;
    }


    public static <T> ResultResponse<T> success(T context) {
        return new ResultResponse<>("success", 200, context);
    }

    public static <T> ResultResponse<T> failed(String msg) {
        return new ResultResponse<>("failed", 500, null);
    }
}
