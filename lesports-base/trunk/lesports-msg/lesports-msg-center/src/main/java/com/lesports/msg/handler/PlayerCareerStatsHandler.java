package com.lesports.msg.handler;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lesports.api.common.LanguageCode;
import com.lesports.msg.Constants;
import com.lesports.msg.core.LeMessage;
import com.lesports.msg.core.MessageContent;
import com.lesports.query.InternalCriteria;
import com.lesports.query.InternalQuery;
import com.lesports.sms.api.common.CompetitorType;
import com.lesports.sms.api.common.ScopeType;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.model.CompetitorSeasonStat;
import com.lesports.sms.model.PlayerCareerStat;
import com.lesports.sms.model.TeamSeason;
import com.lesports.sms.model.TopList;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

/**
 * User: qiaohongxin
 * Time: 16-09-29 : 下午2:52
 */
@Component
public class PlayerCareerStatsHandler extends AbstractHandler implements IHandler {
    private static final Logger LOG = LoggerFactory.getLogger(PlayerCareerStatsHandler.class);

    @Override
    public void handle(LeMessage message) {
        MessageContent messageContent = message.getContent();
        Long cid = Long.valueOf(messageContent.getFromMsgBody("cid"));
        Long csid = Long.valueOf(messageContent.getFromMsgBody("csid"));
        CompetitorSeasonStat currentCompetitorStats = SbdsInternalApis.getCompetitorSeasonStatById(message.getEntityId());
        if (currentCompetitorStats == null || MapUtils.isEmpty(currentCompetitorStats.getTopStats())) return;
        PlayerCareerStat currentPlayerStat = SbdsInternalApis.getPlayerCareerStatByPidAndCid(currentCompetitorStats.getCompetitorId(), cid);
        if (currentPlayerStat == null) {
            currentPlayerStat = new PlayerCareerStat();
            currentPlayerStat.setPlayerId(currentCompetitorStats.getCompetitorId());
            currentPlayerStat.setCid(cid);
            currentPlayerStat.setAllowCountries(SbdsInternalApis.getCompetitionById(cid).getAllowCountries());
            currentPlayerStat.setDeleted(false);
            currentPlayerStat.setStats(new HashMap<String, Object>());
        }

        for (String key : Constants.playerBestStatics) {
            Object oldValue = currentPlayerStat.getStats().get(key);
            Object newValue = currentCompetitorStats.getTopStats().get(key);

            if (newValue == null) continue;
            if (oldValue == null || StringToDouble(newValue).compareTo(StringToDouble(oldValue)) > 0)
                currentPlayerStat.getStats().put(key, newValue);
        }
        if (SbdsInternalApis.savePlayerCareerStat(currentPlayerStat) > 0) {
            getLogger().info("update the player,playerId:{},playerCareerStat:{}and result:{}", currentPlayerStat.getPlayerId(), currentPlayerStat, true);
        }
    }

    private Double StringToDouble(Object value) {
        try {
            String value1 = value.toString();
            if (!StringUtils.isEmpty(value1) && value1.startsWith(".")) value1 = "0" + value1;
            else if (!StringUtils.isEmpty(value1) && value1.contains("%")) value1 = value1.replace("%", "");
            Double valueNew = Double.valueOf(value1);
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
