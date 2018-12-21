package com.lesports.sms.data.service.soda.top12;

import com.google.common.collect.Lists;
import com.lesports.query.InternalCriteria;
import com.lesports.query.InternalQuery;
import com.lesports.sms.api.common.CompetitorType;
import com.lesports.sms.api.common.PlayerType;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.data.model.Constants;
import com.lesports.sms.data.service.IThirdDataParser;
import com.lesports.sms.data.service.Parser;
import com.lesports.sms.data.util.SodaContant;
import com.lesports.sms.model.*;
import com.lesports.utils.LeDateUtils;
import com.lesports.utils.math.LeNumberUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zhonglin on 2016/11/23.
 */
@Service("top12TeamSeasonParser")
public class Top12TeamSeasonParser  extends Parser implements IThirdDataParser {

    private static Logger logger = LoggerFactory.getLogger(Top12TeamSeasonParser.class);
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public Boolean parseData(String file) {
        Boolean result = false;
        try {
            File xmlFile = new File(file);
            if (!xmlFile.exists()) {
                logger.warn("parsing the file:{} not exists", file);
                return result;
            }

            Long cid = SodaContant.parserCidMap.get(getCidByFilePath(file));
            CompetitionSeason latestCompetitionSeason =  SbdsInternalApis.getLatestCompetitionSeasonByCid(cid);
            if(latestCompetitionSeason == null){
                logger.warn("parseTeamSeasonData the competitionSeason is null, cid:{} ", cid);
                return  result;
            }

            SAXReader reader = new SAXReader();
            Document document = reader.read(xmlFile);
            Element rootElmement = document.getRootElement();

            // 球队的基本信息
            Element teamElement = rootElmement.element("Team");
            // 教练的基本信息
            Element coachElement = teamElement.element("Coach");
            // 阵容基本信息
            Element squadsElement = teamElement.element("Squads");


            String teamId = teamElement.attributeValue("id");
            String teamName = teamElement.attributeValue("name");
            String teamEngName = teamElement.attributeValue("nameEng");


            List<DictEntry> dictEntryList = SbdsInternalApis.getDictEntriesByName("^足球$");
            if (CollectionUtils.isEmpty(dictEntryList)) {
                logger.warn("can not find gameFType,name:足球");
                return result;
            }
            Long gameFType = dictEntryList.get(0).getId();

            Team team = SbdsInternalApis.getTeamBySodaId(teamId);
            if (null == team) {
                logger.warn("can not find relative team,sodaid:{},name:{}", teamId, teamName);
                return result;
            }

            String coachId = coachElement.attributeValue("id");
            String coachName = coachElement.getText();


            // 教练信息
            Player coach = SbdsInternalApis.getPlayerBySodaId(coachId);
            if (null == coach) {
                coach = new Player();
                coach.setName(coachName);
                coach.setMultiLangNames(getMultiLang(coachName));
                coach.setType(PlayerType.COACH);
                coach.setSodaId(coachId);
                coach.setGameFType(gameFType);
                coach.setAllowCountries(getAllowCountries());
                coach.setOnlineLanguages(getOnlineLanguage());
            } else {
                coach.setName(coachName);
                coach.setMultiLangNames(getMultiLang(coachName));
            }
            SbdsInternalApis.savePlayer(coach);

            coach = SbdsInternalApis.getPlayerBySodaId(coachId);
            logger.info("coach_id: {} ", coach.getId());

            team.setName(teamName);
            team.setMultiLangNames(getEngMultiLang(team.getMultiLangNames(), teamName, teamEngName));


            // 球员信息
            Iterator<Element> playerIterator = squadsElement.elementIterator("Player");
            List<TeamSeason.TeamPlayer> teamPlayers = Lists.newArrayList();
            Long currentCaptain = 0L;
            List<Long> coreIds = Lists.newArrayList();
            while (playerIterator.hasNext()) {
                Element playerElement = playerIterator.next();
                String playerId = playerElement.attributeValue("id");
                String playerName = playerElement.attributeValue("name");
                String playerEngName = playerElement.attributeValue("nameEng");
                String shirtNumber = playerElement.attributeValue("number");
                String positionId = playerElement.element("Position").attributeValue("id");
                String positionName = playerElement.element("Position").attributeValue("name");
                String countryName = playerElement.element("Country").attributeValue("name");
                String cityName = playerElement.element("City").attributeValue("name");
                String birthday = playerElement.attributeValue("birthDate");
                String height = playerElement.attributeValue("height");
                String weight = playerElement.attributeValue("weight");
                String captain = playerElement.attributeValue("captain");
                String keyman = playerElement.attributeValue("keyman");
                String current = playerElement.attributeValue("current");

                if("0".equals(current))continue; //不是本期球员

                Player sodaPlayer = SbdsInternalApis.getPlayerBySodaId(playerId);
                if (positionName.equals("门将")) {
                    positionName = "守门员";
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
                    buildSodaPlayer(gameFType, playerId, playerName, playerEngName, countryName, cityName, birthday, height, weight, sodaPlayer, dictEntry);

                } else {
                    buildSodaPlayer(gameFType, playerId, playerName, playerEngName, countryName, cityName, birthday, height, weight, sodaPlayer, dictEntry);
                }

                Set<Long> cids = sodaPlayer.getCids();
                cids.add(cid);
                sodaPlayer.setCids(cids);

                SbdsInternalApis.savePlayer(sodaPlayer);
                sodaPlayer = SbdsInternalApis.getPlayerBySodaId(playerId);

                //是队长
                if ("1".equals(captain)) {
                    currentCaptain = sodaPlayer.getId();
                }

                //是核心球员
                if ("1".equals(keyman)) {
                    coreIds.add(sodaPlayer.getId());
                }

                TeamSeason.TeamPlayer teamPlayer = new TeamSeason.TeamPlayer();
                buildTeamPlayer(shirtNumber, positionId, positionName, sodaPlayer, dictEntry, teamPlayer);
                teamPlayers.add(teamPlayer);
            }


            // 赛季阵容
            List<TeamSeason> teamSeasons = SbdsInternalApis.getTeamSeasonsByTeamIdAndCSId(team.getId(), latestCompetitionSeason.getId());
            TeamSeason teamSeason = new TeamSeason();
            if (CollectionUtils.isNotEmpty(teamSeasons)) {
                teamSeason = teamSeasons.get(0);
                teamSeason.setCoachId(coach.getId());
                teamSeason.setCoachName(coachName);
                teamSeason.setMultiLangCoachNames(getMultiLang(coachName));
                teamSeason.setPlayers(teamPlayers);
            } else {
                teamSeason.setAllowCountries(getAllowCountries());
                teamSeason.setPlayers(teamPlayers);
                teamSeason.setCoachId(coach.getId());
                teamSeason.setTid(team.getId());
                teamSeason.setCoachName(coachName);
                teamSeason.setMultiLangCoachNames(getMultiLang(coachName));
                teamSeason.setCsid(latestCompetitionSeason.getId());
            }
            if(currentCaptain !=0 ){
                teamSeason.setCurrentCaptain(currentCaptain);
            }
            teamSeason.setCorePlayers(coreIds);
            logger.info("team: {} , tsid: {}, teamPlayers: {} ", team.getName(), teamSeason.getId(), teamPlayers);

            logger.info("teamSeason: {} ", teamSeason);
            SbdsInternalApis.saveTeamSeason(teamSeason);


            //更新球队信息

            if(cid != null){
                team.setCurrentTsid(teamSeason.getId());
                team.setCurrentCid(cid);
                team.setCurrentCsid(teamSeason.getCsid());
                SbdsInternalApis.saveTeam(team);
            }
            result = true;

        } catch (Exception e) {
            logger.error("insert into team  error: ", e);
        }
        return result;


    }


    private void buildSodaPlayer(Long gameFType, String playerId, String playerName, String playerEngName, String countryName, String cityName, String birthday, String height, String weight, Player sodaPlayer, DictEntry dictEntry) throws Exception {
        sodaPlayer.setSodaId(playerId);
        sodaPlayer.setType(PlayerType.PLAYER);
        DateFormat format = new SimpleDateFormat("yyyyMMdd");
        format.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        if (StringUtils.isNotBlank(birthday)) {
            sodaPlayer.setBirthdate(LeDateUtils.formatYYYYMMDD(format.parse(birthday)));
        }
        sodaPlayer.setEnglishName(playerEngName);
        sodaPlayer.setHeight(LeNumberUtils.toInt(height));
        sodaPlayer.setWeight(LeNumberUtils.toInt(weight));
        sodaPlayer.setNationality(countryName);
        sodaPlayer.setMultiLangNationalities(getMultiLang(countryName));
        sodaPlayer.setCity(cityName);
        sodaPlayer.setMultiLangCities(getMultiLang(cityName));
        sodaPlayer.setPosition(dictEntry.getId());
        sodaPlayer.setPositionName(dictEntry.getName());
        sodaPlayer.setGameFType(gameFType);
        sodaPlayer.setName(playerName);
        sodaPlayer.setMultiLangNames(getEngMultiLang(sodaPlayer.getMultiLangNames(), playerName, playerEngName));
        sodaPlayer.setAllowCountries(getAllowCountries());
        sodaPlayer.setOnlineLanguages(getOnlineLanguage());
    }


    private void buildTeamPlayer(String shirtNumber, String positionId, String positionName, Player sodaPlayer, DictEntry dictEntry, TeamSeason.TeamPlayer teamPlayer) {
        teamPlayer.setPid(sodaPlayer.getId());
        teamPlayer.setName(sodaPlayer.getName());
        teamPlayer.setMultiLangNames(getEngMultiLang(sodaPlayer.getMultiLangNames(), sodaPlayer.getName(), sodaPlayer.getEnglishName()));
        teamPlayer.setNumber(LeNumberUtils.toLong(shirtNumber));
        teamPlayer.setPositionName(positionName);
        teamPlayer.setMultiLangPositionNames(getMultiLang(positionName));
        teamPlayer.setPosition(dictEntry.getId());
        teamPlayer.setPostionOrder(Integer.parseInt(positionId));
    }


    private String  getCidByFilePath(String path) {
        String cid = "";
        Pattern pattern = Pattern.compile("/soda/(.+?)/team/");
        Matcher matcher = pattern.matcher(path);
        if(matcher.find()){
            cid = matcher.group(1);
        }
        return cid;
    }
}