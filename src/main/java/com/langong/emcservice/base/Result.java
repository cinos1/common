package com.langong.emcservice.base;

import java.io.Serializable;

public class Result<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    private boolean success = true;
    private String message = "操作成功！";
    private Integer code = 0;
    private T result;
    private long timestamp = System.currentTimeMillis();

    public Result() {
    }

    public static Result<Object> ok() {
        Result<Object> r = new Result<>();
        r.setSuccess(true);
        r.setCode(CommonConstant.SC_OK_200);
        r.setMessage("成功");
        return r;
    }

    public static Result<Object> ok(String msg) {
        Result<Object> r = new Result<>();
        r.setSuccess(true);
        r.setCode(CommonConstant.SC_OK_200);
        r.setMessage(msg);
        return r;
    }

    public static Result<Object> ok(Object data) {
        Result<Object> r = new Result<>();
        r.setSuccess(true);
        r.setCode(CommonConstant.SC_OK_200);
        r.setResult(data);
        return r;
    }

    public static Result<Object> error(String msg) {
        return error(CommonConstant.SC_INTERNAL_SERVER_ERROR_500, msg);
    }

    public static Result<Object> error(int code, String msg) {
        Result<Object> r = new Result<>();
        r.setCode(code);
        r.setMessage(msg);
        r.setSuccess(false);
        return r;
    }

    public boolean isSuccess() {
        return this.success;
    }

    public String getMessage() {
        return this.message;
    }

    public Integer getCode() {
        return this.code;
    }

    public T getResult() {
        return this.result;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }


    public String toString() {
        return "Result(success=" + this.isSuccess() + ", message=" + this.getMessage() + ", code=" + this.getCode() + ", result=" + this.getResult() + ", timestamp=" + this.getTimestamp() + ")";
    }
}