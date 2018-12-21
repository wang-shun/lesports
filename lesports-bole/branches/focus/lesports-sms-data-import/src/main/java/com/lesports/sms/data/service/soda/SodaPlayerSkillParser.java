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
import com.lesports.sms.service.PlayerService;
import com.lesports.utils.LeDateUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.xml.crypto.Data;
import java.io.File;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by zhonglin on 2016/3/9.
 */
@Service("sodaPlayerSkillParser")
public class SodaPlayerSkillParser extends Parser implements IThirdDataParser {

    private static Logger logger = LoggerFactory.getLogger(SodaPlayerSkillParser.class);

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
            Element teamInfo = rootElmement.element("player");
            Element skills = rootElmement.element("skills");
            String sodaTeamId = teamInfo.attributeValue("id");
            Player player = SbdsInternalApis.getPlayerBySodaId(sodaTeamId);

            Iterator<Element> statisticIterator = skills.elementIterator("statistic");
            while (statisticIterator.hasNext()) {
                Element statistic = statisticIterator.next();
                Map<String, Object> stats = Maps.newHashMap();

                stats.put("year", year);

                String compId = statistic.attributeValue("compId");
                stats.put("compId", compId);

                long cid = SodaContant.getCidBySodaId(compId);

                //如果map里没有csid,根据cid去取，为了导入旧数据
                long csid = SodaContant.getCsidByYearAndCid(cid + "-" + year);
                CompetitionSeason competitionSeason = null;
                if (csid == 0) {
                    competitionSeason = SbdsInternalApis.getLatestCompetitionSeasonByCid(cid);
                    if (competitionSeason == null) {
                        logger.warn("parsing the file:{} competitionSeason not exists, cid:{},year:{}", file, cid, year);
                        continue;
                    }
                }
                CompetitorSeasonStat competitorSeasonStat = SbdsInternalApis.getCompetitorSeasonStatByCsidAndCompetitor(csid, player.getId(), CompetitorType.PLAYER);
                if (competitorSeasonStat == null) {
                    competitorSeasonStat = new CompetitorSeasonStat();
                    competitorSeasonStat.setAllowCountries(getAllowCountries());
                }
                competitorSeasonStat.setCompetitorId(player.getId());
                competitorSeasonStat.setCid(cid);
                competitorSeasonStat.setCsid(csid);

                //赛事名称
                String compName = statistic.attributeValue("compName");
                stats.put("compName", compName);

                String win = statistic.attributeValue("win");
                stats.put("win", win);
                String draw = statistic.attributeValue("draw");
                stats.put("draw", draw);
                String lose = statistic.attributeValue("lose");
                stats.put("lose", lose);

                String games = statistic.attributeValue("games");
                stats.put("games", games);
                String minutes = statistic.attributeValue("minutes");
                stats.put("minutes", minutes);
                String goal = statistic.attributeValue("goal");
                stats.put("goal", goal);
                String concede = statistic.attributeValue("concede");
                stats.put("concede", concede);
                String assist = statistic.attributeValue("assist");
                stats.put("assist", assist);
                String keypass = statistic.attributeValue("keypass");
                stats.put("keypass", keypass);
                String shoot = statistic.attributeValue("shoot");
                stats.put("shoot", shoot);
                String shootok = statistic.attributeValue("shootok");
                stats.put("shootok", shootok);
                String foul = statistic.attributeValue("foul");
                stats.put("foul", foul);
                String fouled = statistic.attributeValue("fouled");
                stats.put("fouled", fouled);
                String offside = statistic.attributeValue("offside");
                stats.put("offside", offside);

                String tackle = statistic.attributeValue("tackle");
                stats.put("tackle", tackle);
                String clearance = statistic.attributeValue("clearance");
                stats.put("clearance", clearance);
                String yellow = statistic.attributeValue("yellow");
                stats.put("yellow", yellow);
                String red = statistic.attributeValue("red");
                stats.put("red", red);
                String yellow2 = statistic.attributeValue("yellow2");
                stats.put("yellow2", yellow2);
                String save = statistic.attributeValue("save");
                stats.put("save", save);
                String penalty = statistic.attributeValue("penalty");
                stats.put("penalty", penalty);
                String score = statistic.attributeValue("score");
                stats.put("score", score);
                String rank = statistic.attributeValue("rank");
                stats.put("rank", rank);

                competitorSeasonStat.setStats(stats);
                competitorSeasonStat.setType(CompetitorType.PLAYER);
                SbdsInternalApis.saveCompetitorSeasonStat(competitorSeasonStat);
            }

        } catch (Exception e) {
            logger.error("insert into match  error: ", e);
        }
        return result;


    }
}
