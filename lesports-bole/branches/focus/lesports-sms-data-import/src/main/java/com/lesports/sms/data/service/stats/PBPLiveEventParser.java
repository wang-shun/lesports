package com.lesports.sms.data.service.stats;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.lesports.api.common.CountryCode;
import com.lesports.api.common.LanguageCode;
import com.lesports.query.InternalCriteria;
import com.lesports.query.InternalQuery;
import com.lesports.sms.api.common.LiveStatus;
import com.lesports.sms.api.common.TextLiveMessageType;
import com.lesports.sms.api.common.TextLiveType;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.client.SopsInternalApis;
import com.lesports.sms.client.TextLiveInternalApis;
import com.lesports.sms.data.model.Constants;
import com.lesports.sms.data.service.IThirdDataParser;
import com.lesports.sms.model.*;
import org.apache.commons.collections.CollectionUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by qiaohongxin on 2015/9/23.
 */

@Service
public class PBPLiveEventParser implements IThirdDataParser {

    private static Logger logger = LoggerFactory.getLogger(PBPLiveEventParser.class);

    private Map<String, LiveStatus> stateMap = ImmutableMap.of("1", LiveStatus.LIVE_NOT_START, "2", LiveStatus.LIVE_NOT_START, "4", LiveStatus.LIVE_END);

    @Override
    public Boolean parseData(String file) {
        Boolean result = false;
        try {
            File xmlFile = new File(file);
            if (!xmlFile.exists()) {
                logger.warn("parsing the file:{} not exists", file);
                return result;
            }
            SAXReader reader = new SAXReader();
            Document document = reader.read(xmlFile);
            Element sportsScores = document.getRootElement().element("sports-scores");
            String name = sportsScores.element("league").attributeValue("alias");
            String season = sportsScores.element("season").attributeValue("season");
            Long gameFTypeId = SbdsInternalApis.getDictEntriesByName("篮球$").get(0).getId();
            List<Competition> competitions = SbdsInternalApis.getCompetitionByNameAndGameFType("^" + name, gameFTypeId);
            if (competitions == null || competitions.isEmpty()) {
                logger.warn("can not find relative competions,name is" + name);
                return result;
            }
            CompetitionSeason competitionseason = SbdsInternalApis.getLatestCompetitionSeasonByCid(competitions.get(0).getId());
            if (competitionseason == null) {
                logger.warn("can not find the right time competions,name is" + name + "season is" + season);
                return result;
            }
            Long csid = competitionseason.getId();
            Element pBpElement = sportsScores.element("nba-scores").element("nba-playbyplay");
            String matchId = pBpElement.element("gamecode").attributeValue("global-id");
            String matchStatus = pBpElement.element("gamestate").attributeValue("status-id");
            Match currentMatch = SbdsInternalApis.getMatchByPartnerIdAndType(matchId, Constants.PartnerSourceStaticId);
            if (null == currentMatch) {
                logger.warn("match is not exits");
                return result;
            }
            Long mid = currentMatch.getId();
            Iterator<Element> playEvents = pBpElement.elementIterator("play");
            Episode currentEpisode = SopsInternalApis.getEpisodesByMidAndCountryAndLanguage(mid, CountryCode.CN, LanguageCode.ZH_CN);
            if (null == currentEpisode) {
                logger.warn("the math is not have the right eposide");
                return result;
            }
            InternalQuery query = new InternalQuery();
            query.addCriteria(new InternalCriteria("eid", "eq", currentEpisode.getId()));
            query.addCriteria(new InternalCriteria("type", "eq", "PLAY_BY_PLAY"));
            query.addCriteria(new InternalCriteria("deleted", "eq", false));
            TextLive textLive = null;
            List<TextLive> lives = TextLiveInternalApis.getTextLivesByQuery(query);
            if (CollectionUtils.isNotEmpty(lives)) {
                textLive = lives.get(0);
            } else {
                textLive = new TextLive();
                LiveStatus status = LiveStatus.LIVE_NOT_START;
                if (null != stateMap.get(matchStatus)) {
                    status = stateMap.get(matchStatus);
                }
                textLive.setDeleted(false);
                textLive.setEid(currentEpisode.getId());
                textLive.setMid(mid);
                textLive.setStatus(status);
                textLive.setType(TextLiveType.PLAY_BY_PLAY);
                textLive.setLatestPartnerId("-1");
                textLive.setDeleted(false);
                Long id = TextLiveInternalApis.saveTextLive(textLive);
                textLive.setId(id);
            }
            Element lastEvent = null;
            while (playEvents.hasNext()) {
                lastEvent = playEvents.next();
                String eventId = lastEvent.attributeValue("id");
                if (Double.parseDouble(eventId) <= Double.parseDouble(textLive.getLatestPartnerId()))
                    continue;
                TextLiveMessage message = createMessage(textLive.getId(), currentEpisode.getId(), mid, lastEvent);
                try {
                    TextLiveInternalApis.saveTextLiveMessage(message);
                } catch (Exception e) {
                    logger.error("text live message is saved failed. eid:{}, ", currentEpisode.getId(), e);
                    continue;
                }
                logger.info("text live message is saved sucessfully");
            }
            if (null != lastEvent) {
                textLive.setLatestPartnerId(lastEvent.attributeValue("id"));
                TextLiveInternalApis.saveTextLive(textLive);
                result = true;
                logger.info("text live is updated sucessfully");
            }
        } catch (Exception e) {
            logger.error("pbb event document is parser fail:", e);
        }
        return result;
    }

    private TextLiveMessage createMessage(Long textId, Long eid, Long mid, Element event) {
        TextLiveMessage message = new TextLiveMessage();
        //解析xml中的event节点
        String playerId = event.attributeValue("global-player-id-1");//球员id
        String teamId = event.attributeValue("global-team-code-1");//球队id
        String quarter = event.attributeValue("quarter");//阶段
        String timeSecond = event.attributeValue("time-seconds");
        String timeMinutes = event.attributeValue("time-minutes");
        String eventId = event.attributeValue("event-id");//事件Id
        String detailId = event.attributeValue("detail-id");//事件详情的Id，事件+详情Id可获取详情内容
        String homescore = event.attributeValue("home-score");
        String visitscore = event.attributeValue("visitor-score");
        //产生所需的属性值
        String teamName = "";
        String playerName = "";
        String eventDetail = "";
        String eventContent = "";
        if (!Strings.isNullOrEmpty(teamId)) {
            Team team = SbdsInternalApis.getTeamByPartnerIdAndPartnerType(teamId, Constants.PartnerSourceStaticId);
            if (null == team)
                logger.warn("the id is" + teamId + "is not exist in the teamDatabase");
            else
                teamName = team.getName();
        }
        if (!Strings.isNullOrEmpty(playerId)) {
            Player player = SbdsInternalApis.getPlayerByPartnerIdAndPartnerType(playerId, Constants.PartnerSourceStaticId);
            if (null == player)
                logger.warn("the id is" + playerId + "is not exist in the playerDatabase");
            else
                playerName = player.getName();
        }
        if (!Strings.isNullOrEmpty(detailId)) {
            PBPEvent pbpEvent = SbdsInternalApis.getPBPEventByDetailIdAndPartnerType(eventId, detailId, Constants.PartnerSourceStaticId);
            if (null != pbpEvent)
                eventDetail = pbpEvent.getEventContent();
            else
                logger.warn("the event:eventId and eventDetailId are " + eventId + "" + detailId + " not exist in the eventDatabase");

        }
        if (!Strings.isNullOrEmpty(eventId)) {
            PBPEvent pbpEvent = SbdsInternalApis.getPBPEventByeventIdAndPartnerType(eventId, Constants.PartnerSourceStaticId);
            if (null == pbpEvent)
                logger.warn("the event:eventId is" + eventId + "not exist in the eventDatabase");
            else {
                eventContent = pbpEvent.getEventContent();
            }
        }
        Long section = 0L;
        if (!Strings.isNullOrEmpty(quarter)) {
            String sectionName = "第1节$";
            if (Integer.parseInt(quarter) > 4) {
                int addtime = Integer.parseInt(quarter) - 4;
                sectionName = "加时" + addtime + "$";
            } else {
                sectionName = "第" + quarter + "节$";
            }
            List<DictEntry> entries = SbdsInternalApis.getDictEntriesByName(sectionName);
            if (null != entries && !entries.isEmpty()) {
                section = entries.get(0).getId();
            }
        }
        String restTime = String.valueOf(15.0 * 60);
        try {
            restTime = String.valueOf(Double.parseDouble(timeMinutes) * 60.0 + Double.parseDouble(timeSecond));
        } catch (Exception e) {
            logger.error("the time can not be parsed to double", e);
        }
        message.setDeleted(false);
        message.setType(TextLiveMessageType.TEXT);
        message.setTextLiveId(textId);
        message.setEid(eid);
        message.setMid(mid);
        message.setPartnerId(eventId);
        message.setPartnerType(Constants.PartnerSourceStaticId);
        message.setContent(teamName + "" + playerName + "" + eventDetail + "" + eventContent);
        message.setMatchResult(visitscore + "-" + homescore);
        message.setSection(section);
        message.setTime(restTime);
        return message;
    }
}