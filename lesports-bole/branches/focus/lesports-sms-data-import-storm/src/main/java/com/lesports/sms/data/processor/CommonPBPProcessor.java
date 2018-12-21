package com.lesports.sms.data.processor;

import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.data.model.sportrard.SportrardTeam;
import com.lesports.sms.data.processor.olympic.AbstractProcessor;
import com.lesports.sms.model.Team;
import org.slf4j.Logger;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by qiaohongxin on 2016/5/11.
 */
public abstract class CommonPBPProcessor extends AbstractProcessor {
    private static Logger LOG = org.slf4j.LoggerFactory.getLogger(CommonPBPProcessor.class);

    public Boolean processTeam(SportrardTeam team) {
        try {
            Team team1 = SbdsInternalApis.getTeamByPartnerIdAndPartnerType(team.getPartnerId(), getPartnerType());
            if (team1 != null) {
                Set cids = team1.getCids();
                cids.add(getCsid(team));
                team1.setCids(cids);
            } else {
                Set cids = new HashSet();
                cids.add(getCsid(team));
                team1 = new Team();
                team1.setAllowCountries(getAllowCountries());
                team1.setName(team.getName());
                team1.setMultiLangNames(getMultiLang(team.getName()));
                team1.setPartnerId(team.getPartnerId());
                team1.setGameFType(getGameFTypeId(team));
                team1.setPartnerType(getPartnerType());
                team1.setCids(cids);
            }
            SbdsInternalApis.saveTeam(team1);
            LOG.info("save fail,sms database not exist:teamEnName:{}", team.getName());
            return true;
        } catch (Exception e) {
            LOG.error("parse team xml error", e);
            return false;
        }
    }

    public abstract Integer getPartnerType();

    public abstract Long getCsid(SportrardTeam team);

    public abstract Long getGameFTypeId(SportrardTeam team);
}
