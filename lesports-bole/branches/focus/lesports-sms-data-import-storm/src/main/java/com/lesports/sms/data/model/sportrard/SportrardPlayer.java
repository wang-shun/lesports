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
public class SportrardPlayer implements Serializable {
    @XPath("/ns2:SportradarData/Sport/@id")
    private String sportId;
    @XPath("/ns2:SportradarData/Sport/Teams/Team/@id")
    private String teamId;
    @XPath("/ns2:SportradarData/Sport/Teams/Team/@gender")
    private String gender;
    @XPath("/@playerId")
    private String partnerId;
    @XPath("./@playerName")
    private String playerEnName;
    @XPath("./Translation[@key='playerName']/TranslatedValue[@lang='zh']/@value")
    private String playerName;
    @XPath("./PlayerInfo/PlayerInfoEntry[@name='Shirt number']/@value")
    private String shirtName;
    @XPath("./PlayerInfo/PlayerInfoEntry[@name='Position']/@value")
    private String position;
    @XPath("./PlayerInfo/PlayerInfoEntry[@name='Weight']/@value")
    private String weight;
    @XPath("./PlayerInfo/PlayerInfoEntry[@name='Height']/@value")
    private String height;
    @XPath("./PlayerInfo/PlayerInfoEntry[@name='Date of birth']/@value")
    private String birth;
    @XPath("./PlayerInfo/PlayerInfoEntry[@name='Nationality']/@value")
    private String nationality;

    public String getSportId() {
        return sportId;
    }

    public String getTeamId() {
        return teamId;
    }

    public String getGender() {
        return gender;
    }

    public String getPartnerId() {
        return partnerId;
    }

    public String getPlayerEnName() {
        return playerEnName;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getShirtName() {
        return shirtName;
    }

    public String getPosition() {
        return position;
    }

    public String getWeight() {
        return weight;
    }

    public String getHeight() {
        return height;
    }

    public String getBirth() {
        return birth;
    }

    public String getNationality() {
        return nationality;
    }
}




