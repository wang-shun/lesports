package com.lesports.sms.data.service.soda;

import com.google.common.collect.Lists;
import com.hankcs.hanlp.HanLP;
import com.lesports.api.common.LangString;
import com.lesports.api.common.LanguageCode;
import com.lesports.query.InternalCriteria;
import com.lesports.query.InternalQuery;
import com.lesports.sms.api.common.PlayerType;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.data.model.Constants;
import com.lesports.sms.data.service.IThirdDataParser;
import com.lesports.sms.data.service.Parser;
import com.lesports.sms.model.*;
import com.lesports.sms.service.*;
import com.lesports.utils.LeDateUtils;
import com.lesports.utils.math.LeNumberUtils;
import org.apache.commons.collections.CollectionUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.*;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by ruiyuansheng on 2016/3/1.
 */

@Service("sodaLineupParser")
public class SodaLineupParser extends Parser implements IThirdDataParser {

    private static Logger logger = LoggerFactory.getLogger(SodaLineupParser.class);

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

            Iterator<Element> teams = competitionInfo.elementIterator("Team");

            while (teams.hasNext()) {
                Element teamElement = teams.next();
                // 赛事基本信息，赛事名称、当前赛季、当前轮次
                String teamId = teamElement.attributeValue("id");
                String teamName = teamElement.attributeValue("name");
                String teamEngName = teamElement.attributeValue("nameEng");
                String coachId = teamElement.element("Manager").attributeValue("id");
                String coachName = teamElement.element("Manager").attributeValue("name");
                String coachEngName = teamElement.element("Manager").attributeValue("nameEng");

                Team team = SbdsInternalApis.getTeamBySodaId(teamId);
                if (null == team) {
                    logger.warn("can not find relative team,sodaid:{},name:{}", teamId, teamName);
                    return result;
                }

                List<DictEntry> dictEntryList = SbdsInternalApis.getDictEntriesByName("^足球$");

                if (CollectionUtils.isEmpty(dictEntryList)) {
                    logger.warn("can not find gameFType,name:足球");
                    return result;
                }

                Long gameFType = dictEntryList.get(0).getId();
                Player coach = SbdsInternalApis.getPlayerBySodaId(coachId);
                if (null == coach) {
                    coach = new Player();
                    coach.setName(coachName);
                    coach.setMultiLangNames(getEngMultiLang(coach.getMultiLangNames(),coachName,coachEngName));
                    coach.setType(PlayerType.COACH);
                    coach.setSodaId(coachId);
                    coach.setGameFType(gameFType);
                    coach.setAllowCountries(getAllowCountries());
                    coach.setOnlineLanguages(getOnlineLanguage());
                }
                else{
                    coach.setMultiLangNames(getEngMultiLang(coach.getMultiLangNames(),coachName,coachEngName));
                }
                SbdsInternalApis.savePlayer(coach);



                InternalQuery internalQuery = new InternalQuery();
                internalQuery.addCriteria(InternalCriteria.where("deleted").is(false));
                internalQuery.addCriteria(InternalCriteria.where("cid").is(cid));
                internalQuery.addCriteria(InternalCriteria.where("season").regex(currentSeason));
                List<CompetitionSeason> competitionSeasons = SbdsInternalApis.getCompetitionSeasonsByQuery(internalQuery);

                if (CollectionUtils.isEmpty(competitionSeasons)) {
                    logger.warn("can not find relative event,name:{},season:{}", competitionName, currentSeason);
                    return result;
                }
                long csid = competitionSeasons.get(0).getId();

                //出场球员信息
                Iterator<Element> playerIterator = teamElement.elementIterator("Player");
                List<TeamSeason.TeamPlayer> teamPlayers = Lists.newArrayList();
                while (playerIterator.hasNext()) {
                    Element playerElement = playerIterator.next();
                    String playerId = playerElement.attributeValue("id");
                    String playerName = playerElement.attributeValue("name");
                    String enName = playerElement.attributeValue("nameEng");
                    String shirtNumber = playerElement.attributeValue("number");
                    String positionId = playerElement.element("Position").attributeValue("id");
                    String positionName = playerElement.element("Position").attributeValue("name");
                    String counId = playerElement.element("Country").attributeValue("id");
                    String counName = playerElement.element("Country").attributeValue("name");
                    String birthday = playerElement.attributeValue("birthDate");
                    String height = playerElement.attributeValue("height");
                    String weight = playerElement.attributeValue("weight");

                    Player sodaPlayer = SbdsInternalApis.getPlayerBySodaId(playerId);
                    if(positionName.equals("门将")){
                        positionName = "守门员" ;
                    }
                    DictEntry dictEntry = SbdsInternalApis.getDictEntryByNameAndParentId("^" + positionName + "$", Constants.POSITION);
                    if (null == dictEntry) {
                        dictEntry = new DictEntry();
                        dictEntry.setName(positionName);
                        dictEntry.setMultiLangNames(getMultiLang(positionName));
                        dictEntry.setParentId(Constants.POSITION);
                        SbdsInternalApis.saveDict(dictEntry);
                    }

                    if (null == sodaPlayer) {
                        sodaPlayer = new Player();
                        buildSodaPlayer(gameFType, playerId, playerName, enName, counName, birthday, height, weight, sodaPlayer, dictEntry);

                    } else {
                        buildSodaPlayer(gameFType, playerId, playerName, enName, counName, birthday, height, weight, sodaPlayer, dictEntry);
                    }
                    Set<Long> cids = sodaPlayer.getCids();
                    cids.add(cid);
                    sodaPlayer.setCids(cids);
                    SbdsInternalApis.savePlayer(sodaPlayer);
                    TeamSeason.TeamPlayer teamPlayer = new TeamSeason.TeamPlayer();
                    buildTeamPlayer(shirtNumber, positionId, positionName, sodaPlayer, dictEntry, teamPlayer);
                    teamPlayers.add(teamPlayer);
                }


                List<TeamSeason> teamSeasons = SbdsInternalApis.getTeamSeasonsByTeamIdAndCSId(team.getId(), csid);
                TeamSeason teamSeason = new TeamSeason();
                if (CollectionUtils.isNotEmpty(teamSeasons)) {
                    teamSeason = teamSeasons.get(0);
                    teamSeason.setCoachId(coach.getId());
                    teamSeason.setCoachName(coachName);
                    teamSeason.setMultiLangCoachNames(getEngMultiLang(teamSeason.getMultiLangCoachNames(),coachName,coachEngName));
                    teamSeason.setPlayers(teamPlayers);
                } else {
                    teamSeason.setAllowCountries(getAllowCountries());
                    teamSeason.setPlayers(teamPlayers);
                    teamSeason.setCoachId(coach.getId());
                    teamSeason.setTid(team.getId());
                    teamSeason.setCoachName(coachName);
                    teamSeason.setMultiLangCoachNames(getEngMultiLang(teamSeason.getMultiLangCoachNames(),coachName,coachEngName));
                    teamSeason.setCsid(csid);
                }

                SbdsInternalApis.saveTeamSeason(teamSeason);
            }


            logger.info("insert lineup success", file);
        } catch (Exception e) {
            logger.error("insert into player  error: ", e);
        }
        return result;
    }

    private void buildTeamPlayer(String shirtNumber, String positionId, String positionName, Player sodaPlayer, DictEntry dictEntry, TeamSeason.TeamPlayer teamPlayer) {
        teamPlayer.setPid(sodaPlayer.getId());
        teamPlayer.setName(sodaPlayer.getName());
        teamPlayer.setMultiLangNames(getEngMultiLang(sodaPlayer.getMultiLangNames(),sodaPlayer.getName(),sodaPlayer.getEnglishName()));
        teamPlayer.setNumber(LeNumberUtils.toLong(shirtNumber));
        teamPlayer.setPositionName(positionName);
        teamPlayer.setMultiLangPositionNames(getMultiLang(positionName));
        teamPlayer.setPosition(dictEntry.getId());
        teamPlayer.setPostionOrder(LeNumberUtils.toInt(positionId));
    }

    private void buildSodaPlayer(Long gameFType, String playerId, String playerName, String enName, String counName, String birthday, String height, String weight, Player sodaPlayer, DictEntry dictEntry) {
        sodaPlayer.setSodaId(playerId);
        sodaPlayer.setType(PlayerType.PLAYER);
        sodaPlayer.setBirthdate(LeDateUtils.formatYYYYMMDD(LeDateUtils.parseYYYY_MM_DD(birthday)));
        sodaPlayer.setEnglishName(enName);
        sodaPlayer.setHeight(LeNumberUtils.toInt(height));
        sodaPlayer.setWeight(LeNumberUtils.toInt(weight));
        sodaPlayer.setNationality(counName);
        sodaPlayer.setMultiLangNationalities(getMultiLang(counName));
        sodaPlayer.setPosition(dictEntry.getId());
        sodaPlayer.setPositionName(dictEntry.getName());
        sodaPlayer.setGameFType(gameFType);
        sodaPlayer.setName(playerName);
        sodaPlayer.setMultiLangNames(getEngMultiLang(sodaPlayer.getMultiLangNames(),playerName,enName));
        sodaPlayer.setAllowCountries(getAllowCountries());
        sodaPlayer.setOnlineLanguages(getOnlineLanguage());
    }
}
