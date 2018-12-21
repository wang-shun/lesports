package com.lesports.sms.data.service.soda.top12;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.lesports.query.InternalCriteria;
import com.lesports.query.InternalQuery;
import com.lesports.sms.api.common.CompetitorType;
import com.lesports.sms.api.common.TeamType;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.data.model.Constants;
import com.lesports.sms.data.model.PlayerPageContent;
import com.lesports.sms.data.model.StatConstats;
import com.lesports.sms.data.service.IThirdDataParser;
import com.lesports.sms.data.service.soda.SodaAssistParser;
import com.lesports.sms.data.util.CommonUtil;
import com.lesports.sms.model.*;
import com.lesports.utils.xml.ParserFactory;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.jdom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by qiaohongxin on 2016/8/17.
 */
@Service("playerPageParser")
public class PlayerPageContentParser extends com.lesports.sms.data.service.Parser implements IThirdDataParser {
    private static Logger LOG = LoggerFactory.getLogger(SodaAssistParser.class);

    @Override
    public Boolean parseData( String file) {
        Boolean result = false;
        try {
            File xmlFile = new File(file);
            if (!xmlFile.exists()) {
                LOG.warn("parsing the file:{} not exists", file);
                return result;
            }
            FileInputStream fileInputStream = new FileInputStream(xmlFile);
            com.lesports.utils.xml.Parser<PlayerPageContent> parserClass = new ParserFactory().createXmlParser(PlayerPageContent.class);
            PlayerPageContent playerPageContent = parserClass.parse(fileInputStream);
            if (playerPageContent == null) {
                LOG.warn("the file is empty", file);
                return false;
            }
            Player player = getplayer(playerPageContent);
            if (player == null) {
                LOG.warn("the player is not exist,sodaId:{}", playerPageContent.getSodaId());
                return false;
            }
            Team currentClub = getTeam(TeamType.CLUB_TEAM, playerPageContent.getCurrentClubId(), playerPageContent.getCurrentClubName());
            if (currentClub != null) {
                Map<TeamType, Player.PlayingTeam> teamNumber = player.getTeamNumber();
                if (teamNumber == null) teamNumber = new HashMap<>();
                Player.PlayingTeam clubTeam = new Player.PlayingTeam();
                clubTeam.setTeamId(currentClub.getId());
                clubTeam.setNumber(CommonUtil.parseInt(playerPageContent.getCurrentClubNumber(),0));
                teamNumber.put(TeamType.CLUB_TEAM, clubTeam);
                player.setTeamNumber(teamNumber);
                updateCurrentTeamStat(TeamType.CLUB_TEAM, player.getId(), currentClub.getId(), playerPageContent.getCurrentClubStats());
            }
            Team currentnational = getTeam(TeamType.NATIONAL_TEAM, playerPageContent.getCurrentNationalId(), playerPageContent.getCurrentNationalName());
            if (currentnational != null) {
                Map<TeamType, Player.PlayingTeam> teamNumber = player.getTeamNumber();
                if (teamNumber == null) teamNumber = new HashMap<>();
                Player.PlayingTeam clubTeam = new Player.PlayingTeam();
                clubTeam.setTeamId(currentnational.getId());
                clubTeam.setNumber(CommonUtil.parseInt(playerPageContent.getCurrentNationalNumber(),0));
                teamNumber.put(TeamType.NATIONAL_TEAM, clubTeam);
                player.setTeamNumber(teamNumber);
                updateCurrentTeamStat(TeamType.NATIONAL_TEAM, player.getId(), currentnational.getId(), playerPageContent.getCurrentNationalStats());
            }
            player.setCareer(getCareerDes(playerPageContent.getCareers()));
            SbdsInternalApis.savePlayer(player);
            LOG.info("the player is updated sucess,player:{}", player.toString());
            updateStats(TeamType.NATIONAL_TEAM, player.getId(), playerPageContent.getNationalStats());//更新参加的国家队的技术统计
            updateStats(TeamType.CLUB_TEAM, player.getId(), playerPageContent.getClubStats());//更新效力的俱乐部的总的技术统计
        } catch (Exception e) {
            LOG.warn("parse this file error.current ", e);
        }
        return true;
    }

    private List<Player.CareerItem> getCareerDes(List<PlayerPageContent.Career> careers) {
        List<Player.CareerItem> old = Lists.newArrayList();
        if (CollectionUtils.isEmpty(careers)) return old;
        for (PlayerPageContent.Career career : careers) {
            Team team = SbdsInternalApis.getTeamBySodaId(career.getClubId());
            if (team == null) continue;
            Player.CareerItem item = new Player.CareerItem();
            item.setTeamId(team.getId());
            item.setSeason(career.getSeason());
            old.add(item);
        }
        return old;
    }

    private void updateCurrentTeamStat(TeamType type, Long pid, Long tid, List<Element> stats) {
        if (CollectionUtils.isEmpty(stats)) return;
        for (Element element : stats) {
            PlayerCareerStat playerCareerStat = SbdsInternalApis.getPlayerCareerStatByPidAndTid(pid, tid);
            if (playerCareerStat == null) {
                playerCareerStat = new PlayerCareerStat();
                playerCareerStat.setPlayerId(pid);
                playerCareerStat.setTeamId(tid);
                playerCareerStat.setDeleted(false);
                playerCareerStat.setAllowCountries(getAllowCountries());
            }
            playerCareerStat.setStats(getStats(element, StatConstats.sodaPlayerTotalStatPath));
            playerCareerStat.setTeamType(type);
            SbdsInternalApis.savePlayerCareerStat(playerCareerStat);
            LOG.warn("the current player:{} careerStat:{} is updated", pid, playerCareerStat.toString());
        }
    }

    private void updateStats(TeamType type, Long pid, List<Element> stats) {
        if (CollectionUtils.isEmpty(stats)) return;
        for (Element element : stats) {
            String sodaCid = element.getAttributeValue("compId");
            String season = element.getAttributeValue("season");
            String name = element.getAttributeValue("compName");
            Long cid = StatConstats.cidMap.get(sodaCid);
            if (cid == null || cid < 0) {
                continue;
            }
            InternalQuery query = new InternalQuery();
            query.addCriteria(new InternalCriteria("cid", "eq", cid));
            query.addCriteria(new InternalCriteria("season", "eq", season));
            query.addCriteria(new InternalCriteria("deleted", "eq", false));
            List<CompetitionSeason> competitionSeason = SbdsInternalApis.getCompetitionSeasonsByQuery(query);
            if (CollectionUtils.isEmpty(competitionSeason)) continue;
            CompetitorSeasonStat cidStat = SbdsInternalApis.getCompetitorSeasonStatByCsidAndCompetitor(competitionSeason.get(0).getId(), pid, CompetitorType.PLAYER);
            if (cidStat == null) {
                cidStat = new CompetitorSeasonStat();
                cidStat.setCid(cid);
                cidStat.setCsid(competitionSeason.get(0).getId());
                cidStat.setAllowCountries(getAllowCountries());
                cidStat.setCompetitorId(pid);
                cidStat.setType(CompetitorType.PLAYER);
                cidStat.setTeamType(type);
            }
            cidStat.setStats(getStats(element, StatConstats.sodaPlayerStatPath));
            SbdsInternalApis.saveCompetitorSeasonStat(cidStat);
            LOG.warn("the current player:{} currentCsid ：{} and Stat:{} is updated", pid, competitionSeason.get(0).getId(), cidStat.toString());
        }
    }

    private Player getplayer(PlayerPageContent playerPageContent) {
        Player player = SbdsInternalApis.getPlayerBySodaId(playerPageContent.getSodaId());
        if (player != null) return player;
        player = new Player();
        player.setAllowCountries(getAllowCountries());
        player.setOnlineLanguages(getOnlineLanguage());
        player.setSodaId(playerPageContent.getSodaId());
        player.setBirthdate(playerPageContent.getBirthDate());
        player.setName(playerPageContent.getName());
        player.setEnglishName(playerPageContent.getEngLishName());
        player.setMultiLangNames(getMultiLang(playerPageContent.getName()));
        player.setWeight(playerPageContent.getWeight());
        player.setHeight(playerPageContent.getHeight());
        player.setPositionName(playerPageContent.getPositionName());
        player.setDeleted(false);
        Long id = SbdsInternalApis.savePlayer(player);
        if (id > 0) {
            player.setId(id);
            return player;
        }
        return null;
    }

    private Team getTeam(TeamType type, String sodaId, String name) {
        Team currentTeam = SbdsInternalApis.getTeamBySodaId(sodaId);
        if (currentTeam != null) return currentTeam;
        currentTeam = new Team();
        currentTeam.setAllowCountries(getAllowCountries());
        currentTeam.setOnlineLanguages(getOnlineLanguage());
        currentTeam.setSodaId(sodaId);
        currentTeam.setName(name);
        currentTeam.setMultiLangNames(getMultiLang(name));
        currentTeam.setTeamType(type);
        currentTeam.setGameFType(StatConstats.soccerID);
        currentTeam.setDeleted(false);
        Long id = SbdsInternalApis.saveTeam(currentTeam);
        if (id > 0) {
            currentTeam.setId(id);
            return currentTeam;
        }
        return null;
    }


}
