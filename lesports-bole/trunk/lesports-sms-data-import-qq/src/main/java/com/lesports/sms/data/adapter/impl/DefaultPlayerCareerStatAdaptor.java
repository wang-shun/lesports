package com.lesports.sms.data.adapter.impl;

import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.data.adapter.DefualtAdaptor;
import com.lesports.sms.data.model.TransModel;
import com.lesports.sms.data.model.commonImpl.DefaultPlayerCareerStats;
import com.lesports.sms.data.model.commonImpl.DefaultPlayerStats;
import com.lesports.sms.data.process.impl.DefaultCareerStatsProcessor;
import com.lesports.sms.model.Player;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by qiaohongxin on 2016/5/11.
 */
@Service("playerCareerStatAdaptor")
public class DefaultPlayerCareerStatAdaptor extends DefualtAdaptor {
    private static Logger LOG = LoggerFactory.getLogger(DefaultPlayerCareerStatAdaptor.class);
    @Resource
    DefaultCareerStatsProcessor defaultCareerStatsProcessor;

    public Boolean nextProcessor(TransModel transModel, Object object) {
        DefaultPlayerCareerStats.PlayerCareerStats playerStat = (DefaultPlayerCareerStats.PlayerCareerStats) object;
        Player player = SbdsInternalApis.getPlayerByQQId(playerStat.getPartnerId());
        if (player == null) return false;
//        if (CollectionUtils.isNotEmpty(playerStat.g())) {
//            if (currentScopeType == null) {
//                currentScopeType = CareerScopeType.CLUB_TEAM;
//                scopeId = 0L;
//            }
//            for (DefaultPlayerCareerStats.PlayerCareerStats.CareerStatT statT : playerStat.getClubStats()) {
//                PlayerCareerStat stat = convertModelToCareerStats(player.getId(), currentScopeType, scopeId, playerStat.getStatTypeType(), statT);
//                defaultCareerStatsProcessor.process(stat);
//            }
//        }
//        if (CollectionUtils.isNotEmpty(playerStat.getNationalStats())) {
//            if (currentScopeType == null) {
//                currentScopeType = CareerScopeType.NATIONAL_TEAM;
//                scopeId = 0L;
//            }
//            for (DefaultPlayerCareerStats.PlayerCareerStats.CareerStatT statT : playerStat.getNationalStats()) {
//                PlayerCareerStat stat = convertModelToCareerStats(player.getId(), currentScopeType, scopeId, playerStat.getStatTypeType(), statT);
//                defaultCareerStatsProcessor.process(stat);
//            }
//        }
        return true;
    }

//    private PlayerCareerStat convertModelToCareerStats(Long pid, CareerScopeType type, Long id, CareerStatType statsType, DefaultPlayerCareerStats.PlayerCareerStats.CareerStatT careerStatT) {
//        PlayerCareerStat stat = new PlayerCareerStat();
//        stat.setPlayerId(pid);
//        stat.setScopeType(type);
//        stat.setScopeId(id);
//        stat.setStatType(statsType == null ? CareerStatType.TOTAL : statsType);
//        stat.setStats(careerStatT.getElementStats());
//        return stat;
//    }

}