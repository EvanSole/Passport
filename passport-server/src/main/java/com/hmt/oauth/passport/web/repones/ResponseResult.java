package com.hmt.oauth.passport.web.repones;

import java.io.Serializable;

/**
 * Created by Edwin on 2016/12/19.
 */
public class ResponseResult<T> implements Serializable {

    private String code;
    private T data;
    private String message;

    public ResponseResult(T data) {
        this.data = data;
    }

    public ResponseResult(String code, T data) {
        this.code = code;
        this.data = data;
    }

    public ResponseResult(String code, String message) {
        this.code = code;
        this.message = message;
    }


    public ResponseResult(String code, T data, String message)
    {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    public ResponseResult() {
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
