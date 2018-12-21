package com.lesports.monitor.cat.constant;

/**
 * Created by zhangdeqiang on 2017/3/9.
 */
public class JerseyResponseException extends RuntimeException {
    /** serialVersionUID */
    private static final long serialVersionUID = 2332608236621015980L;

    private String code;

    public JerseyResponseException() {
        super();
    }

    public JerseyResponseException(String message) {
        super(message);
    }

    public JerseyResponseException(BussinessCode code) {

        this(String.valueOf(code.getId()), code.getDesc());
    }

    public JerseyResponseException(String code, String message) {
        super(message);
        this.code = code;
    }

    public JerseyResponseException(Throwable cause) {
        super(cause);
    }

    public JerseyResponseException(String message, Throwable cause) {
        super(message, cause);
    }

    public JerseyResponseException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
