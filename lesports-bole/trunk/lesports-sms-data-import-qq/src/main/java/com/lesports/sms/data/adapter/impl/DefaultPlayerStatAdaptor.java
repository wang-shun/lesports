package com.lesports.sms.data.adapter.impl;

import com.lesports.sms.api.common.CompetitorType;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.data.adapter.DefualtAdaptor;
import com.lesports.sms.data.model.TransModel;
import com.lesports.sms.data.model.commonImpl.DefaultPlayerStats;
import com.lesports.sms.data.model.commonImpl.DefaultTeamStats;
import com.lesports.sms.data.process.impl.DefaultCompetitorStatsProcessor;
import com.lesports.sms.model.CompetitionSeason;
import com.lesports.sms.model.CompetitorSeasonStat;
import com.lesports.sms.model.Player;
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

    public Boolean nextProcessor(TransModel transModel, Object object) {
        try {
            DefaultPlayerStats.PlayerStats playerStat = (DefaultPlayerStats.PlayerStats) object;
            Player player = SbdsInternalApis.getPlayerByQQId(playerStat.getPartnerId());
            if (player == null) {
                LOG.warn("the playerPartnerId:{} is not exist", playerStat.getPartnerId());
                return false;
            }
            if (CollectionUtils.isNotEmpty(playerStat.getMainStats())) {
                for (DefaultPlayerStats.PlayerStats.StatT statT : playerStat.getMainStats()) {
                    CompetitorSeasonStat stat = convertModelToCompetitorStats(player.getId(), transModel.getCsid(), statT);
                    LOG.info("the playerCompetitorSeasonStats:{} adator is created and send to the nextProcessor", stat.toString());
                    competitorStatsProcessor.process(stat);
                }
            }
            if (CollectionUtils.isNotEmpty(playerStat.getSubStats())) {
                for (DefaultPlayerStats.PlayerStats.StatT statT : playerStat.getSubStats()) {
                    CompetitorSeasonStat stat = convertModelToCompetitorStats(player.getId(), transModel.getCsid(), statT);
                    competitorStatsProcessor.process(stat);
                }
            }

            return true;
        } catch (Exception e) {
            LOG.error("player season Stats adapt fail", e);
        }
        return false;
    }

    private CompetitorSeasonStat convertModelToCompetitorStats(Long pid, Long csid1, DefaultPlayerStats.PlayerStats.StatT statT) {
        Long cid = statT.getCid();
        if (cid == null || cid.longValue() == 0L) {
            cid = SbdsInternalApis.getCompetitionSeasonById(csid1).getCid();
        }
        CompetitionSeason competitionSeason = SbdsInternalApis.getCompetitionSeasonByCidAndSeason(cid, statT.getSeason());
        Long csid = competitionSeason == null ? csid1 : competitionSeason.getId();
        CompetitorSeasonStat stat = new CompetitorSeasonStat();
        stat.setCid(SbdsInternalApis.getCompetitionSeasonById(csid).getCid());
        stat.setCsid(csid);
        stat.setCompetitorId(pid);
        stat.setType(CompetitorType.PLAYER);
        stat.setStats(statT.getElementStats());
        stat.setAvgStats(statT.getElementAvgStats());
        return stat;
    }

}