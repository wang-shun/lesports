package com.lesports.sms.data.process.impl;

import com.google.common.collect.Lists;
import com.lesports.id.api.IdType;
import com.lesports.msg.core.LeMessage;
import com.lesports.msg.core.LeMessageBuilder;
import com.lesports.msg.core.MessageContent;
import com.lesports.sms.api.common.CompetitorType;
import com.lesports.sms.api.common.ScopeType;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.data.process.BeanProcessor;
import com.lesports.sms.model.CompetitorSeasonStat;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by qiaohongxin on 2016/5/11.
 */
@Service("competitorSeasonStatsProcessor")
public class DefaultCompetitorStatsProcessor extends BeanProcessor {
    private static Logger LOG = org.slf4j.LoggerFactory.getLogger(DefaultCompetitorStatsProcessor.class);

    public Boolean process(Object object) {
        CompetitorSeasonStat newStats = (CompetitorSeasonStat) object;
        try {
            CompetitorSeasonStat competitorSeasonStat = SbdsInternalApis.getCompetitorSeasonStatByCsidAndCompetitor(newStats.getCsid(), newStats.getCompetitorId(), CompetitorType.PLAYER);
            if (competitorSeasonStat == null) {
                SbdsInternalApis.saveCompetitorSeasonStat(newStats);
                return true;
            } else {
                List<String> configs = getUpdateProperty();
                if (CollectionUtils.isEmpty(configs)) {
                    LOG.warn("NO pemission to Update property");
                    return false;
                }
                competitorSeasonStat = (CompetitorSeasonStat) updateObject(configs, competitorSeasonStat, newStats);
                if (SbdsInternalApis.saveCompetitorSeasonStat(competitorSeasonStat) > 0) {
                    if (competitorSeasonStat.getType().equals(CompetitorType.PLAYER)) {
                        sendPlayerStatsUpdateMessage(competitorSeasonStat.getTid(), competitorSeasonStat.getCid(), competitorSeasonStat.getCsid(), competitorSeasonStat.getId());
                    } else {
                        sendTeamStatsUpdateMessage(competitorSeasonStat.getCid(), competitorSeasonStat.getCsid());
                    }
                    LOG.warn("Player Season Stats:{} is updated", competitorSeasonStat.getCompetitorId());

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
        properties.add("avgStats");
        properties.add("stats");
       // properties.add("topStats");
        return properties;
    }

    private Boolean sendPlayerStatsUpdateMessage(Long tid, Long cid, Long csid, Long cidStatsId) {

        MessageContent messageContent = new MessageContent();
        messageContent.addToMsgBody("cid", cid.toString()).addToMsgBody("csid", csid.toString());
        LeMessage message = LeMessageBuilder.create().setEntityId(cidStatsId)
                .setIdType(IdType.PLAYER_CAREER_STAT).setContent(messageContent).build();
        sendMessage(message);

        MessageContent messageContent2 = new MessageContent();
        messageContent2.addToMsgBody("scope", tid.toString()).addToMsgBody("competitorType", CompetitorType.PLAYER.toString()).addToMsgBody("scopeType", ScopeType.TEAM.toString()).addToMsgBody("cid", cid.toString()).addToMsgBody("csid", csid.toString());
        LeMessage message2 = LeMessageBuilder.create().setEntityId(tid)
                .setIdType(IdType.TOP_LIST).setContent(messageContent).build();
        sendMessage(message);
        LOG.info("the current players and csid is updated");
        return true;

    }

    private Boolean sendTeamStatsUpdateMessage(Long cid, Long csid) {
        Long scopeId = 100009000L;
        MessageContent messageContent = new MessageContent();
        messageContent.addToMsgBody("scope", scopeId.toString()).addToMsgBody("competitorType", CompetitorType.TEAM.toString()).addToMsgBody("scopeType", ScopeType.CONFERENCE.toString()).addToMsgBody("cid", cid.toString()).addToMsgBody("csid", csid.toString());
        LeMessage message = LeMessageBuilder.create().setEntityId(scopeId)
                .setIdType(IdType.TOP_LIST).setContent(messageContent).build();
        return sendMessage(message);
    }
}
