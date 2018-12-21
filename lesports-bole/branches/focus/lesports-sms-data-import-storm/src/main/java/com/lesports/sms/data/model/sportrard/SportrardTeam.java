package com.lesports.sms.data.model.sportrard;

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
public class SportrardTeam implements Serializable {

    @XPath("/ns2:SportradarData/Sport/@id")
    private String sportId;
    @XPath("/ns2:SportradarData/Sport/Category/Tournament/@id")
    private String tournamentId;
    @XPath("/@gender")
    private String gender;//F
    @XPath("/@id")
    private String partnerId;
    @XPath("./@name")
    private String name;

    public String getSportId() {
        return sportId;
    }

    public String getTournamentId() {
        return tournamentId;
    }

    public String getGender() {
        return gender;
    }

    public String getPartnerId() {
        return partnerId;
    }

    public String getName() {
        return name;
    }
}





