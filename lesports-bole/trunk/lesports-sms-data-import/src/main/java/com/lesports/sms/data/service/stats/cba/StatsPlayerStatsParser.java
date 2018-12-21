package com.lesports.sms.data.service.stats.cba;

import com.google.common.collect.ImmutableMap;
import com.lesports.id.api.IdType;
import com.lesports.msg.core.LeMessage;
import com.lesports.msg.core.LeMessageBuilder;
import com.lesports.msg.core.MessageContent;
import com.lesports.sms.api.common.CompetitorType;
import com.lesports.sms.api.common.ScopeType;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.data.model.Constants;
import com.lesports.sms.data.model.StatConstats;
import com.lesports.sms.data.service.IThirdDataParser;
import com.lesports.sms.data.util.CommonUtil;
import com.lesports.sms.model.*;
import org.apache.commons.collections.CollectionUtils;
import org.dom4j.Document;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Created by qiaohongxin on 2016/8/17.
 */
@Service("StatsPlayerStatsParser")
public class StatsPlayerStatsParser extends com.lesports.sms.data.service.Parser implements IThirdDataParser {
    private static Logger LOG = LoggerFactory.getLogger(StatsPlayerStatsParser.class);
    private Map<String, String> elementIdentifyMap = ImmutableMap.of("NBA", "nba", "CBACHN", "bk");


    @Override
    public Boolean parseData(String file) {
        Boolean result = false;
        try {
            SAXReader reader = new SAXReader();
            FileInputStream fileInputStream = new FileInputStream(file);
            Document document = reader.read(fileInputStream);
            org.dom4j.Element rootElement = document.getRootElement();
            String cName = CommonUtil.getStringValue(rootElement.selectObject("/sports-statistics/sports-rankings/league/@alias"));
            String elementIdentify = elementIdentifyMap.get(cName);
            String season = CommonUtil.getStringValue(rootElement.selectObject("/sports-statistics/sports-rankings/season/@season"));
            String stageId = CommonUtil.getStringValue(rootElement.selectObject("/sports-statistics/sports-rankings/gametype/@id"));
            List<org.dom4j.Element> stats = rootElement.selectNodes("/sports-statistics/sports-rankings/" + elementIdentify + "-player-splits");
            if (CollectionUtils.isEmpty(stats)) {
                LOG.warn("the file is empty", file);
                return false;
            }
            Long cid = Constants.statsNameMap.get(cName);
            CompetitionSeason competitionSeason = SbdsInternalApis.getCompetitionSeasonByCidAndSeason(cid, season);
            if (null == competitionSeason) {
                LOG.warn("the competitionSeason  is empty");
                return false;
            }
            Long stage = 0L;
            String stageName = Constants.nbaGameType.get(stageId);
            List<DictEntry> stageList = SbdsInternalApis.getDictEntriesByName(stageName);
            if (CollectionUtils.isNotEmpty(stageList)) {
                stage = stageList.get(0).getId();
            }

            Set<Long> teamIds = new HashSet<Long>();
            for (org.dom4j.Element element : stats) {
                String teamId = CommonUtil.getStringValue(element.selectObject("./bk-player-split/team-code/@global-id"));
                String playerId = CommonUtil.getStringValue(element.selectObject("./player-code/@global-id"));
                Team team = SbdsInternalApis.getTeamByStatsId(teamId);
                if (team != null) teamIds.add(team.getId());
                Player player = SbdsInternalApis.getPlayerByPartnerIdAndPartnerType(playerId, Constants.PartnerSourceStaticId);
                if (player == null) continue;
                CompetitorSeasonStat cidStat = SbdsInternalApis.getCompetitorSeasonStatByCsidAndCompetitor(competitionSeason.getId(), player.getId(), CompetitorType.PLAYER);
                if (cidStat == null) {
                    cidStat = new CompetitorSeasonStat();
                    cidStat.setCid(cid);
                    cidStat.setCsid(competitionSeason.getId());
                    cidStat.setAllowCountries(SbdsInternalApis.getCompetitionById(cid).getAllowCountries());
                    cidStat.setCompetitorId(player.getId());
                    cidStat.setType(CompetitorType.PLAYER);
                    cidStat.setStage(stage);
                }
                org.dom4j.Element currentElement = element.element(elementIdentify + "-player-split");
                cidStat.setStats(getNBAStats(currentElement, StatConstats.NBAPlayerTotalStatPath));
                cidStat.setAvgStats(getNBAStats(currentElement, StatConstats.NBAPlayerAVGStatPath));
                cidStat.setTopStats(getNBAStats(currentElement, StatConstats.NBAPlayerTopStatPath));
                cidStat.setTid(team.getId());
                Long cidStatsId = SbdsInternalApis.saveCompetitorSeasonStat(cidStat);
                if (cidStatsId > 0) {
                    LOG.info("the current player:{} currentCsid ：{} and Stat:{} is updated", player.getId(), competitionSeason.getId(), cidStat.toString());
                    MessageContent messageContent = new MessageContent();
                    messageContent.addToMsgBody("cid", competitionSeason.getCid().toString()).addToMsgBody("csid", competitionSeason.getId().toString());
                    LeMessage message = LeMessageBuilder.create().setEntityId(cidStatsId)
                            .setIdType(IdType.PLAYER_CAREER_STAT).setContent(messageContent).build();
                    sendMessage(message);
                }
            }
            for (Long teamId : teamIds) {
                MessageContent messageContent = new MessageContent();
                messageContent.addToMsgBody("scope", teamId.toString()).addToMsgBody("competitorType", CompetitorType.PLAYER.toString()).addToMsgBody("scopeType", ScopeType.TEAM.toString()).addToMsgBody("cid", competitionSeason.getCid().toString()).addToMsgBody("csid", competitionSeason.getId().toString());
                LeMessage message = LeMessageBuilder.create().setEntityId(teamId)
                        .setIdType(IdType.TOP_LIST).setContent(messageContent).build();
                sendMessage(message);
            }
            LOG.info("the current players and csid is updated");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
