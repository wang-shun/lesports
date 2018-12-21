package com.lesports.sms.data.model.olympic;

import com.lesports.sms.data.utils.SimpleDateFormatter;
import com.lesports.utils.xml.annotation.Formatter;
import com.lesports.utils.xml.annotation.XPath;

import java.io.Serializable;
import java.util.List;

/**
 * trunk.
 *
 * @author pangchuanxiao
 * @since 2016/3/14
 */
public class RecordDetail implements Serializable {
    @XPath("/OdfBody/Competition/Record/@Code")
    private String recordCode;
    @XPath("./@RecordType")
    private String recordType;
    @XPath("./@Shared")
    private String isShared;
    @XPath("./RecordData")
    private List<RecordData> datas;

    public static class RecordData implements Serializable {
        @XPath("./@Current")
        private String isCurrent;
        @XPath("./@Date")
        @Formatter(SimpleDateFormatter.class)
        private String genDate;
        @XPath("./@Place")
        private String genCity;
        @XPath("./@Result")
        private String result;
        @XPath("./@ResultType")
        private String resultType;
        @XPath("./Competitor/@Organisation")
        private String genConntry;
        @XPath("./Competitor/@Code")
        private String CompetitorCode;
        @XPath("./Competitor/@Type")
        private String CompetitorType;

        public String getIsCurrent() {
            return isCurrent;
        }

        public void setIsCurrent(String isCurrent) {
            this.isCurrent = isCurrent;
        }

        public String getGenDate() {
            return genDate;
        }

        public void setGenDate(String genDate) {
            this.genDate = genDate;
        }

        public String getGenConntry() {
            return genConntry;
        }

        public void setGenConntry(String genConntry) {
            this.genConntry = genConntry;
        }

        public String getGenCity() {
            return genCity;
        }

        public void setGenCity(String genCity) {
            this.genCity = genCity;
        }

        public String getResult() {
            return result;
        }

        public void setResult(String result) {
            this.result = result;
        }

        public String getResultType() {
            return resultType;
        }

        public void setResultType(String resultType) {
            this.resultType = resultType;
        }

        public String getCompetitorCode() {
            return CompetitorCode;
        }

        public void setCompetitorCode(String competitorCode) {
            CompetitorCode = competitorCode;
        }

        public String getCompetitorType() {
            return CompetitorType;
        }

        public void setCompetitorType(String competitorType) {
            CompetitorType = competitorType;
        }
    }

    public List<RecordData> getDatas() {
        return datas;
    }

    public void setDatas(List<RecordData> datas) {
        this.datas = datas;
    }

    public String getIsShared() {
        return isShared;
    }

    public void setIsShared(String isShared) {
        this.isShared = isShared;
    }

    public String getRecordType() {
        return recordType;
    }

    public void setRecordType(String recordType) {
        this.recordType = recordType;
    }

    public String getRecordCode() {
        return recordCode;
    }

    public void setRecordCode(String recordCode) {
        this.recordCode = recordCode;
    }
}

