package com.lesports.sms.data.model.olympic;

import com.lesports.utils.xml.annotation.XPath;

import java.io.Serializable;

/**
 * lesports-projects
 *
 * @author pangchuanxiao
 * @since 16-2-18
 */
public class Code implements Serializable {
    @XPath("./@Code")
    private String code;
    //只在SPORT CODE中使用
    @XPath("./@Type")
    private String type;
    //只在SPORT CODE中使用
    @XPath("./@Discipline")
    private String discipline;
    @XPath("./@Sport")
    private String sportCode;
    @XPath("./@Schedule")
    private String schedule;
    @XPath("./Language[@Language='ENG']/@Description")
    private String description;
    @XPath("./Language[@Language='ENG']/@LongDescription")
    private String longDescription;

    public String getDiscipline() {
        return discipline;
    }

    public void setDiscipline(String discipline) {
        this.discipline = discipline;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLongDescription() {
        return longDescription;
    }

    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }

    public String getSportCode() {
        return sportCode;
    }

    public void setSportCode(String sportCode) {
        this.sportCode = sportCode;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }
}
