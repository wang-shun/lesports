package com.lesports.sms.cooperation.Model;

import com.lesports.model.VideoData;
import com.lesports.model.VideoSearch;

import java.util.List;
import java.util.Map;

/**
 * Created by zhonglin on 2016/5/26.
 */
public class SearchResult {
    private int code;
    private String msg;
    private Data data;
    private String timestamp;

    public int getCode() {
        return code;
    }
    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }
    public void setMsg(String code) {
        this.msg = msg;
    }

    public Data getData() {
        return data;
    }
    public void setData(Data data) {
        this.data = data;
    }

    public String getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public static class Data{
        private List<Row> rows;
        private int total;

        public List<Row> getRows() {
            return rows;
        }
        public void setRows(List<Row> rows) {
            this.rows = rows;
        }
        public int getTotal() {
            return total;
        }
        public void setTotal(int total) {
            this.total = total;
        }
    }

    public static class Row{
        private String updateAt;
        private long id;
        private boolean deleted;
        private String name;
        private int type;
        private List<Map<String,String>> multiLangNames;
        private int[] allowCountries;
        private int[] platforms;

        public String getUpdateAt() {
            return updateAt;
        }
        public void setUpdateAt(String updateAt) {
            this.updateAt = updateAt;
        }
        public long getId() {
            return id;
        }
        public void setId(long id) {
            this.id = id;
        }

        public boolean getDeleted() {
            return deleted;
        }
        public void setDeleted(boolean deleted) {
            this.deleted = deleted;
        }
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }

        public int getType() {
            return type;
        }
        public void setType(int type) {
            this.type = type;
        }

        public List<Map<String,String>> getMultiLangNames() {
            return multiLangNames;
        }
        public void setMultiLangNames(List<Map<String,String>> multiLangNames) {
            this.multiLangNames = multiLangNames;
        }

        public int[] getAllowCountries() {
            return allowCountries;
        }
        public void setAllowCountries(int[] allowCountries) {
            this.allowCountries = allowCountries;
        }


        public int[] getPlatforms() {
            return platforms;
        }
        public void setPlatforms(int[] platforms) {
            this.platforms = platforms;
        }
    }
}
