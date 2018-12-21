package com.lesports.sms.data.model.olympic;

import com.lesports.utils.xml.annotation.XPath;

import java.io.Serializable;

/**
 * lesports-projects
 *
 * @author pangchuanxiao
 * @since 16-2-19
 */
public class Participant implements Serializable {

    @XPath("/OdfBody/Competition/@Code")
    private String competitionCode;
    @XPath("/OdfBody/@DocumentCode")
    private String sportType;
    @XPath("./@Code")
    private String code;
    @XPath("./@MainFunctionId")
    private String functionType;
    @XPath("./@PrintName")
    private String familyName;
    @XPath("./@Gender")
    private String gender;
    @XPath("./@Height")
    private Integer height;
    @XPath("./@Weight")
    private Integer wight;
    @XPath("./@Organisation")
    private String organisation;
    @XPath("./@BirthDate")
    private String birthDate;

    public String getCompetitionCode() {
        return competitionCode;
    }

    public void setCompetitionCode(String competitionCode) {
        this.competitionCode = competitionCode;
    }

    public String getSportType() {
        return sportType;
    }

    public void setSportType(String sportType) {
        this.sportType = sportType;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getOrganisation() {
        return organisation;
    }

    public void setOrganisation(String organisation) {
        this.organisation = organisation;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Integer getWight() {
        return wight;
    }

    public void setWight(Integer wight) {
        this.wight = wight;
    }

    public String getFunctionType() {
        return functionType;
    }

    public void setFunctionType(String functionType) {
        this.functionType = functionType;
    }
}
