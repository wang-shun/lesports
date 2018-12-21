package com.lesports.sms.data.service.stats;

import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.data.model.Constants;
import com.lesports.sms.data.service.IThirdDataParser;
import com.lesports.sms.data.service.Parser;
import com.lesports.sms.model.*;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by qiaohongxin on 2015/9/17.
 */
@Service("NBATeamsParser")
public class NBATeamsParser extends Parser implements IThirdDataParser {

    private static Logger logger = LoggerFactory.getLogger(NBATeamsParser.class);

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
            Element nbaRosters = document.getRootElement();
            String sportName = nbaRosters.element("league").attributeValue("alias");
            String season = nbaRosters.element("season").attributeValue("season");
            Long gameFTypeId = SbdsInternalApis.getDictEntriesByName("篮球$").get(0).getId();
            List<Competition> competitions = SbdsInternalApis.getCompetitionByNameAndGameFType("^" + sportName, gameFTypeId);
            if (competitions == null || competitions.isEmpty()) {
                logger.warn("can not find relative competions,name is" + sportName);
                return result;
            }
            CompetitionSeason competitionseason = SbdsInternalApis.getLatestCompetitionSeasonByCid(competitions.get(0).getId());
            if (competitionseason == null) {
                logger.warn("can not find the right time competions,name is" + sportName + "season is" + season);
                return result;
            }
            Long csid = competitionseason.getId();
            Iterator<Element> nbaconferences = nbaRosters.elementIterator("conference");
            while (nbaconferences.hasNext()) {
                Element nbaconference = nbaconferences.next();
                String conference = nbaconference.attributeValue("label");
                Iterator<Element> nbadivisions = nbaconference.elementIterator("division");
                while (nbadivisions.hasNext()) {
                    Element nbadivision = nbadivisions.next();
                    String division = nbadivision.attributeValue("label");
                    Iterator<Element> nbateams = nbadivision.elementIterator("team");
                    while (nbateams.hasNext()) {
                        Element nbateam = nbateams.next();
                        upsertTeam(competitions.get(0).getId(), csid, gameFTypeId, conference, division, nbateam);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("team info error: ", e);
        }
        return result;
    }


    private void upsertTeam(Long cid, Long csid, Long gameFTypeid, String conference, String division, Element tmpTeam) {
        Set cidMap = new HashSet<Long>();
        cidMap.add(cid);
        String partnerId = tmpTeam.attributeValue("global-id");
        Integer partnerType = Constants.PartnerSourceStaticId;
        Team team = new Team();
        if (null != SbdsInternalApis.getTeamByPartnerIdAndPartnerType(partnerId, partnerType)) {
            team = SbdsInternalApis.getTeamByPartnerIdAndPartnerType(partnerId, partnerType);
        }
        team.setCids(cidMap);
        team.setPartnerType(partnerType);
        team.setPartnerId(partnerId);
        team.setName(tmpTeam.attributeValue("name"));
        team.setMultiLangNames(getMultiLang(tmpTeam.attributeValue("name")));
        team.setCity(tmpTeam.attributeValue("city"));
        team.setMultiLangCities(getMultiLang(tmpTeam.attributeValue("city")));
        //  team.setConference(nbaRoster.element("conference").attributeValue("name"));
        //大项 篮球
        team.setAllowCountries(getAllowCountries());
        team.setOnlineLanguages(getOnlineLanguage());
        team.setGameFType(gameFTypeid);
        team.setConference(Constants.nbaConferenceId.get(conference));
        team.setRegion(Constants.nbaDivisionId.get(division));
        SbdsInternalApis.saveTeam(team);
        Long teamId = SbdsInternalApis.getTeamByPartnerIdAndPartnerType(partnerId, partnerType).getId();
        List<TeamSeason> seasonList = SbdsInternalApis.getTeamSeasonsByTeamIdAndCSId(teamId, csid);
        if (seasonList == null || seasonList.isEmpty()) {
            TeamSeason teamSeason = new TeamSeason();
            teamSeason.setTid(teamId);
            teamSeason.setCsid(csid);
            SbdsInternalApis.saveTeamSeason(teamSeason);
        }
    }

}
