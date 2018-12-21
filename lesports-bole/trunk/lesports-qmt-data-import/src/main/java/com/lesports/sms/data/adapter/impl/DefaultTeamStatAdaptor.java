package com.lesports.sms.data.adapter.impl;

import com.google.common.collect.Lists;
import com.lesports.qmt.sbd.SbdCompetitionSeasonInternalApis;
import com.lesports.qmt.sbd.SbdTeamInternalApis;
import com.lesports.qmt.sbd.api.common.CompetitorType;
import com.lesports.qmt.sbd.model.CompetitionSeason;
import com.lesports.qmt.sbd.model.CompetitorSeasonStat;
import com.lesports.qmt.sbd.model.PartnerType;
import com.lesports.qmt.sbd.model.Team;
import com.lesports.sms.data.adapter.DefualtAdaptor;
import com.lesports.sms.data.model.commonImpl.DefaultTeamStats;
import com.lesports.sms.data.process.impl.DefaultCompetitorStatsProcessor;
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

    public Boolean nextProcessor(PartnerType partnerType, Long csid, Object object) {
        try {
            DefaultTeamStats.TeamStat teamStats = (DefaultTeamStats.TeamStat) object;
            Team team = SbdTeamInternalApis.getTeamByPartner(getPartner(teamStats.getPartnerId(), partnerType));
            if (team == null) return false;
            if (CollectionUtils.isNotEmpty(teamStats.getStatTList())) {
                for (DefaultTeamStats.StatT statT : teamStats.getStatTList()) {
                    CompetitorSeasonStat stat = convertModelToCompetitorStats(team.getId(), csid, statT);
                    competitorStatsProcessor.process(stat);
                }
            }
            return true;
        } catch (Exception e) {
            LOG.error("adaptor teamStats fail", e);
        }
        return false;
    }

    private CompetitorSeasonStat convertModelToCompetitorStats(Long tid, Long csid1, DefaultTeamStats.StatT statT) {
        CompetitorSeasonStat stat = new CompetitorSeasonStat();
        Long cid = statT.getCid();
        if (cid == null || cid.longValue() == 0L)
            cid = SbdCompetitionSeasonInternalApis.getCompetitionSeasonById(csid1).getCid();
        CompetitionSeason competitionSeason = SbdCompetitionSeasonInternalApis.getCompetitionSeasonByCidAndSeason(cid, statT.getSeason().substring(0, 4));
        Long csid = competitionSeason == null ? csid1 : competitionSeason.getId();
        stat.setCid(cid);
        stat.setCsid(csid);
        stat.setCompetitorId(tid);
        stat.setType(CompetitorType.TEAM);
        stat.setStats(statT.getStats());
        stat.setAvgStats(statT.getAvgStats());
        return stat;
    }

}
