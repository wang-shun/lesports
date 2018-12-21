package com.lesports.sms.data.job;

import com.google.common.collect.Lists;
import com.lesports.query.InternalCriteria;
import com.lesports.query.InternalQuery;
import com.lesports.sms.api.common.MatchStatus;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.client.SopsInternalApis;
import com.lesports.sms.data.model.Constants;
import com.lesports.sms.data.service.soda.top12.WarningMailUtil;
import com.lesports.sms.data.service.stats.*;
import com.lesports.sms.data.service.stats.cba.*;
import com.lesports.sms.data.util.FtpUtil;
import com.lesports.sms.data.util.HttpUtils;
import com.lesports.sms.data.util.MD5Util;
import com.lesports.sms.model.DataImportConfig;
import com.lesports.sms.model.Match;
import com.lesports.utils.LeDateUtils;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by qiaohongxin on 2015/10/9.
 */
@Service("nbaMatchLiveDataHandleJob")
public class NBAMatchLiveDataHandleJob {
    private static Logger logger = LoggerFactory.getLogger(NBAMatchLiveDataHandleJob.class);
    private static List<String> LiveMatchsBoxScoreFiles = Collections.synchronizedList(new ArrayList<String>());
    private static List<String> LiveMatchsPBPEventFiles = Collections.synchronizedList(new ArrayList<String>());
    @Resource
    private NBAMatchParser matchParser;
    @Resource
    private NBATeamStandingsParser teamStandingsParser;
    @Resource
    private NBALiveParser liveParser;
    @Resource
    private PBPLiveEventParser liveEventParser;
    @Resource
    NBAPlayerStandingsParser playerStandingsParser;
    @Resource
    NBATeamAndPlayerParser nbaTeamAndPlayerParser;
    @Resource
    WarningMailUtil warningMailUtil;
    @Resource
    NBATeamStatsParser nbaTeamStatsParser;
    @Resource
    NBAPlayerStatsParser nbaPlayerStatsParser;
    @Resource
    NBAFullPlayerStandingParser nbaFullPlayerStandingParser;
    @Resource
    StatsTeamStandingsParser statsTeamStandingsParser;
    @Resource
    StatsLiveParser statsLiveParser;
    @Resource
    StatsMatchParser statsMatchParser;
    @Resource
    StatsTeamAndPlayerParser statsTeamAndPlayerParser;
    @Resource
    StatsTeamStatsParser statsTeamStatsParser;
    @Resource
    StatsTop20PlayerStandingsParser statsTop20PlayerStandingsParser;
    @Resource
    StatsPlayerStatsParser statsPlayerStatsParser;


    /**
     * 获取比赛已结束数据文件
     */
    public void importMissesedMatchs() {
        logger.info("begin delete the datas in the list");
        if (!LiveMatchsPBPEventFiles.isEmpty()) {
            LiveMatchsPBPEventFiles.clear();
            logger.info(" delete the datas in the list");
        }
        if (!LiveMatchsBoxScoreFiles.isEmpty()) {
            LiveMatchsBoxScoreFiles.clear();
            logger.info(" delete the datas in the list");
        }
        logger.info("get the current valid  nba matchs data");
        List<Match> currentValidmatchs = SbdsInternalApis.getMatchByStatusAndPartnerType("MATCH_END", Constants.PartnerSourceStaticId);
        if (currentValidmatchs == null || currentValidmatchs.isEmpty()) {
            logger.warn("there are no match on this day");
            return;
        }
        for (Match match : currentValidmatchs) {
            if (match.getPartnerCode() == null) {
                logger.warn("the match" + match.getId() + "doesnot have the partnercode");
                continue;
            }
            String nbaBoxscoreFileName = "NBA_BOXSCORE_GAME$" + match.getPartnerCode() + ".XML";
            String nbaPbpFileName = "NBA_PBP_GAME$" + match.getPartnerCode() + ".XML";
            if (match.getStage().longValue() == 100083000L) {
                nbaBoxscoreFileName = "NBA_BOXSCORE_GAME_PRE$" + match.getPartnerCode() + ".XML";
                nbaPbpFileName = "NBA_PBP_GAME_PRE$" + match.getPartnerCode() + ".XML";
            }
            LiveMatchsBoxScoreFiles.add(nbaBoxscoreFileName);
            LiveMatchsPBPEventFiles.add(nbaPbpFileName);
            logger.info("the right live file is added" + match.getId());
        }
    }

    /**
     * 获取当前时间日期所对应的赛程信息
     */

    public void getCurrentValidMatchs() {
        logger.info("begin delete the datas in the list");
        if (!LiveMatchsPBPEventFiles.isEmpty()) {
            LiveMatchsPBPEventFiles.clear();
            logger.info(" delete the datas in the list");
        }
        if (!LiveMatchsBoxScoreFiles.isEmpty()) {
            LiveMatchsBoxScoreFiles.clear();
            logger.info(" delete the datas in the list");
        }
        logger.info("get the current valid  nba matchs data");
        Date date = new Date();
        DateFormat format = new SimpleDateFormat("yyyyMMdd");
        String time = format.format(date);
        List<Match> currentValidmatchs = SbdsInternalApis.getMatchByDateAndStatusAndPartnerType(time, "MATCH_END", Constants.PartnerSourceStaticId);
        if (currentValidmatchs == null || currentValidmatchs.isEmpty()) {
            logger.warn("there are no match on this day");
            return;
        }
        for (Match match : currentValidmatchs) {
            if (match.getPartnerCode() == null) {
                logger.warn("the match" + match.getId() + "doesnot have the partnercode");
                continue;
            }
            String boxscoreFileName = "";
            String pbpFileName = "";
            if (match.getCid().longValue() == Constants.statsNameMap.get("NBA") && match.getStage().longValue() == 100083000L) {
                boxscoreFileName = "NBA_BOXSCORE_GAME_PRE$" + match.getPartnerCode() + ".XML";
                pbpFileName = "NBA_PBP_GAME_PRE$" + match.getPartnerCode() + ".XML";
            } else if (match.getCid().longValue() == Constants.statsNameMap.get("CBACHN")) {
                boxscoreFileName = "cba/CBACHN_BOXSCORE_GAME$" + match.getPartnerCode() + ".XML";
                pbpFileName = "cba/CBACHN__PBP_GAME$" + match.getPartnerCode() + ".XML";
            } else {
                boxscoreFileName = "NBA_BOXSCORE_GAME$" + match.getPartnerCode() + ".XML";
                pbpFileName = "NBA_PBP_GAME$" + match.getPartnerCode() + ".XML";
            }
            LiveMatchsBoxScoreFiles.add(boxscoreFileName);
            LiveMatchsPBPEventFiles.add(pbpFileName);
            logger.info("the right live file is added" + match.getId());
        }
    }

    /**
     * 下载解析直播数据
     */
    public void importMatchBoxscoreData() {
        logger.info("import nba stats boxscore Live start");
        if (LiveMatchsBoxScoreFiles.isEmpty()) {
            logger.warn("no match here");
            return;
        }
        for (String fileName : LiveMatchsBoxScoreFiles) {
            String localFilePath = Constants.localDownloadPath + fileName;
            String downLoadFileUrl = Constants.statsDownloadUrl + fileName;
            //  boolean result = downloadFile(fileName, Constants.localDownloadPath);
            try {
                HttpUtils.downloadFile(downLoadFileUrl, localFilePath);
            } catch (Exception e) {
                if (e instanceof java.io.FileNotFoundException) {
                    logger.error("file can not be found on this url" + localFilePath, e);
                } else {
                    warningMailUtil.sendMai("data-import file downLoad fail", "FILE_PROVIDER:stats<br>" + "FILE_URL:" + downLoadFileUrl + "<br>" + "ERROR:" + e.toString());
                }
                logger.error("file download fail" + e);
                continue;
            }
            if (compareMD5(localFilePath)) continue;
            if (localFilePath.contains("CBA")) statsLiveParser.parseData(localFilePath);
            else {
                liveParser.parseData(localFilePath);
            }
            String md5New = MD5Util.fileMd5(new File(localFilePath));
            saveMD5(localFilePath, md5New);
            logger.info("import nba live matchdata sucessfully" + localFilePath);
        }
    }

    /**
     * 下载解析直播事件数据
     */
    public void importMatchPBPEventData() {
        logger.info("import nbaLive start");
        if (LiveMatchsPBPEventFiles.isEmpty()) {
            logger.warn("no match here");
            return;
        }
        for (String fileName : LiveMatchsPBPEventFiles) {
            String localFilePath = Constants.localDownloadPath + fileName;
            String downLoadFileUrl = Constants.statsDownloadUrl + fileName;
            try {
                HttpUtils.downloadFile(downLoadFileUrl, localFilePath);
            } catch (Exception e) {
                if (e instanceof java.io.FileNotFoundException) {
                    logger.error("file can not be found on this url" + localFilePath, e);
                } else {
                    warningMailUtil.sendMai("data-import file downLoad fail", "FILE_PROVIDER:stats<br>" + "FILE_URL:" + downLoadFileUrl + "<br>" + "ERROR:" + e.toString());
                }
                logger.error("file download fail" + e);
                continue;
            }
            if (compareMD5(localFilePath)) continue;
            if (localFilePath.contains("CBA")) continue;//filter the cba pbp
            liveEventParser.parseData(localFilePath);
            String md5New = MD5Util.fileMd5(new File(localFilePath));
            saveMD5(localFilePath, md5New);
            logger.info("import nba livee match pbp event data sucessfully" + localFilePath);
        }
    }

    /**
     * 下载解析赛程所对应的数据文件
     */
    public void importMatchScheduleData() {
        logger.info("import nba match schedule data");
        if (CollectionUtils.isEmpty(Constants.statsScheduleFiles)) {
            logger.warn("match files are not existing");
            return;
        }
        for (String fileName : Constants.statsScheduleFiles) {
            String localFilePath = Constants.localDownloadPath + fileName;
            String downLoadFileUrl = Constants.statsDownloadUrl + fileName;
            try {
                HttpUtils.downloadFile(downLoadFileUrl, localFilePath);
            } catch (Exception e) {
                if (e instanceof java.io.FileNotFoundException) {
                    logger.error("file can not be found on this url" + localFilePath, e);
                } else {
                    warningMailUtil.sendMai("data-import file downLoad fail", "FILE_PROVIDER:stats<br>" + "FILE_URL:" + downLoadFileUrl + "<br>" + "ERROR:" + e.toString());
                }
                logger.error("file download fail" + e);
                continue;
            }
            if (compareMD5(localFilePath)) continue;
            if (localFilePath.contains("CBA")) statsMatchParser.parseData(localFilePath);
            else {
                matchParser.parseData(localFilePath);
            }
            String md5New = MD5Util.fileMd5(new File(localFilePath));
            saveMD5(localFilePath, md5New);
            logger.info("parse the file:{} data sucessfully", localFilePath);
        }
    }

    /**
     * 下载解析球队榜单所对应的数据文件
     */
    public void importTeamRankingData() {
        logger.info("import nba match schedule data");
        if (CollectionUtils.isEmpty(Constants.statsTeamRankingFiles)) {
            logger.warn("match files are not existing");
            return;
        }
        for (String fileName : Constants.statsTeamRankingFiles) {
            String localFilePath = Constants.localDownloadPath + fileName;
            String downLoadFileUrl = Constants.statsDownloadUrl + fileName;
            try {
                HttpUtils.downloadFile(downLoadFileUrl, localFilePath);
            } catch (Exception e) {
                if (e instanceof java.io.FileNotFoundException) {
                    logger.error("file can not be found on this url" + localFilePath, e);
                } else {
                    warningMailUtil.sendMai("data-import file downLoad fail", "FILE_PROVIDER:stats<br>" + "FILE_URL:" + downLoadFileUrl + "<br>" + "ERROR:" + e.toString());
                }
                logger.error("file download fail" + e);
                continue;
            }
            if (compareMD5(localFilePath)) continue;
            if (localFilePath.contains("CBA")) statsTeamStandingsParser.parseData(localFilePath);
            else {
                teamStandingsParser.parseData(localFilePath);
            }
            String md5New = MD5Util.fileMd5(new File(localFilePath));
            saveMD5(localFilePath, md5New);
            logger.info("parse the file:{} data sucessfully", localFilePath);
        }
    }

    /**
     * 下载解析球队本赛季技术统计所对应的数据文件
     */
    public void importTeamSeasonStatData() {
        logger.info("import nba team season stat data");
        if (CollectionUtils.isEmpty(Constants.stasTeamStatFiles)) {
            logger.warn("match files are not existing");
            return;
        }
        for (String fileName : Constants.stasTeamStatFiles) {
            String localFilePath = Constants.localDownloadPath + fileName;
            String downLoadFileUrl = Constants.statsDownloadUrl + fileName;
            try {
                HttpUtils.downloadFile(downLoadFileUrl, localFilePath);
            } catch (Exception e) {
                if (e instanceof java.io.FileNotFoundException) {
                    logger.error("file can not be found on this url" + localFilePath, e);
                } else {
                    warningMailUtil.sendMai("data-import file downLoad fail", "FILE_PROVIDER:stats<br>" + "FILE_URL:" + downLoadFileUrl + "<br>" + "ERROR:" + e.toString());
                }
                logger.error("file download fail" + e);
                continue;
            }
            if (compareMD5(localFilePath)) continue;
            if (localFilePath.contains("CBA")) statsTeamStatsParser.parseData(localFilePath);
            else {
                nbaTeamStatsParser.parseData(localFilePath);
            }
            String md5New = MD5Util.fileMd5(new File(localFilePath));
            saveMD5(localFilePath, md5New);
            logger.info("parse the file:{} data sucessfully", localFilePath);
        }
    }


    /**
     * 下载解析球员榜单所对应的数据文件
     */
    public void importPlayerRankingData() {
        logger.info("import nba player ranking data");
        if (CollectionUtils.isEmpty(Constants.statsTop20PlayerRankingFiles)) {
            logger.warn("match files are not existing");
            return;
        }
        for (String fileName : Constants.statsTop20PlayerRankingFiles) {
            String localFilePath = Constants.localDownloadPath + fileName;
            String downLoadFileUrl = Constants.statsDownloadUrl + fileName;
            try {
                HttpUtils.downloadFile(downLoadFileUrl, localFilePath);
            } catch (Exception e) {
                if (e instanceof java.io.FileNotFoundException) {
                    logger.error("file can not be found on this url" + localFilePath, e);
                } else {
                    warningMailUtil.sendMai("data-import file downLoad fail", "FILE_PROVIDER:stats<br>" + "FILE_URL:" + downLoadFileUrl + "<br>" + "ERROR:" + e.toString());
                }
                logger.error("file download fail" + e);
                return;
            }
            if (compareMD5(localFilePath)) continue;
            if (localFilePath.contains("CBA")) statsTop20PlayerStandingsParser.parseData(localFilePath);
            else {
                playerStandingsParser.parseData(localFilePath);
            }
            String md5New = MD5Util.fileMd5(new File(localFilePath));
            saveMD5(localFilePath, md5New);
            logger.info("parse the file:{} data sucessfully", localFilePath);
        }
    }

    /**
     * 下载解析球员榜单所对应的数据文件
     */
    public void importPlayerFullRankingData() {
        logger.info("import nba player full ranking data");
        if (Constants.nbaPlayerRankingFullFiles == null) {
            logger.warn("match files are not existing");
            return;
        }
        String localFilePath = Constants.localDownloadPath + Constants.nbaPlayerRankingFullFiles;
        String downLoadFileUrl = Constants.statsDownloadUrl + Constants.nbaPlayerRankingFullFiles;
        try {
            HttpUtils.downloadFile(downLoadFileUrl, localFilePath);
        } catch (Exception e) {
            if (e instanceof java.io.FileNotFoundException) {
                logger.error("file can not be found on this url" + localFilePath, e);
            } else {
                warningMailUtil.sendMai("data-import file downLoad fail", "FILE_PROVIDER:stats<br>" + "FILE_URL:" + downLoadFileUrl + "<br>" + "ERROR:" + e.toString());
            }
            logger.error("file download fail" + e);
            return;
        }
        if (compareMD5(localFilePath)) return;
        nbaFullPlayerStandingParser.parseData(localFilePath);
        String md5New = MD5Util.fileMd5(new File(localFilePath));
        saveMD5(localFilePath, md5New);
        logger.info("parse nba player ranking data sucessfully");
    }

    /**
     * 下载解析球员本赛季技术统计所对应的数据文件
     */
    public void importPlayerSeasonStatData() {
        logger.info("import nba player season stat data");
        if (CollectionUtils.isEmpty(Constants.statsPlayerStatFiles)) {
            logger.warn("match files are not existing");
            return;
        }
        for (String fileName : Constants.statsPlayerStatFiles) {
            String localFilePath = Constants.localDownloadPath + fileName;
            String downLoadFileUrl = Constants.statsDownloadUrl + fileName;
            try {
                HttpUtils.downloadFile(downLoadFileUrl, localFilePath);
            } catch (Exception e) {
                if (e instanceof java.io.FileNotFoundException) {
                    logger.error("file can not be found on this url" + localFilePath, e);
                } else {
                    warningMailUtil.sendMai("data-import file downLoad fail", "FILE_PROVIDER:stats<br>" + "FILE_URL:" + downLoadFileUrl + "<br>" + "ERROR:" + e.toString());
                }
                logger.error("file download fail" + e);
                continue;
            }
            if (compareMD5(localFilePath)) continue;
            if (localFilePath.contains("CBA")) statsPlayerStatsParser.parseData(localFilePath);
            else {
                nbaPlayerStatsParser.parseData(localFilePath);
            }
            String md5New = MD5Util.fileMd5(new File(localFilePath));
            saveMD5(localFilePath, md5New);
            logger.info("parse nba livee match pbp event data sucessfully");
        }
    }

    /**
     * 下载解析球员球队文件
     */
    public void updateTeamAndPlayersData() {
        logger.info("update nba team and players data");
        if (CollectionUtils.isEmpty(Constants.statsPlayersFiles)) {
            logger.warn("definate files of rester are not existing");
            return;
        }
        for (String fileName : Constants.statsPlayersFiles) {
            String localFilePath = Constants.localDownloadPath + fileName;
            String downLoadFileUrl = Constants.statsDownloadUrl + fileName;
            try {
                HttpUtils.downloadFile(downLoadFileUrl, localFilePath);
            } catch (Exception e) {
                if (e instanceof java.io.FileNotFoundException) {
                    logger.error("file can not be found on this url" + localFilePath, e);
                } else {
                    warningMailUtil.sendMai("data-import file downLoad fail", "FILE_PROVIDER:stats<br>" + "FILE_URL:" + downLoadFileUrl + "<br>" + "ERROR:" + e.toString());
                }
                logger.error("file download fail" + e);
                return;
            }
            if (compareMD5(localFilePath)) continue;
            if (localFilePath.contains("CBA")) statsTeamAndPlayerParser.parseData(localFilePath);
            else {
                nbaTeamAndPlayerParser.parseData(localFilePath);
            }
            String md5New = MD5Util.fileMd5(new File(localFilePath));
            saveMD5(localFilePath, md5New);
            logger.info("update nba team and players sucessfully");
        }
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
        List<DataImportConfig> dataImportConfigList = SopsInternalApis.getDataImportConfigsBybyFileNameAndpartnerType(file.getName(), Constants.PartnerSourceStaticId);
        if (CollectionUtils.isEmpty(dataImportConfigList)) return false;
        DataImportConfig dataImportConfig = dataImportConfigList.get(0);
        return md5New.equals(dataImportConfig.getMd5());
    }

    private void saveMD5(String filePath, String md5New) {
        File file = new File(filePath);
        List<DataImportConfig> dataImportConfigList = SopsInternalApis.getDataImportConfigsBybyFileNameAndpartnerType(file.getName(), Constants.PartnerSourceStaticId);

        if (CollectionUtils.isNotEmpty(dataImportConfigList)) {
            DataImportConfig dataImportConfig = dataImportConfigList.get(0);
            dataImportConfig.setMd5(md5New);
            dataImportConfig.setUpdateAt(LeDateUtils.formatYYYYMMDDHHMMSS(new Date()));
            SopsInternalApis.saveDataImportConfig(dataImportConfig);
        } else {
            DataImportConfig dataImportConfig = new DataImportConfig();
            dataImportConfig.setFileName(file.getName());
            dataImportConfig.setPartnerType(Constants.PartnerSourceStaticId);
            dataImportConfig.setMd5(md5New);
            dataImportConfig.setDeleted(false);
            dataImportConfig.setCreateAt(LeDateUtils.formatYYYYMMDDHHMMSS(new Date()));
            dataImportConfig.setUpdateAt(LeDateUtils.formatYYYYMMDDHHMMSS(new Date()));
            SopsInternalApis.saveDataImportConfig(dataImportConfig);
        }
    }

    private Boolean downloadFile(String fileName, String localRootPath) {
        boolean result = true;
        FtpUtil ftpUtil = new FtpUtil(Constants.LocalFtpHost, Constants.LocalFtpPort, Constants.LocalFtpUserName, Constants.LocalFtpPassword);
        try {
            if (!ftpUtil.loginFtp(60)) {
                logger.error("login ftp:{} fail", ftpUtil.getFtpName());
                return false;
            }
            boolean downloadResult = ftpUtil.downloadFile(fileName, localRootPath, "//Sport//stats//");
            if (!downloadResult) {
                logger.error("download file fail!{}", fileName);
                result = false;
            } else {
                logger.info("download file success!{}", fileName);
            }
        } catch (IOException e) {
            logger.error("{}", e);
            result = false;
        } finally {
            ftpUtil.logOutFtp();
        }
        return result;
    }
}
