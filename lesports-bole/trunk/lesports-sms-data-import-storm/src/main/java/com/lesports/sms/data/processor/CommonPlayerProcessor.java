package com.lesports.sms.data.processor;

import com.lesports.sms.api.common.Gender;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.data.model.sportrard.SportrardConstants;
import com.lesports.sms.data.model.sportrard.SportrardPlayer;
import com.lesports.sms.data.processor.olympic.AbstractProcessor;
import com.lesports.sms.model.DictEntry;
import com.lesports.sms.model.Player;
import com.lesports.sms.model.Team;
import com.lesports.sms.model.TeamSeason;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by qiaohongxin on 2016/5/11.
 */
public abstract class CommonPlayerProcessor extends AbstractProcessor{
    private static Logger logger = LoggerFactory.getLogger(CommonPlayerProcessor.class);
    public Boolean processPlayer( SportrardPlayer player) {

        try {
            Team team1 = SbdsInternalApis.getTeamByPartnerIdAndPartnerType(player.getTeamId(), getPartnerType());
            if (null == team1) {
                logger.warn("team not found,partnerId:{}", player.getTeamId());
                return false;
            }
            List<DictEntry> dictEntries = SbdsInternalApis.getDictEntriesByName(SportrardConstants.sportsTypeMap.get(player.getSportId()));
            if (CollectionUtils.isEmpty(dictEntries)) {
                logger.warn("sporttype not found,{}", player.getSportId());
                return false;
            }
            Player player1 = SbdsInternalApis.getPlayerByPartnerIdAndPartnerType(player.getTeamId(), 469);
            if (player1 != null) return true;
            player1 = new Player();
            player1.setAllowCountries(getAllowCountries());
            String name = player.getPlayerName() == null ? player.getPlayerEnName() : player.getPlayerName();
            player1.setName(name);
            player1.setMultiLangNames(getMultiLang(name));
            player1.setHeight(Integer.parseInt(player.getHeight()));
            player1.setWeight(Integer.parseInt(player.getWeight()));
            player1.setBirthdate(player.getBirth());
            Gender gender = player1.getGender().equals("F") ? Gender.MALE : Gender.FEMALE;
            player1.setGender(gender);//男：0，女：1
            player1.setNationality(player.getNationality());
            player1.setMultiLangNationalities(getMultiLang(player.getNationality()));
            player1.setPartnerType(getPartnerType());
            player1.setGameFType(dictEntries.get(0).getId());
            player1.setPartnerId(player.getPartnerId());
            SbdsInternalApis.savePlayer(player1);
            logger.info("insert player success,team:{},playerName:{},playerPartnerId:{}", team1.getName(), player.getPlayerEnName(), player.getPartnerId());
            TeamSeason teamSeason = new TeamSeason();
            //赛季Id是需要手工配置的
            List<TeamSeason> teamSeasons = SbdsInternalApis.getTeamSeasonsByTeamIdAndCSId(team1.getId(), getCsid());
            if (CollectionUtils.isNotEmpty(teamSeasons)) {
                teamSeason = teamSeasons.get(0);
                SbdsInternalApis.addTeamPlayer(teamSeason.getId(), player1.getId(), Long.valueOf(Long.parseLong(player.getShirtName())));
            } else {
                teamSeason.setAllowCountries(getAllowCountries());
                teamSeason.setCsid(getCsid());
                teamSeason.setTid(team1.getId());
                SbdsInternalApis.saveTeamSeason(teamSeason);
                SbdsInternalApis.addTeamPlayer(teamSeason.getId(), player1.getId(), Long.valueOf(player.getShirtName()));
            }
            return true;

        } catch (Exception e) {
            logger.error("process player error", e);
            return false;
        }

    }
    public abstract Integer getPartnerType();
    public abstract Long getCsid();
}
