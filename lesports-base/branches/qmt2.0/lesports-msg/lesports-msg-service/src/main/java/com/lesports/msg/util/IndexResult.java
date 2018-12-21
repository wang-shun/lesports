package com.lesports.msg.util;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * aa.
 *
 * @author pangchuanxiao
 * @since 2015/12/25
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class IndexResult {
    private Integer code;
    private String msg;
    private Data data;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class Data {
        private Boolean success;

        public Boolean getSuccess() {
            return success;
        }

        public void setSuccess(Boolean success) {
            this.success = success;
        }
    }
}
