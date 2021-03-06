package com.lesports.sms.data.adapter.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.lesports.api.common.CountryCode;
import com.lesports.api.common.LanguageCode;
import com.lesports.qmt.sbd.SbdLiveEventInternalApis;
import com.lesports.qmt.sbd.SbdMatchInternalApis;
import com.lesports.qmt.sbd.SbdPlayerInternalApis;
import com.lesports.qmt.sbd.SbdTeamInternalApis;
import com.lesports.qmt.sbd.model.*;
import com.lesports.query.InternalCriteria;
import com.lesports.query.InternalQuery;
import com.lesports.sms.api.common.TextLiveMessageType;
import com.lesports.sms.client.SopsInternalApis;
import com.lesports.sms.client.TextLiveInternalApis;
import com.lesports.sms.data.adapter.DefualtAdaptor;
import com.lesports.sms.data.model.Constants;
import com.lesports.sms.data.model.commonImpl.DefaultLiveEvent;
import com.lesports.sms.data.process.impl.DefaultLiveEventProcessor;
import com.lesports.sms.data.process.impl.DefaultPBPEventProessor;
import com.lesports.sms.model.Episode;
import com.lesports.sms.model.PBPEvent;
import com.lesports.sms.model.TextLive;
import com.lesports.sms.model.TextLiveMessage;
import org.apache.commons.collections.CollectionUtils;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;

/**
 * Created by qiaohongxin on 2016/5/11.
 */
public abstract class DefultMatchActionAdaptor extends DefualtAdaptor {
    @Resource
    DefaultLiveEventProcessor defaultLiveEventProcessor;
    @Resource
    DefaultPBPEventProessor defaultPBPEventProessor;

    private static Logger LOG = LoggerFactory.getLogger(DefultMatchActionAdaptor.class);

    public Boolean nextProcessor(PartnerType partnerType, Long csid, Object object) {
        try {
            DefaultLiveEvent.LiveModel liveEvent = (DefaultLiveEvent.LiveModel) object;
            Match match = SbdMatchInternalApis.getMatchByPartner(getPartner(liveEvent.getPartnerId(), partnerType));
            if (match == null) return false;
            Episode currentEpisode = SopsInternalApis.getEpisodesByMidAndCountryAndLanguage(match.getId(), CountryCode.CN, LanguageCode.ZH_CN);
            if (null == currentEpisode) {
                LOG.warn("the math is not have the right eposide");
                return null;
            }
            InternalQuery query = new InternalQuery();
            query.addCriteria(new InternalCriteria("eid", "eq", currentEpisode.getId()));
            query.addCriteria(new InternalCriteria("type", "eq", "PLAY_BY_PLAY"));
            query.addCriteria(new InternalCriteria("deleted", "eq", false));
            List<TextLive> lives = TextLiveInternalApis.getTextLivesByQuery(query);
            TextLive currentTextLive = null;
            if (CollectionUtils.isNotEmpty(lives)) currentTextLive = lives.get(0);
            List<DefaultLiveEvent.LiveModel.LiveSingleEvent> convertEvents = processLiveSingleEvent(match.getId(), liveEvent);
            if (CollectionUtils.isNotEmpty(convertEvents)) {
                List<MatchAction> actions = convertModelToMatchAction(convertEvents, match, currentTextLive, partnerType);
                defaultLiveEventProcessor.process(actions);
                if (CollectionUtils.isEmpty(actions)) {
                    LOG.warn("adaptor matchActions fail");
                    return false;
                }
                if (currentTextLive != null) {
                    List<TextLiveMessage> pbpMessages = convertModelToTextLiveMessage(convertEvents, match, currentTextLive, partnerType);
                    if (CollectionUtils.isEmpty(pbpMessages)) {
                        LOG.warn("adaptor pbpLiveEvent fail");
                        return false;
                    }
                    defaultPBPEventProessor.process(pbpMessages);
                }
            }
            return true;
        } catch (Exception e) {
            LOG.error("adaptor matchAction fail", e);
        }
        return false;
    }

    private List<DefaultLiveEvent.LiveModel.LiveSingleEvent> processLiveSingleEvent(Long matchId, DefaultLiveEvent.LiveModel liveEvent) {
        List<DefaultLiveEvent.LiveModel.LiveSingleEvent> matchActionList = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(liveEvent.getCardEvent())) {
            matchActionList.addAll(liveEvent.getCardEvent());
        }
        if (CollectionUtils.isNotEmpty(liveEvent.getGoalEvent())) {
            for (
                    DefaultLiveEvent.LiveModel.LiveSingleEvent event
                    : liveEvent.getGoalEvent()) {
                if (event == null) continue;
                LiveEvent validEvent = SbdLiveEventInternalApis.getLiveEventByName("进球");
                event.setEventType(validEvent == null ? 0L : validEvent.getId());
                matchActionList.add(event);
            }
        }
        if (CollectionUtils.isNotEmpty(liveEvent.getGoalEvent())) {
            for (
                    DefaultLiveEvent.LiveModel.LiveSingleEvent event
                    : liveEvent.getSubstationEvent()) {
                if (event == null) continue;
                LiveEvent validEvent = SbdLiveEventInternalApis.getLiveEventByName("换人");
                event.setEventType(validEvent == null ? 0L : validEvent.getId());
                matchActionList.add(event);
            }
        }
        return matchActionList;
    }

    private List<MatchAction> convertModelToMatchAction(List<DefaultLiveEvent.LiveModel.LiveSingleEvent> actions, Match currentMatch, TextLive currentTextLve, com.lesports.qmt.sbd.model.PartnerType partnerType) {
        if (CollectionUtils.isEmpty(actions)) return null;
        List<MatchAction> matchActionList = Lists.newArrayList();
        for (DefaultLiveEvent.LiveModel.LiveSingleEvent event : actions) {
            if (currentTextLve == null || event.getPartnerId() == null || event.getPartnerId().compareTo(currentTextLve.getLatestPartnerId()) > 0) {
                Player firstPlayer = SbdPlayerInternalApis.getPlayerByPartner(getPartner(event.getMainPlayerId(), partnerType));
                Player secondaryPlayer = SbdPlayerInternalApis.getPlayerByPartner(getPartner(event.getSubOutplayerId(), partnerType));
                Team currentTeam = SbdTeamInternalApis.getTeamByPartner(getPartner(event.getTeamId(), partnerType));
                Long teamId = currentTeam == null ? 0L : currentTeam.getId();
                if (currentTeam == null && event.getTeamType() != null) {
                    for (Match.Competitor currentCometitor : currentMatch.getCompetitors()) {
                        if (currentCometitor.getGround().equals(event.getTeamType())) {
                            teamId = currentCometitor.getCompetitorId();
                        }
                    }
                }
                MatchAction currentMatchAction = new MatchAction();
                currentMatchAction.setMid(currentMatch.getId());
                currentMatchAction.setSection(event.getSectionId());
                currentMatchAction.setPassedTime(event.getTime());
                currentMatchAction.setTid(teamId);
                currentMatchAction.setPid(firstPlayer == null ? 0L : firstPlayer.getId());
                if (secondaryPlayer != null) currentMatchAction.setSubOutPid(secondaryPlayer.getId());
                currentMatchAction.setDetailType(event.getEventDetailType());
                matchActionList.add(currentMatchAction);
            }
        }
        return matchActionList;
    }


    private List<TextLiveMessage> convertModelToTextLiveMessage(List<DefaultLiveEvent.LiveModel.LiveSingleEvent> actions, Match currentMatch,
                                                                TextLive currentTextLve, PartnerType partnerType) {
        if (CollectionUtils.isEmpty(actions)) return null;
        List<TextLiveMessage> textLiveMessages = Lists.newArrayList();
        for (DefaultLiveEvent.LiveModel.LiveSingleEvent event : actions) {
            if (event.getPartnerId().compareTo(currentTextLve.getLatestPartnerId()) > 0) {
                Player firstPlayer = SbdPlayerInternalApis.getPlayerByPartner(getPartner(event.getMainPlayerId(), partnerType));
                Player secondaryPlayer = SbdPlayerInternalApis.getPlayerByPartner(getPartner(event.getSubOutplayerId(), partnerType));
                Player subOnPlayer = SbdPlayerInternalApis.getPlayerByPartner(getPartner(event.getSubOnplayerId(), partnerType));
                if (subOnPlayer != null) {
                    secondaryPlayer = firstPlayer;
                    firstPlayer = subOnPlayer;
                }
                Team currentTeam = SbdTeamInternalApis.getTeamByPartner(getPartner(event.getTeamId(), partnerType));
                PBPEvent mainEvent = null;
                PBPEvent detailEvent = null;
                if (mainEvent == null) continue;
                Long teamId = currentTeam == null ? 0L : currentTeam.getId();
                if (currentTeam == null && event.getTeamType() != null) {
                    for (Match.Competitor currentCometitor : currentMatch.getCompetitors()) {
                        if (currentCometitor.getGround().equals(event.getTeamType())) {
                            teamId = currentCometitor.getCompetitorId();
                        }
                    }
                }
                currentTeam = SbdTeamInternalApis.getTeamById(teamId);
                String mainPlayerName = firstPlayer != null ? firstPlayer.getName() : "";
                String secondPlayerName = secondaryPlayer != null ? secondaryPlayer.getName() : null;
                String teamName = currentTeam != null ? currentTeam.getName() : "";
                String action = mainEvent.getEventContent();
                String detailAction = detailEvent != null ? detailEvent.getEventContent() : null;
                String content = "";
                //begin create the textlive content
                if (secondPlayerName != null && detailAction != null) {
                    content = teamName + " " + action + "[" + detailAction + "]"
                            + " " + secondPlayerName + "->" + mainPlayerName;
                } else if (secondPlayerName != null) {
                    content = teamName + " " + action + " " + secondPlayerName + "->" + mainPlayerName;
                } else if (detailAction != null) {
                    content = teamName + " " + mainPlayerName + " " + action + "[" + detailAction + "]";
                } else {
                    content = teamName + " " + mainPlayerName + " " + action;
                }
                //begin init the textLiveMessage
                TextLiveMessage message = new TextLiveMessage();
                message.setType(TextLiveMessageType.TEXT);
                message.setTextLiveId(currentTextLve.getId());
                message.setEid(currentTextLve.getEid());
                message.setMid(currentTextLve.getMid());
                message.setPartnerId(event.getPartnerId());
                message.setMatchResult(event.getAwayTeamScore() + "-" + event.getHomeTeamScore());
                message.setSection(event.getSectionId());
                message.setTime(event.getTime().toString());
                message.setContent(content);
                textLiveMessages.add(message);
            }

        }
        return textLiveMessages;
    }


}


