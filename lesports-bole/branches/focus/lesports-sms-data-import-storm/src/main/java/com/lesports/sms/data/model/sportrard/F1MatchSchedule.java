package com.lesports.sms.data.model.sportrard;

import com.lesports.sms.data.model.CommonSchedule;
import com.lesports.sms.data.utils.DateFormatter;
import com.lesports.sms.data.utils.GameFTypeFormatter;
import com.lesports.utils.xml.annotation.Formatter;
import com.lesports.utils.xml.annotation.XPath;

import java.io.Serializable;
import java.util.List;

/**
 * lesports-projects
 *
 * @author qiaohngxin
 * @since 16-4-18
 */
public class F1MatchSchedule extends CommonSchedule implements Serializable {

    @XPath("/ns2:SportradarData/Sport/@id")
    @Formatter(GameFTypeFormatter.class)
    private Long sportId;
    @XPath("/ns2:SportradarData/Sport/Category/@id")
    private String tourmamentId;
    @XPath("../../@stageId")
    private String stageId;
    @XPath("../../@description")
    private String stageName;
    @XPath("./@startDate")
    @Formatter(DateFormatter.class)
    private String startTime;
    @XPath("./@endDate")
    @Formatter(DateFormatter.class)
    private String endTime;
    @XPath("./@stageId")
    private String partnerId;
    @XPath("./@description")
    private String subStageType;

    public Long getSportId() {
        return sportId;
    }

    public void setSportId(Long sportId) {
        this.sportId = sportId;
    }

    public String getTourmamentId() {
        return tourmamentId;
    }

    public void setTourmamentId(String tourmamentId) {
        this.tourmamentId = tourmamentId;
    }

    public String getStageId() {
        return stageId;
    }

    public void setStageId(String stageId) {
        this.stageId = stageId;
    }

    public String getStageName() {
        return stageName;
    }

    public void setStageName(String stageName) {
        this.stageName = stageName;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(String partnerId) {
        this.partnerId = partnerId;
    }

    public String getSubStageType() {
        return subStageType;
    }

    public void setSubStageType(String subStageType) {
        this.subStageType = subStageType;
    }
}



