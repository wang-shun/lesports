package com.lesports.sms.data.process.impl;

import com.google.common.collect.Lists;
import com.lesports.query.InternalCriteria;
import com.lesports.query.InternalQuery;
import com.lesports.sms.api.common.GroundType;
import com.lesports.sms.api.common.MatchStatus;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.data.process.BeanProcessor;
import com.lesports.sms.model.Match;
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
            Match match = SbdsInternalApis.getMatchByQQId(newMatch.getQQId());
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
            Long matchId = SbdsInternalApis.getmatchIdByStartTimeAndCompetitorIds(newMatch.getStartDate(), homeTeamId, awayTeamId);
            if (matchId != null && matchId > 0) match = SbdsInternalApis.getMatchById(matchId);
            if (match == null) {
                SbdsInternalApis.saveMatch(newMatch);
                LOG.info("Match:partnreId:{} created", newMatch.getQQId());
                return true;
            } else {
                List<String> configs = getUpdatePropertyByStatus(MatchStatus.MATCH_NOT_START);
                if (CollectionUtils.isEmpty(configs)) {
                    LOG.warn("NO pemission to Update property");
                    return false;
                }
                Match match1 = (Match) updateObject(configs, match, newMatch);
                if (SbdsInternalApis.saveMatch(match1) > 0) {
                    LOG.warn("The match:{} is updated", matchId);
                }
            }
            return true;
        } catch (Exception e) {
            LOG.error("Schedule Exception error ,e:{}", e);
            return false;
        }
    }

    private List<String> getUpdatePropertyByStatus(MatchStatus status) {
        List<String> properties = Lists.newArrayList();
        if (status.equals(MatchStatus.MATCH_NOT_START)) {
            properties.add("startTime");
            properties.add("startDate");
            properties.add("QQId");
            return properties;
        } else {
            return null;
        }

    }
}
