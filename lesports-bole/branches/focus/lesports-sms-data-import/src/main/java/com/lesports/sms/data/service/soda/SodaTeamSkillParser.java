package com.lesports.sms.data.service.soda;

import com.google.common.collect.Maps;
import com.lesports.sms.api.common.CompetitorType;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.data.service.IThirdDataParser;
import com.lesports.sms.data.service.Parser;
import com.lesports.sms.data.util.SodaContant;
import com.lesports.sms.model.*;
import com.lesports.sms.service.CompetitionSeasonService;
import com.lesports.sms.service.CompetitionService;
import com.lesports.sms.service.CompetitorSeasonStatService;
import com.lesports.sms.service.TeamService;
import com.lesports.utils.LeDateUtils;
import freemarker.template.utility.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by zhonglin on 2016/3/7.
 */
@Service("sodaTeamSkillParser")
public class SodaTeamSkillParser extends Parser implements IThirdDataParser {

    private static Logger logger = LoggerFactory.getLogger(SodaTeamSkillParser.class);

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

            String time = rootElmement.attributeValue("TimeStamp");
            Date data = LeDateUtils.parseYMDHMS(time);
            String year = LeDateUtils.formatYYYY(data);

            // 队伍信息
            Element teamInfo = rootElmement.element("team");
            Element skills = rootElmement.element("skills");
            String sodaTeamId = teamInfo.attributeValue("id");
            Team team = SbdsInternalApis.getTeamBySodaId(sodaTeamId);
            Iterator<Element> statisticIterator = skills.elementIterator("statistic");
            while (statisticIterator.hasNext()) {
                Element statistic = statisticIterator.next();
                Map<String, Object> stats = Maps.newHashMap();

                stats.put("year", year);

                String compId = statistic.attributeValue("compId");
                stats.put("compId", compId);

                String compName = statistic.attributeValue("compName");
                stats.put("compName", compName);

                //根据名称或者缩略名称查询赛事
                long cid = SodaContant.getCidBySodaId(compId);

                CompetitionSeason competitionSeason = SbdsInternalApis.getLatestCompetitionSeasonByCid(cid);
                if (competitionSeason == null) continue;


                CompetitorSeasonStat competitorSeasonStat = SbdsInternalApis.getCompetitorSeasonStatByCsidAndCompetitor(competitionSeason.getId(), team.getId(), CompetitorType.TEAM);
                if (competitorSeasonStat == null) {
                    competitorSeasonStat = new CompetitorSeasonStat();
                    competitorSeasonStat.setAllowCountries(getAllowCountries());
                }

                competitorSeasonStat.setCompetitorId(team.getId());
                competitorSeasonStat.setCid(cid);
                competitorSeasonStat.setCsid(competitionSeason.getId());

                String homeAway = statistic.attributeValue("homeAway");
                stats.put("homeAway", homeAway);

                String total = statistic.attributeValue("total");
                stats.put("total", total);
                String win = statistic.attributeValue("win");
                stats.put("win", win);
                String draw = statistic.attributeValue("draw");
                stats.put("draw", draw);
                String lose = statistic.attributeValue("lose");
                stats.put("lose", lose);
                String goal = statistic.attributeValue("goal");
                stats.put("goal", goal);
                String concede = statistic.attributeValue("concede");
                stats.put("concede", concede);
                String goaldiffer = statistic.attributeValue("goaldiffer");
                stats.put("goaldiffer", goaldiffer);
                String avgGoal = statistic.attributeValue("avgGoal");
                stats.put("avgGoal", avgGoal);
                String avgConcede = statistic.attributeValue("avgConcede");
                stats.put("avgConcede", avgConcede);
                String point = statistic.attributeValue("point");
                stats.put("point", point);

                competitorSeasonStat.setStats(stats);
                competitorSeasonStat.setType(CompetitorType.TEAM);
                SbdsInternalApis.saveCompetitorSeasonStat(competitorSeasonStat);
            }

        } catch (Exception e) {
            logger.error("insert into match  error: ", e);
        }
        return result;


    }

}
