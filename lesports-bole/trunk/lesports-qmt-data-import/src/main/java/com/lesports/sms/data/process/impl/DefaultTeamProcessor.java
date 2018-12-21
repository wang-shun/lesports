package com.lesports.sms.data.process.impl;

import com.google.common.collect.Lists;
import com.lesports.qmt.sbd.SbdPlayerInternalApis;
import com.lesports.qmt.sbd.SbdTeamInternalApis;
import com.lesports.qmt.sbd.model.MatchStats;
import com.lesports.qmt.sbd.model.Player;
import com.lesports.qmt.sbd.model.Team;
import com.lesports.sms.data.process.BeanProcessor;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by qiaohongxin on 2016/5/11.
 */
@Service("teamProcessor")
public class DefaultTeamProcessor extends BeanProcessor {
    private static Logger LOG = LoggerFactory.getLogger(DefaultTeamProcessor.class);

    @Override
    public Boolean process(Object object) {
        Team newTeam = (Team) object;
        try {
            Team team = SbdTeamInternalApis.getTeamByPartner(newTeam.getPartners().get(0));
            if (team == null) {
                SbdTeamInternalApis.saveTeam(newTeam);
                return true;
            } else {
                List<String> configs = getUpdateProperty();
                if (CollectionUtils.isEmpty(configs)) {
                    LOG.warn("NO pemission to Update property");
                    return false;
                }
                team = (Team) updateObject(configs, team, newTeam);
                if (SbdTeamInternalApis.saveTeam(team) > 0) {
                    LOG.warn("The Team:{} is updated", team.getId());
                }
            }
            return true;
        } catch (Exception e) {
            LOG.error("Schedule Exception error ,e:{}", e);
            return false;
        }
    }

    private List<String> getUpdateProperty() {
        List<String> properties = Lists.newArrayList();
        properties.add("gameFType");
        properties.add("homeGround");
        properties.add("honors");
        properties.add("ranks");
        return properties;
    }
}
