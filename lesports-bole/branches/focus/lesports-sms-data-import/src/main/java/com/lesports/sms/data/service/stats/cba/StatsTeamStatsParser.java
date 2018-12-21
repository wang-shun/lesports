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

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Map;


/**
 * Created by qiaohongxin on 2016/8/17.
 */
@Service("StatsTeamStatParse")
public class StatsTeamStatsParser extends com.lesports.sms.data.service.Parser implements IThirdDataParser {
    private static Logger LOG = LoggerFactory.getLogger(StatsTeamStatsParser.class);
    private Map<String, String> elementIdentifyMap = ImmutableMap.of("NBA", "nba", "CBACHN", "bk");

    @Override
    public Boolean parseData(String file) {
        Boolean result = false;
        try {
            File xmlFile = new File(file);
            if (!xmlFile.exists()) {
                LOG.warn("parsing the file:{} not exists", file);
                return result;
            }
            SAXReader reader = new SAXReader();
            FileInputStream fileInputStream = new FileInputStream(xmlFile);
            Document document = reader.read(fileInputStream);
            org.dom4j.Element rootElement = document.getRootElement();
            String cName = CommonUtil.getStringValue(rootElement.selectObject("/sports-statistics/sports-teamstats/league/@alias"));
            String elementIdentify = elementIdentifyMap.get(cName);
            String season = CommonUtil.getStringValue(rootElement.selectObject("/sports-statistics/sports-teamstats/season/@season"));
            String stageId = CommonUtil.getStringValue(rootElement.selectObject("/sports-statistics/sports-teamstats/gametype/@id"));
            List<org.dom4j.Element> stats = rootElement.selectNodes("/sports-statistics/sports-teamstats/" + elementIdentify + "-team-splits");
            if (CollectionUtils.isEmpty(stats)) {
                LOG.warn("the file is empty", file);
                return false;
            }
            Competition competitions = SbdsInternalApis.getCompetitionById(Constants.statsNameMap.get(cName));
            if (competitions == null) {
                LOG.warn("can not find relative competions,name is" + cName);
                return result;
            }
            CompetitionSeason competitionSeason = SbdsInternalApis.getCompetitionSeasonByCidAndSeason(competitions.getId(), season);
            if (null == competitionSeason) {
                LOG.warn("the competitionSeason  is empty");
                return false;
            }
            String stageName = Constants.nbaGameType.get(stageId);
            Long stage = 0L;
            List<DictEntry> stageList = SbdsInternalApis.getDictEntriesByName(stageName);
            if (CollectionUtils.isEmpty(stageList)) {
                LOG.warn("the stage  is not exist,stage:{}", stageName);
            } else {
                stage = stageList.get(0).getId();
            }
            for (org.dom4j.Element element : stats) {
                String teamId = CommonUtil.getStringValue(element.selectObject("./team-code/@global-id"));
                Team team = SbdsInternalApis.getTeamByPartnerIdAndPartnerType(teamId, Constants.PartnerSourceStaticId);
                if (team == null) continue;
                CompetitorSeasonStat cidStat = SbdsInternalApis.getCompetitorSeasonStatByCsidAndCompetitor(competitionSeason.getId(), team.getId(), CompetitorType.TEAM);
                if (cidStat == null) {
                    cidStat = new CompetitorSeasonStat();
                    cidStat.setCid(competitions.getId());
                    cidStat.setCsid(competitionSeason.getId());
                    cidStat.setAllowCountries(competitions.getAllowCountries());
                    cidStat.setCompetitorId(team.getId());
                    cidStat.setType(CompetitorType.TEAM);
                    cidStat.setStage(stage);
                }
                org.dom4j.Element currentElement = element.element(elementIdentify + "-team-split");
                cidStat.setStats(getNBAStats(currentElement, StatConstats.NBATeamTotalStatPath));
                cidStat.setAvgStats(getNBAStats(currentElement, StatConstats.NBATeamAVGStatPath));
                if (SbdsInternalApis.saveCompetitorSeasonStat(cidStat) > 0) {
                    LOG.warn("the current player:{} currentCsid ：{} and Stat:{} is updated", competitionSeason.getId(), cidStat.toString());
                }
            }
            if (competitions.getId().longValue() == 44001L || competitions.getId().longValue() == 39001L) {
                Long scopeId = 100009000L;
                MessageContent messageContent = new MessageContent();
                messageContent.addToMsgBody("scope", scopeId.toString()).addToMsgBody("competitorType", CompetitorType.TEAM.toString()).addToMsgBody("scopeType", ScopeType.CONFERENCE.toString()).addToMsgBody("cid", competitionSeason.getCid().toString()).addToMsgBody("csid", competitionSeason.getId().toString());
                LeMessage message = LeMessageBuilder.create().setEntityId(scopeId)
                        .setIdType(IdType.TOP_LIST).setContent(messageContent).build();
                sendMessage(message);
            }
            LOG.warn("the competition:{}, seasons are updated sucessfully", competitions.getName());
            return true;
        } catch (Exception e) {
            return false;
        }
    }


}
