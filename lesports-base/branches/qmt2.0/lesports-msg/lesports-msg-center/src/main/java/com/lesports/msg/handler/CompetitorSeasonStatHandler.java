package com.lesports.msg.handler;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lesports.msg.Constants;
import com.lesports.msg.core.LeMessage;
import com.lesports.sms.api.common.CompetitorType;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.model.CompetitorSeasonStat;
import com.lesports.sms.model.MatchStats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * User: qiaohongxin
 * Time: 15-11-10 : 下午2:52
 */
@Component
public class CompetitorSeasonStatHandler extends AbstractHandler implements IHandler {
    private static final Logger LOG = LoggerFactory.getLogger(CompetitorSeasonStatHandler.class);

    @Override
    public void handle(LeMessage message) {
        long matchId = message.getEntityId();
        long csid = Long.valueOf(message.getContent().getFromMsgBody("csid"));
        MatchStats matchStats = SbdsInternalApis.getMatchStatsById(matchId);
        if (matchStats == null) {
            LOG.warn("fail to handle message : {}, matchId : {} no exists.");
        }
        if (updatePlayerBest(csid, matchStats)) {
            LOG.info("update bole competition season : {}, result : {}", message.getEntityId(), true);
        }
        LOG.warn("update bole competition season : {}, result : {}", message.getEntityId(), false);
    }

    private boolean updatePlayerBest(Long csid, MatchStats matchStats) {
        List<MatchStats.CompetitorStat> competitorStatList = matchStats.getCompetitorStats();
        List<MatchStats.PlayerStat> playerStats = Lists.newArrayList();
        playerStats.addAll(competitorStatList.get(0).getPlayerStats());
        playerStats.addAll(competitorStatList.get(1).getPlayerStats());
        for (MatchStats.PlayerStat playerStat : playerStats) {
            if (playerStat == null) continue;
            CompetitorSeasonStat currentStat = SbdsInternalApis.getCompetitorSeasonStatByCsidAndCompetitor(csid, playerStat.getPlayerId(), CompetitorType.PLAYER);
            if (currentStat == null) {
                LOG.warn(" the players competitionseason:{}, is not exist result : {}", playerStat.getPlayerId(), true);
                continue;
            }
            Map map = currentStat.getTopStats() == null ? Maps.newHashMap() : currentStat.getTopStats();
            for (String key : Constants.playerBestStatics) {
                if (map.get(key) == null || (playerStat.getStats().get(key) != null && (StringToDouble(map.get(key)).compareTo(
                        StringToDouble(playerStat.getStats().get(key))) < 0))) {
                    map.put(key, playerStat.getStats().get(key));
                }
            }
            currentStat.setTopStats(map);
            SbdsInternalApis.saveCompetitorSeasonStat(currentStat);
            LOG.info("update  the players best result : {}, result : {}", currentStat.getId(), true);
        }
        LOG.info("update all the players in the match : {}, result : {}", matchStats.getId(), true);
        return true;
    }

    private Double StringToDouble(Object value) {
        try {
            Double valueNew = Double.valueOf(value.toString());
            return valueNew;
        } catch (Exception e) {
            return new Double(0.0);
        }
    }

    @Override
    Logger getLogger() {
        return LOG;
    }
}
