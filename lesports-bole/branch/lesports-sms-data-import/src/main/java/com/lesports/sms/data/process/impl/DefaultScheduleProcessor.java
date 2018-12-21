package com.lesports.sms.data.process.impl;

import com.google.common.collect.Lists;
import com.lesports.api.common.MatchStatus;
import com.lesports.qmt.sbd.SbdMatchInternalApis;
import com.lesports.qmt.sbd.api.common.GroundType;
import com.lesports.qmt.sbd.model.Match;
import com.lesports.sms.data.process.BeanProcessor;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

/**
 * Created by qiaohongxin on 2016/5/11.
 */
@Service("scheduleProcessor")
public class DefaultScheduleProcessor extends BeanProcessor {
    private static Logger LOG = org.slf4j.LoggerFactory.getLogger(DefaultScheduleProcessor.class);

    public Boolean process(Object object) {
        Match newMatch = (Match) object;
        try {
            Match match = SbdMatchInternalApis.getMatchByPartner(newMatch.getPartners().get(0));
            Set<Match.Competitor> competitorSet = newMatch.getCompetitors();
            Long homeTeamId = 0L;
            Long awayTeamId = 0L;
            for (Match.Competitor currentCometitor : competitorSet) {
                if (currentCometitor.getGround().equals(GroundType.HOME)) {
                    homeTeamId = currentCometitor.getCompetitorId();
                } else {
                    awayTeamId = currentCometitor.getCompetitorId();
                }
            }
            Long matchId = SbdMatchInternalApis.getMatchIdByStartTimeAndCompetitorIds(newMatch.getStartTime(), homeTeamId, awayTeamId);
            if (match == null && matchId <= 0 && !newMatch.getStatus().equals(MatchStatus.MATCH_CANCELED)) {
                SbdMatchInternalApis.saveMatch(newMatch);
                return true;
            } else {
                if (newMatch.getStatus() == MatchStatus.MATCH_CANCELED) {
                    SbdMatchInternalApis.deleteMatch(matchId);
                    LOG.warn("The match is cancelled and not created }", matchId);
                    return false;
                }
                List<String> configs = Lists.newArrayList();
                if (CollectionUtils.isEmpty(configs)) {
                    Field[] fields = match.getClass().getDeclaredFields();
                    for (int index = 0; index < fields.length; index++) {
                        String propertyName = fields[index].getName();
                        configs.add(propertyName);
                    }
                }
                configs = getUpdatePropertyByStatus(match.getStatus());
                if (CollectionUtils.isEmpty(configs)) {
                    LOG.warn("NO pemission to Update property");
                    return false;
                }
                Match match1 = (Match) updateObject(configs, match, newMatch);
                if (SbdMatchInternalApis.saveMatch(match1) > 0) {
                    LOG.warn("The match:{} is updated", matchId);
                }
            }
            return true;
        } catch (Exception e) {
            LOG.error("Schedule Exception error ,e:{}", e);
            return false;
        }
    }

    private List<String> getUpdatePropertyByStatus(com.lesports.api.common.MatchStatus status) {
        List<String> properties = Lists.newArrayList();
        if (status.equals(MatchStatus.MATCH_NOT_START)) {
            properties.add("startTime");
            properties.add("startDate");
            properties.add("round");
            properties.add("stage");
            properties.add("status");
            properties.add("competitors");
            return properties;
        } else {
            return null;
        }

    }
}
