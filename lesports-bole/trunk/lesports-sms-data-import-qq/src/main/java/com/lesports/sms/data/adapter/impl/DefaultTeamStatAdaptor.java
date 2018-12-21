package com.lesports.sms.data.adapter.impl;

import com.google.common.collect.Lists;
import com.hankcs.hanlp.corpus.util.StringUtils;
import com.lesports.sms.api.common.CompetitorType;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.data.adapter.DefualtAdaptor;
import com.lesports.sms.data.model.TransModel;
import com.lesports.sms.data.model.commonImpl.DefaultTeamStats;
import com.lesports.sms.data.process.impl.DefaultCompetitorStatsProcessor;
import com.lesports.sms.model.CompetitionSeason;
import com.lesports.sms.model.CompetitorSeasonStat;
import com.lesports.sms.model.Team;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by qiaohongxin on 2016/5/11.
 */
@Service("teamStatAdaptor")
public class DefaultTeamStatAdaptor extends DefualtAdaptor {
    private static Logger LOG = LoggerFactory.getLogger(DefaultTeamStatAdaptor.class);
    @Resource
    DefaultCompetitorStatsProcessor competitorStatsProcessor;

    public Boolean nextProcessor(TransModel transModel, Object object) {
        try {
            DefaultTeamStats.TeamStat teamStats = (DefaultTeamStats.TeamStat) object;
            Team team = SbdsInternalApis.getTeamByQQId(teamStats.getPartnerId());
            if (team == null) return false;
            if (CollectionUtils.isNotEmpty(teamStats.getStatTList())) {
                for (DefaultTeamStats.StatT statT : teamStats.getStatTList()) {
                    CompetitorSeasonStat stat = convertModelToCompetitorStats(team.getId(), transModel.getCsid(), statT);
                    LOG.warn("teamCompetitionSeasonStats:{} adpter fail,the sid:{}", stat.toString());
                    competitorStatsProcessor.process(stat);
                }
            }
            return true;
        } catch (Exception e) {
            LOG.error("competionSeasonStats adapter fail", e);
        }
        return false;
    }

    private CompetitorSeasonStat convertModelToCompetitorStats(Long tid, Long csid1, DefaultTeamStats.StatT statT) {
        CompetitorSeasonStat stat = new CompetitorSeasonStat();
        Long cid = statT.getCid();
        if (cid == null || cid.longValue() == 0L)
            cid = SbdsInternalApis.getCompetitionSeasonById(csid1).getCid();
        if (!StringUtils.isBlankOrNull(statT.getSeason())) {
            CompetitionSeason competitionSeason = SbdsInternalApis.getCompetitionSeasonByCidAndSeason(cid, statT.getSeason().substring(0, 4));
            csid1 = competitionSeason == null ? csid1 : competitionSeason.getId();
        }
        stat.setCid(cid);
        stat.setCsid(csid1);
        stat.setCompetitorId(tid);
        stat.setType(CompetitorType.TEAM);
        stat.setStats(statT.getStats());
        return stat;
    }

}
