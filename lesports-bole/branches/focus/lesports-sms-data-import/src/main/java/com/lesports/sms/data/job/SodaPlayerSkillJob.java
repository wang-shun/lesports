package com.lesports.sms.data.job;

import com.lesports.sms.api.common.PlayerType;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.client.SopsInternalApis;
import com.lesports.sms.data.model.Constants;
import com.lesports.sms.data.service.soda.SodaPlayerSkillParser;
import com.lesports.sms.data.util.MD5Util;
import com.lesports.sms.model.*;
import com.lesports.utils.LeProperties;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.io.File;
import java.util.List;

/**
 * Created by zhonglin on 2016/3/9.
 */
public class SodaPlayerSkillJob {

    private static Logger logger = LoggerFactory.getLogger(SodaPlayerSkillJob.class);

    private final String SODA_PLAYER_SKILL_FILE_PATH = LeProperties.getString("soda.player.skill.file.path");
    private final String SODA_TEAM_SKILL_FILE_NAMES = LeProperties.getString("soda.team.skill.file.names");
    //中超赛事id
    public static final Long CSL_COMPETITION_ID = 47001L;

    @Resource
    private SodaPlayerSkillParser sodaPlayerSkillParser;

    /**
     * 导入球员技术统计信息
     */
    public void importSodaPlayerSkill() {
        logger.info("【import soda player skill】 start");
        String name = SODA_TEAM_SKILL_FILE_NAMES;
        String[] teamIds = name.split("\\|");

        //根据赛事id获取最新的赛季id
        CompetitionSeason competitionSeason = SbdsInternalApis.getLatestCompetitionSeasonByCid(CSL_COMPETITION_ID);
        if (competitionSeason == null) {
            logger.info("import soda player skill tCompetitionSeason is null,cid = {}", CSL_COMPETITION_ID);
//            return;
        }
        else{
            competitionSeason = new CompetitionSeason();
            competitionSeason.setId(100538002L);
        }

        for (String teamId : teamIds) {
            Team team = SbdsInternalApis.getTeamBySodaId(teamId);
            if (team == null) {
                logger.info("import soda player skill team is null,teamId  = {}", teamId);
            }
            //获取每个队的赛季阵容
            List<TeamSeason> teamSeasons = SbdsInternalApis.getTeamSeasonsByTeamIdAndCSId(team.getId(), competitionSeason.getId());
            TeamSeason teamSeason = null;
            if (CollectionUtils.isNotEmpty(teamSeasons)) teamSeason = teamSeasons.get(0);
            if (teamSeason == null) {
                logger.info("import soda player skill tTeamSeason is null,teamId  = {}", teamId);
                continue;
            }
            List<TeamSeason.TeamPlayer> teamPlayers = teamSeason.getPlayers();

            if (CollectionUtils.isNotEmpty(teamPlayers)) {
                for (TeamSeason.TeamPlayer teamPlayer : teamPlayers) {
                    Player player = SbdsInternalApis.getPlayerById(teamPlayer.getPid());
                    if (player == null || player.getSodaId() == null || !player.getType().equals(PlayerType.PLAYER)) {
                        logger.info("import soda player skill player is error,playerId  = {}", player.getId());
                        continue;
                    }
                    String filePath = SODA_PLAYER_SKILL_FILE_PATH + "t316-player-skill-" + player.getSodaId() + ".xml";
                    if (compareMD5(filePath)) continue;
                    String md5New = MD5Util.fileMd5(new File(filePath));
                    sodaPlayerSkillParser.parseData(filePath);
                    saveMD5(filePath, md5New);
                }
            }
        }
        logger.info("【import soda player skill】 end");
    }

    /**
     * 导入旧的球员技术统计信息
     */
    public void importOldSodaPlayerSkill() {
        logger.info("【import soda player skill】 start");
        String name = SODA_TEAM_SKILL_FILE_NAMES;
        String[] teamIds = name.split("\\|");

        //根据赛事id获取最新的赛季id
        CompetitionSeason competitionSeason = SbdsInternalApis.getLatestCompetitionSeasonByCid(CSL_COMPETITION_ID);
        if (competitionSeason == null) {
            return;
        }

        for (String teamId : teamIds) {
            Team team = SbdsInternalApis.getTeamBySodaId(teamId);
            //获取每个队的赛季阵容
            List<TeamSeason> teamSeasons = SbdsInternalApis.getTeamSeasonsByTeamIdAndCSId(team.getId(), competitionSeason.getId());
            TeamSeason teamSeason = null;
            if (CollectionUtils.isNotEmpty(teamSeasons)) teamSeason = teamSeasons.get(0);
            if (teamSeason == null) continue;
            List<TeamSeason.TeamPlayer> teamPlayers = teamSeason.getPlayers();

            if (CollectionUtils.isNotEmpty(teamPlayers)) {
                for (TeamSeason.TeamPlayer teamPlayer : teamPlayers) {
                    for (int i = 2013; i < 2016; i++) {
                        Player player = SbdsInternalApis.getPlayerById(teamPlayer.getPid());
                        if (player == null || player.getSodaId() == null || !player.getType().equals(PlayerType.PLAYER))
                            continue;
                        String path = SODA_PLAYER_SKILL_FILE_PATH;
                        path.replace("2016", i + "");
                        String filePath = path + "t316-player-skill-" + player.getSodaId() + ".xml";
                        if (compareMD5(filePath)) continue;
                        String md5New = MD5Util.fileMd5(new File(filePath));
                        sodaPlayerSkillParser.parseData(filePath);
                        saveMD5(filePath, md5New);
                    }
                }
            }
        }
        logger.info("【import soda player skill】 end");
    }

    /**
     * 比较md5值，若无记录或不同返回false
     *
     * @param filePath
     * @return
     */
    private Boolean compareMD5(String filePath) {
        File file = new File(filePath);
        String md5New = MD5Util.fileMd5(file);
        if (null == md5New) return true;
        List<DataImportConfig> dataImportConfigList = SopsInternalApis.getDataImportConfigsBybyFileNameAndpartnerType(file.getName(), Constants.SODAOartnerSource);
        if (CollectionUtils.isEmpty(dataImportConfigList)) return false;
        DataImportConfig dataImportConfig = dataImportConfigList.get(0);
        return md5New.equals(dataImportConfig.getMd5());
    }

    private void saveMD5(String filePath, String md5New) {
        File file = new File(filePath);
        List<DataImportConfig> dataImportConfigList = SopsInternalApis.getDataImportConfigsBybyFileNameAndpartnerType(file.getName(), Constants.SODAOartnerSource);

        if (CollectionUtils.isNotEmpty(dataImportConfigList)) {
            DataImportConfig dataImportConfig = dataImportConfigList.get(0);
            dataImportConfig.setMd5(md5New);
            SopsInternalApis.saveDataImportConfig(dataImportConfig);
        } else {
            DataImportConfig dataImportConfig = new DataImportConfig();
            dataImportConfig.setFileName(file.getName());
            dataImportConfig.setPartnerType(Constants.SODAOartnerSource);
            dataImportConfig.setMd5(md5New);
            SopsInternalApis.saveDataImportConfig(dataImportConfig);
        }
    }
}
