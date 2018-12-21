package com.lesports.msg.cache;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * lesports-projects.
 *
 * @author: pangchuanxiao
 * @since: 2015/7/10
 */
@JsonIgnoreProperties
public class CleanCacheResult {
    private Content result;

    public Content getResult() {
        return result;
    }

    public void setResult(Content result) {
        this.result = result;
    }

    public static class Content {
        private String msg;
        private String success;

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public String getSuccess() {
            return success;
        }

        public void setSuccess(String success) {
            this.success = success;
        }
    }

    public boolean isSuccess() {
        if (null == result) {
            return false;
        }
        if (null == result.getSuccess()) {
            return false;
        }
        if (result.getSuccess().equalsIgnoreCase("succeed")) {
            return true;
        }
        return false;
    }
}
