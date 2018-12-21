package com.lesports.sms.data.service.stats;

import com.google.common.collect.Lists;
import com.lesports.query.InternalCriteria;
import com.lesports.query.InternalQuery;
import com.lesports.sms.api.common.CompetitorType;
import com.lesports.sms.api.common.ScopeType;
import com.lesports.sms.api.common.TeamType;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.data.model.Constants;
import com.lesports.sms.data.model.NBAFullPlayerStandingContent;
import com.lesports.sms.data.model.PlayerPageContent;
import com.lesports.sms.data.model.StatConstats;
import com.lesports.sms.data.service.IThirdDataParser;
import com.lesports.sms.data.service.soda.SodaAssistParser;
import com.lesports.sms.data.util.CommonUtil;
import com.lesports.sms.model.*;
import com.lesports.utils.xml.ParserFactory;
import org.apache.commons.collections.CollectionUtils;
import org.jdom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;


/**
 * Created by qiaohongxin on 2016/8/17.
 */
@Service("NBAFullStandingParser")
public class NBAFullPlayerStandingParser extends com.lesports.sms.data.service.Parser implements IThirdDataParser {
    private static Logger LOG = LoggerFactory.getLogger(SodaAssistParser.class);

    @Override
    public Boolean parseData(String file) {
        Boolean result = false;
        try {
            File xmlFile = new File(file);
            if (!xmlFile.exists()) {
                LOG.warn("parsing the file:{} not exists", file);
                return result;
            }
            FileInputStream fileInputStream = new FileInputStream(xmlFile);
            com.lesports.utils.xml.Parser<NBAFullPlayerStandingContent> parserClass = new ParserFactory().createXmlParser(NBAFullPlayerStandingContent.class);
            NBAFullPlayerStandingContent fullPlayerStandingContent = parserClass.parse(fileInputStream);
            if (fullPlayerStandingContent == null) {
                LOG.warn("the file is empty", file);
                return false;
            }
            Long gameFTypeId = SbdsInternalApis.getDictEntriesByName("篮球$").get(0).getId();
            List<Competition> competitions = SbdsInternalApis.getCompetitionByNameAndGameFType("^" + fullPlayerStandingContent.getcName(), gameFTypeId);
            if (CollectionUtils.isEmpty(competitions)) {
                LOG.warn("the competition  is empty");
                return false;
            }
            CompetitionSeason competitionSeason = SbdsInternalApis.getCompetitionSeasonByCidAndSeason(competitions.get(0).getId(), fullPlayerStandingContent.getSeason());
            if (null == competitionSeason) {
                LOG.warn("the competitionSeason  is empty");
                return false;
            }
            if (CollectionUtils.isEmpty(fullPlayerStandingContent.getRankingItems())) {
                LOG.warn("the teamElement  is empty");
                return false;
            }
            for (NBAFullPlayerStandingContent.rankingItem currentItem : fullPlayerStandingContent.getRankingItems()) {
                Long rankingType = Constants.rankingTypeId.get(currentItem.getCategory());
                if (rankingType == null || CollectionUtils.isEmpty(currentItem.getPlayerRankingItems())) continue;
                TopList playerTopList = getTopList(competitions.get(0).getId(), competitionSeason.getId(), rankingType);
                List<TopList.TopListItem> playerTopListItems = new ArrayList<TopList.TopListItem>();
                for (NBAFullPlayerStandingContent.PlayerRankingItem playerRankingItem : currentItem.getPlayerRankingItems()) {
                    Player player = SbdsInternalApis.getPlayerByPartnerIdAndPartnerType(playerRankingItem.getPlayerId(), Constants.PartnerSourceStaticId);
                    if (player == null) {
                        LOG.warn("cannot find team,teamPartnerId:{}", playerRankingItem.getPlayerId());
                        continue;
                    }
                    Team team = SbdsInternalApis.getTeamByPartnerIdAndPartnerType(playerRankingItem.getTeamId(), Constants.PartnerSourceStaticId);
                    if (team == null) {
                        LOG.warn("cannot find team,teamPartnerId:{}", playerRankingItem.getTeamId());
                        continue;
                    }
                    TopList.TopListItem playerTopListItem = new TopList.TopListItem();
                    Map<String, Object> map = new HashMap<>();
                    String statsValue = playerRankingItem.getStats();
                    if (currentItem.getCategory().contains("%")) {
                        map.put(currentItem.getCategory(), CommonUtil.getPercentFormat(statsValue));
                    } else {
                        map.put(currentItem.getCategory(), CommonUtil.parseDouble(statsValue, 0.0));
                    }
                    playerTopListItem.setStats(map);
                    playerTopListItem.setCompetitorId(player.getId());
                    playerTopListItem.setCompetitorType(CompetitorType.PLAYER);
                    playerTopListItem.setTeamId(team.getId());
                    playerTopListItem.setRank(playerRankingItem.getRanking());
                    playerTopListItem.setShowOrder(playerRankingItem.getRanking());
                    playerTopListItems.add(playerTopListItem);
                }
                playerTopList.setItems(playerTopListItems);
                SbdsInternalApis.saveTopList(playerTopList);
                LOG.info("Player Ranking  order by " + currentItem.getCategory() + "is created sucessfully");
            }
            return true;
        } catch (Exception e) {
            LOG.debug("player ranking is parse fail", e);
        }
        return result;
    }

    private TopList getTopList(Long cid, Long csid, Long rankingtypeId) {
        TopList topList = new TopList();
        Long scope = 100009000L;
        InternalQuery query = new InternalQuery();
        query.addCriteria(new InternalCriteria("deleted", "eq", false));
        query.addCriteria(new InternalCriteria("csid", "eq", csid));
        query.addCriteria(new InternalCriteria("type", "eq", rankingtypeId));
        query.addCriteria(new InternalCriteria("scope", "eq", scope));
        query.addCriteria(new InternalCriteria("competitor_type", "eq", CompetitorType.PLAYER));
        List<TopList> topLists = SbdsInternalApis.getTopListsByQuery(query);
        if (CollectionUtils.isNotEmpty(topLists)) {
            topList = topLists.get(0);
        } else {
            topList.setDeleted(false);
            topList.setCid(cid);
            topList.setCsid(csid);
            topList.setLatest(true);
            topList.setType(rankingtypeId);
            topList.setAllowCountries(getAllowCountries());
            topList.setOnlineLanguages(getOnlineLanguage());
            topList.setCompetitorType(CompetitorType.PLAYER);
            topList.setScopType(ScopeType.CONFERENCE);
            topList.setScope(scope);
        }

        return topList;
    }


    private TopList.TopListItem getTopListItem(String standingName, org.dom4j.Element ranking) {
        String PlayerPartnerId = ranking.element("player-id").attributeValue("global-id");//ranking.attributeValue("global-id");
        Player player = SbdsInternalApis.getPlayerByPartnerIdAndPartnerType(PlayerPartnerId, Constants.PartnerSourceStaticId);
        if (player == null) {
            LOG.warn("cannot find team,teamPartnerId:{}", PlayerPartnerId);
            return null;
        }
        String teamPartnerId = ranking.element("team-code").attributeValue("global-id");// ranking.attributeValue("global-team-id");
        Team team = SbdsInternalApis.getTeamByPartnerIdAndPartnerType(teamPartnerId, Constants.PartnerSourceStaticId);
        if (team == null) {
            LOG.warn("cannot find team,teamPartnerId:{}", teamPartnerId);
            return null;
        }
        TopList.TopListItem playerTopListItem = new TopList.TopListItem();
        Map<String, Object> map = new HashMap<>();
        map.put(standingName, CommonUtil.parseDouble(ranking.element("stat").attributeValue("stat"), 0.0));
        playerTopListItem.setStats(map);
        playerTopListItem.setCompetitorId(player.getId());
        playerTopListItem.setCompetitorType(CompetitorType.PLAYER);
        playerTopListItem.setTeamId(team.getId());
        playerTopListItem.setRank(CommonUtil.parseInt(ranking.element("ranking").attributeValue("ranking"), 0));
        playerTopListItem.setShowOrder(CommonUtil.parseInt(ranking.element("ranking").attributeValue("ranking"), 0));
        LOG.info("add  team information to conference list");
        return playerTopListItem;


    }

}
