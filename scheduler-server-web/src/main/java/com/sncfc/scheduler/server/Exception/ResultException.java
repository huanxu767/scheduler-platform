package com.sncfc.scheduler.server.Exception;

/**
 * 自定义异常类
 * <p/>
 */
public class ResultException extends RuntimeException {
    /**
     * Exception code
     */
    private String resultCode = "UN_KNOWN_EXCEPTION";
    /**
     * Exception message
     */
    private String resultMsg = "未知异常";

    public ResultException() {

    }
    public ResultException(String resultMsg) {
        this.resultMsg = resultMsg;
    }
    public ResultException(String resultCode, String resultMsg) {
        this.resultCode = resultCode;
        this.resultMsg = resultMsg;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultMsg() {
        return resultMsg;
    }

    public void setResultMsg(String resultMsg) {
        this.resultMsg = resultMsg;
    }
}
