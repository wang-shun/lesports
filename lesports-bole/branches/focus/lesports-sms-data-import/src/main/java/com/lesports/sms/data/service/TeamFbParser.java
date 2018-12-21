package com.lesports.sms.data.service;

import java.io.File;
import java.util.*;

import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.data.model.Constants;
import com.lesports.sms.model.Competition;
import com.lesports.sms.model.CompetitionSeason;
import com.lesports.sms.model.DictEntry;
import com.lesports.sms.model.Team;
import com.lesports.sms.service.CompetitionSeasonService;
import com.lesports.sms.service.CompetitionService;
import com.lesports.sms.service.DictService;
import com.lesports.sms.service.TeamService;
import org.apache.commons.collections.CollectionUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by lufei1 on 2014/11/27.
 */
@Service("teamFbParser")
public class TeamFbParser extends Parser implements IThirdDataParser {

    private static Logger logger = LoggerFactory.getLogger(TeamFbParser.class);


    @Override
    public Boolean parseData(String file) {
        logger.info("team parser begin");
        Boolean result = false;
        File xmlFile = new File(file);
        SAXReader reader = new SAXReader();
        try {
            Document document = reader.read(xmlFile);
            Element rootElement = document.getRootElement();
            Element tournament = rootElement.element("Sport").element("Category").element("Tournament");
            String gameS = tournament.attribute("uniqueTournamentId").getValue();
            String season = tournament.element("Season").attribute("start").getValue().substring(0, 4);
            String name = Constants.nameMap.get(gameS);
            //查找比赛类型
            String sportsType = Constants.sportsTypeMap.get(rootElement.element("Sport").attributeValue("id"));
            List<DictEntry> dictEntryList = SbdsInternalApis.getDictEntriesByName("^" + sportsType + "$");
            Long gameFType = dictEntryList.get(0).getId();
            //赛事查询

            List<Competition> competitions = SbdsInternalApis.getCompetitionByNameAndGameFType(name, gameFType);
            if (CollectionUtils.isEmpty(competitions)) {
                logger.warn("can not find relative event,uniqueTournamentId:{},name:{}", gameS, name);
                return result;
            }
            CompetitionSeason competitionSeason = SbdsInternalApis.getLatestCompetitionSeasonByCid(competitions.get(0).getId());
            if (competitionSeason == null) {
                logger.warn("can not find relative event,uniqueTournamentId:{},name:{},season:{}", gameS, name, season);
                return result;
            }
            Element teamselement = rootElement.element("Sport").element("Category").element("Tournament").element("Teams");
            Iterator teams = teamselement.elementIterator("Team");
            while (teams.hasNext()) {
                Element team = (Element) teams.next();
                String engliahName = team.attributeValue("name");
                String teamName = null;
                List list = team.selectNodes("./Translation/TranslatedValue");
                if (CollectionUtils.isNotEmpty(list)) {
                    Element tn = (Element) team.element("Translation").elementIterator("TranslatedValue").next();
                    teamName = tn.attributeValue("value");
                }
                Team team1 = SbdsInternalApis.getTeamByPartnerIdAndPartnerType(team.attributeValue("id"), Constants.PartnerSourceStaticId);
                if (team1 != null) {
                    Set cids = team1.getCids();
                    cids.add(competitionSeason.getId());
                    team1.setCids(cids);
                } else {
                    Set cids = new HashSet();
                    cids.add(competitionSeason.getId());
                    team1 = new Team();
                    team1.setAllowCountries(getAllowCountries());
                    team1.setOnlineLanguages(getOnlineLanguage());
                    teamName = teamName != null ? teamName : engliahName;
                    team1.setName(teamName);
                    team1.setMultiLangNames(getMultiLang(teamName));
                    team1.setPartnerId(team.attributeValue("id"));
                    team1.setGameFType(gameFType);
                    team1.setPartnerType(Constants.PartnerSourceStaticId);
                    team1.setCids(cids);
                }
                SbdsInternalApis.saveTeam(team1);
                logger.info("save fail,sms database not exist:teamEnName:{}", team.attributeValue("name"));
            }
        } catch (DocumentException e) {
            logger.error("parse team xml error", e);
        }
        return true;
    }
}
