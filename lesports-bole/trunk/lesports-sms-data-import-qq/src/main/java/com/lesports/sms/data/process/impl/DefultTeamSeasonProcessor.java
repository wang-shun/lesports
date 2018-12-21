package com.lesports.sms.data.process.impl;

import com.google.common.collect.Lists;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.data.process.BeanProcessor;
import com.lesports.sms.model.TeamSeason;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by qiaohongxin on 2016/5/11.
 */
@Service("teamSeasonProcessor")
public class DefultTeamSeasonProcessor extends BeanProcessor {

    private static Logger LOG = LoggerFactory.getLogger(DefultTeamSeasonProcessor.class);

    public Boolean process(Object object) {
        TeamSeason newTeamSeason = (TeamSeason) object;
        try {
            List<TeamSeason> teamSeasonList = SbdsInternalApis.getTeamSeasonsByTeamIdAndCSId(newTeamSeason.getTid(), newTeamSeason.getCsid());
            if (CollectionUtils.isEmpty(teamSeasonList)) {
                SbdsInternalApis.saveTeamSeason(newTeamSeason);
                LOG.info("New TeamSeason:{} saved", newTeamSeason.getTid());
                return true;
            }
            TeamSeason teamSeason = teamSeasonList.get(0);
            List<String> configs = getUpdateProperty();
            if (CollectionUtils.isEmpty(configs)) {
                LOG.warn("NO pemission to Update property");
                return false;
            }
            teamSeason = (TeamSeason) updateObject(configs, teamSeason, newTeamSeason);
            if (SbdsInternalApis.saveTeamSeason(teamSeason) > 0) {
                LOG.warn("The TEAMSEASON,csid:{},tid:{}", teamSeason.getCsid(), teamSeason.getTid());
            }
            return true;
        } catch (Exception e) {
            LOG.error("LiveScore Exception error ,e:{}", e);
            return false;
        }
    }

    private List<String> getUpdateProperty() {
        List<String> properties = Lists.newArrayList();
        properties.add("coachId");
        properties.add("players");
        properties.add("corePlayers");
        return properties;
    }

}


