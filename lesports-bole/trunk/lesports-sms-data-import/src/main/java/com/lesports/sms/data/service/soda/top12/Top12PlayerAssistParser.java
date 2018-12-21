package com.lesports.sms.data.service.soda.top12;

import com.lesports.query.InternalCriteria;
import com.lesports.query.InternalQuery;
import com.lesports.sms.api.common.CompetitorType;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.data.model.Constants;
import com.lesports.sms.data.model.PlayerAssistsStatistic;
import com.lesports.sms.data.service.IThirdDataParser;
import com.lesports.sms.data.service.Parser;
import com.lesports.sms.data.util.CommonUtil;
import com.lesports.sms.model.*;
import org.apache.commons.collections.CollectionUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by zhonglin on 2016/8/2.
 */
@Service("top12PlayerAssistParser")
public class Top12PlayerAssistParser extends Parser implements IThirdDataParser {
    private static Logger logger = LoggerFactory.getLogger(Top12PlayerGoalParser.class);

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
            Element rootElmement = document.getRootElement();

            // 赛事基本信息，赛事名称、当前赛季、当前轮次
            Element competitionInfo = rootElmement.element("Competition");
            String competitionId = competitionInfo.attributeValue("id");
            String competitionName = competitionInfo.attributeValue("name");

            Competition competition = SbdsInternalApis.getCompetitionById(Constants.sodaSMSCompetittionMap.get(competitionId));
            if (null == competition) {
                logger.warn("can not find relative event,name:{}", competitionName);
                return result;
            }
            String currentSeason = competitionInfo.attributeValue("season");
            long cid = competition.getId();
            CompetitionSeason competitionSeason = SbdsInternalApis.getLatestCompetitionSeasonByCid(cid);

            if (competitionSeason == null) {
                logger.warn("can not find relative event,name:{},season:{}", competitionName, currentSeason);
                return result;
            }
            long csid = competitionSeason.getId();
            List<DictEntry> dictEntryList = SbdsInternalApis.getDictEntriesByName("^足球$");
            if (CollectionUtils.isEmpty(dictEntryList)) {
                logger.warn("can not find gameFType,name:足球");
                return result;
            }
            Long gameFType = dictEntryList.get(0).getId();

            Long type = 0L;
            List<DictEntry> dictEntries = SbdsInternalApis.getDictEntriesByName("^助攻榜$");
            if (CollectionUtils.isNotEmpty(dictEntries)) {
                type = dictEntries.get(0).getId();
            }

            TopList topList = new TopList();
            List<TopList> topLists = new ArrayList<>();
            topLists = SbdsInternalApis.getTopListsByCsidAndType(csid, type);
            if (CollectionUtils.isNotEmpty(topLists)) {
                topList = topLists.get(0);
            } else {
                topList.setDeleted(false);
                topList.setCid(cid);
                topList.setCsid(csid);
                topList.setLatest(true);
                topList.setType(type);
                topList.setAllowCountries(getAllowCountries());
                topList.setOnlineLanguages(getOnlineLanguage());
            }
            //球队排名信息
            Element assists = competitionInfo.element("Assist");
            Iterator<Element> assistIte = assists.elementIterator("Player");
            List<TopList.TopListItem> topListItems = new ArrayList<>();
            int order = 0;
            while (assistIte.hasNext()) {
                order++;
                Element assistElement = assistIte.next();
                String playerName = assistElement.attributeValue("name");
                String playerId = assistElement.attributeValue("id");
                String teamName = assistElement.attributeValue("clubName");
                String teamId = assistElement.attributeValue("clubId");
                String total = assistElement.attributeValue("assist");
//                String caps = assistElement.attributeValue("caps");
//                String counName = assistElement.attributeValue("counName");


                Player player = SbdsInternalApis.getPlayerBySodaId(playerId);

                if (null == player) {
                    InternalQuery internalQuery = new InternalQuery();
                    internalQuery.addCriteria(InternalCriteria.where("deleted").is(false));
                    internalQuery.addCriteria(InternalCriteria.where("name").regex("^" + playerName + "$"));
                    internalQuery.addCriteria(InternalCriteria.where("partner_id").exists(true));

                    List<Player> players = SbdsInternalApis.getPlayersByQuery(internalQuery);
                    if (CollectionUtils.isNotEmpty(players)) {
                        player = players.get(0);
                    }
                    if (null == player) {
                        logger.warn("cannot find player,sodaId:{}", playerId);
                        player = new Player();
                        player.setSodaId(playerId);
                        player.setDeleted(false);
                        player.setName(playerName);
                        player.setMultiLangNames(getMultiLang(playerName));
//                        player.setNationality(counName);
//                        player.setMultiLangNationalities(getMultiLang(counName));
                        player.setGameFType(gameFType);
                        player.addCids(cid);
                        player.setAllowCountries(getAllowCountries());
                        player.setOnlineLanguages(getOnlineLanguage());
                    } else {
                        player.setSodaId(playerId);
                    }
                    Long id = SbdsInternalApis.savePlayer(player);
                    player.setId(id);
                    logger.info("insert into player sucess,sodaId:" + player);
                }
                Team team = SbdsInternalApis.getTeamBySodaId(teamId);
                if (team == null) {
                    logger.warn("cannot find team,sodaId:{}", teamId);
                    continue;
                }
                TopList.TopListItem topListItem = new TopList.TopListItem();
                topListItem.setCompetitorId(player.getId());
                topListItem.setTeamId(team.getId());
                topListItem.setCompetitorType(CompetitorType.PLAYER);
                topListItem.setRank(order);
                topListItem.setShowOrder(order);
                PlayerAssistsStatistic playerAssistsStatistic = new PlayerAssistsStatistic();
                playerAssistsStatistic.setAssists(Integer.parseInt(total));
//                playerAssistsStatistic.setCaps(Integer.parseInt(caps));
                Map<String, Object> map = CommonUtil.convertBeanToMap(playerAssistsStatistic);
                topListItem.setStats(map);
                topListItems.add(topListItem);

            }
            topList.setItems(topListItems);
            SbdsInternalApis.saveTopList(topList);
            logger.info("insert toplist success,cId:{},csId:{}", topList.getCid(), topList.getCsid());
            result = true;
        } catch (Exception e) {
            logger.error("insert into toplist  error: ", e);
        }
        return result;
    }
}
