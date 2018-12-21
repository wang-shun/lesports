package com.lesports.sms.data.adapter.impl;

import com.lesports.qmt.sbd.SbdCompetitionSeasonInternalApis;
import com.lesports.qmt.sbd.SbdPlayerInternalApis;
import com.lesports.qmt.sbd.api.common.CompetitorType;
import com.lesports.qmt.sbd.model.CompetitionSeason;
import com.lesports.qmt.sbd.model.CompetitorSeasonStat;
import com.lesports.qmt.sbd.model.PartnerType;
import com.lesports.qmt.sbd.model.Player;
import com.lesports.sms.data.adapter.DefualtAdaptor;
import com.lesports.sms.data.model.commonImpl.DefaultPlayerStats;
import com.lesports.sms.data.model.commonImpl.DefaultTeamStats;
import com.lesports.sms.data.process.impl.DefaultCompetitorStatsProcessor;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by qiaohongxin on 2016/5/11.
 */
@Service("playerStatAdaptor")
public class DefaultPlayerStatAdaptor extends DefualtAdaptor {
    private static Logger LOG = LoggerFactory.getLogger(DefaultPlayerStatAdaptor.class);
    @Resource
    DefaultCompetitorStatsProcessor competitorStatsProcessor;

    public Boolean nextProcessor(PartnerType partnerType, Long csid, Object object) {
        try {
            DefaultPlayerStats.PlayerStats playerStat = (DefaultPlayerStats.PlayerStats) object;
            Player player = SbdPlayerInternalApis.getPlayerByPartner(getPartner(playerStat.getPartnerId(), partnerType));
            if (player == null) return false;
            if (CollectionUtils.isNotEmpty(playerStat.getMainStats())) {
                for (DefaultPlayerStats.PlayerStats.StatT statT : playerStat.getMainStats()) {
                    CompetitorSeasonStat stat = convertModelToCompetitorStats(player.getId(), csid, statT);
                    competitorStatsProcessor.process(stat);
                }
            }
            if (CollectionUtils.isNotEmpty(playerStat.getSubStats())) {
                for (DefaultPlayerStats.PlayerStats.StatT statT : playerStat.getSubStats()) {
                    CompetitorSeasonStat stat = convertModelToCompetitorStats(player.getId(), csid, statT);
                    competitorStatsProcessor.process(stat);
                }
            }

            return true;
        } catch (Exception e) {
            LOG.error("adaptor playerStat fail", e);
        }
        return false;
    }


    private CompetitorSeasonStat convertModelToCompetitorStats(Long pid, Long csid1, DefaultPlayerStats.PlayerStats.StatT statT) {
        Long cid = statT.getCid();
        if (cid == null || cid.longValue() == 0L)
            cid = SbdCompetitionSeasonInternalApis.getCompetitionSeasonById(csid1).getCid();
        CompetitionSeason competitionSeason = SbdCompetitionSeasonInternalApis.getCompetitionSeasonByCidAndSeason(cid, statT.getSeason());
        Long csid = competitionSeason == null ? csid1 : competitionSeason.getId();
        CompetitorSeasonStat stat = new CompetitorSeasonStat();
        stat.setCid(SbdCompetitionSeasonInternalApis.getCompetitionSeasonById(csid).getCid());
        stat.setCsid(csid);
        stat.setCompetitorId(pid);
        stat.setType(CompetitorType.PLAYER);
        stat.setStats(statT.getElementStats());
        return stat;
    }

}