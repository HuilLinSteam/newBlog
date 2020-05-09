package com.newblog.huil.entity.vo;

/**
 * @author HuilLIN
 */
public class ResultVo {
    private Integer code;
    private Object data;
    private String message;

    public ResultVo(Integer code, Object data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    public ResultVo(Integer code, String message) {
        this.code = code;
        this.message = message;
    }



    public ResultVo(Object data) {
        this.data = data;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "ResultVO{" +
                "code=" + code +
                ", data=" + data +
                ", message='" + message + '\'' +
                '}';
    }
}
