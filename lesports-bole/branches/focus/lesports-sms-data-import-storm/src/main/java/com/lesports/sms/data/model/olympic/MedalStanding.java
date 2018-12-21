package com.lesports.sms.data.model.olympic;

import com.lesports.utils.xml.annotation.XPath;

import java.io.Serializable;

/**
 * trunk.
 *
 * @author pangchuanxiao
 * @since 2016/3/14
 */
public class MedalStanding implements Serializable {
    @XPath("/OdfBody/@DocumentCode")
    private String sportCode;
    @XPath("./@Organisation")
    private String organisation;
    @XPath("./@rank")
    private String rank;

    public String getSportCode() {
        return sportCode;
    }

    public void setSportCode(String sportCode) {
        this.sportCode = sportCode;
    }

    public String getOrganisation() {
        return organisation;
    }

    public void setOrganisation(String organisation) {
        this.organisation = organisation;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }
}
