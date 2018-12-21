package com.lesports.sms.data.job;

import com.lesports.query.InternalCriteria;
import com.lesports.query.InternalQuery;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.client.SopsInternalApis;
import com.lesports.sms.data.model.Constants;
import com.lesports.sms.data.service.stats.*;
import com.lesports.sms.data.service.stats.euro.EUROLiveParser;
import com.lesports.sms.data.util.FtpUtil;
import com.lesports.sms.data.util.HttpUtils;
import com.lesports.sms.data.util.MD5Util;
import com.lesports.sms.model.DataImportConfig;
import com.lesports.sms.model.Match;
import com.lesports.utils.LeDateUtils;
import com.lesports.utils.PageUtils;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
 * Created by zhonglin on 2016/6/4.
 */
@Service("euroJob")
public class EuroJob {
    private static Logger logger = LoggerFactory.getLogger(EuroJob.class);
    private static List<String> LiveEureoMatchsBoxScoreFiles = Collections.synchronizedList(new ArrayList<String>());
    /**
     * 获取当前时间日期所对应的赛程信息
     */
    public void getEuroCurrentValidMatchs() {
        logger.info("begin delete the datas in the list");
        if (!LiveEureoMatchsBoxScoreFiles.isEmpty()) {
            LiveEureoMatchsBoxScoreFiles.clear();
            logger.info(" delete the datas in the list");
        }
        logger.info("get the current valid  euro matchs data");
        Date date = new Date();
        DateFormat format = new SimpleDateFormat("yyyyMMdd");
        String time = format.format(date);
        Pageable pageable = PageUtils.createPageable(0, 60);
        InternalQuery internalQuery = new InternalQuery();
        internalQuery.addCriteria(new InternalCriteria("start_date", "eq", time));
        internalQuery.addCriteria(new InternalCriteria("deleted", "eq", false));
        internalQuery.addCriteria(InternalCriteria.where("csid").is(100794002L));
        internalQuery.setPageable(pageable);
        internalQuery.setSort(new Sort(Sort.Direction.ASC, "begin_time"));
        List<Match> currentValidmatchs = SbdsInternalApis.getMatchsByQuery(internalQuery);

//        List<Match> currentValidmatchs = SbdsInternalApis.getMatchByDateAndStatusAndPartnerType(time, "MATCH_END", Constants.PartnerSourceStaticId);
        if (currentValidmatchs == null || currentValidmatchs.isEmpty()) {
            logger.warn("there are no match on this day");
            return;
        }
        for (Match match : currentValidmatchs) {
            if (match.getStatsCode() == null) {
                logger.warn("the match " + match.getId() + " doesnot have the statsCode");
                continue;
            }
            String euroBoxscoreFileName = "EURO_BOXSCORE_GAME$" + match.getStatsCode() + ".XML";
            LiveEureoMatchsBoxScoreFiles.add(euroBoxscoreFileName);
            logger.info("the right live file is added" + match.getId());
        }
    }

    /**
     * 下载解析直播数据
     */
    public void importMatchBoxscoreData() {
        logger.info("import euro stats boxscore Live start");
        Date date = new Date();
        DateFormat format = new SimpleDateFormat("yyyyMMdd");
        String time = format.format(date);
        Pageable pageable = PageUtils.createPageable(0, 20);
        InternalQuery internalQuery = new InternalQuery();
        internalQuery.addCriteria(new InternalCriteria("start_date", "eq", time));
        internalQuery.addCriteria(new InternalCriteria("deleted", "eq", false));
        internalQuery.addCriteria(InternalCriteria.where("csid").is(100794002L));
        internalQuery.setPageable(pageable);
        internalQuery.setSort(new Sort(Sort.Direction.ASC, "begin_time"));
        List<Match> currentValidmatchs = SbdsInternalApis.getMatchsByQuery(internalQuery);
        if (currentValidmatchs.isEmpty()) {
            logger.warn("no match here");
            return;
        }
        for (Match match : currentValidmatchs) {
            String localFilePath = Constants.localDownloadPath + "EURO_BOXSCORE_GAME$" + match.getStatsCode() + ".XML";
            String downLoadFileUrl = Constants.statsDownloadUrl + "EURO_BOXSCORE_GAME$" + match.getStatsCode() + ".XML";
            try {
//                boolean result = HttpUtils.downloadFile(downLoadFileUrl, localFilePath);
//                if (!result){
//                    logger.error("file download fail" + downLoadFileUrl);
//                    continue;
//                }
            } catch (Exception e) {
                logger.error("file download fail" + e);
                continue;
            }
            logger.info("euro localFilePath: " + localFilePath);
//            if (compareMD5(localFilePath)) continue;
//            euroLiveParser.parseData(localFilePath);
            String md5New = MD5Util.fileMd5(new File(localFilePath));
            saveMD5(localFilePath, md5New);
            logger.info("import euro live matchdata sucessfully" + localFilePath);
        }
    }



    /**
     * 下载解析赛程所对应的数据文件
     */
    public void importMatchScheduleData() {
        logger.info("import euro match schedule data");
        if (Constants.euroScheduleFiles == null) {
            logger.warn("match files are not existing");
            return;
        }
        String localFilePath = Constants.localDownloadPath + Constants.euroScheduleFiles;
        String downLoadFileUrl = Constants.statsDownloadUrl + Constants.euroScheduleFiles;
        try {
//            boolean result = HttpUtils.downloadFile(downLoadFileUrl, localFilePath);
//            if (!result) return;
//            logger.info("file is download sucessful" + localFilePath);
        } catch (Exception e) {
            logger.error("file download fail", e);
            return;
        }
//        if (compareMD5(localFilePath)) return;
//        euroMatchParser.parseData(localFilePath);
//        String md5New = MD5Util.fileMd5(new File(localFilePath));
//        saveMD5(localFilePath, md5New);
        logger.info("parse euro  match schedule  data sucessfully");
    }

    /**
     * 下载解析球队榜单所对应的数据文件
     */
    public void importTeamRankingData() {
        logger.info("import euro match schedule data");
        if (Constants.euroTeamRankingFiles == null ) {
            logger.warn("match files are not existing");
            return;
        }
        String localFilePath = Constants.localDownloadPath + Constants.euroTeamRankingFiles;
        String downLoadFileUrl = Constants.statsDownloadUrl + Constants.euroTeamRankingFiles;
        try {
//            boolean result = HttpUtils.downloadFile(downLoadFileUrl, localFilePath);
//            if (!result) return;
//            logger.info("file is download sucessful" + localFilePath);
        } catch (Exception e) {
            logger.error("file download fail" + e);
            return;
        }
//        if (compareMD5(localFilePath)) return;
//        teamStandingsParser.parseData(localFilePath);
//        String md5New = MD5Util.fileMd5(new File(localFilePath));
//        saveMD5(localFilePath, md5New);
        logger.info("parse team ranking data sucessfully");
    }

    /**
     * 下载解析球员榜单所对应的数据文件
     */
    public void importPlayerRankingData() {
        logger.info("import euro player ranking data");
        if (Constants.euroPlayerRankingFiles == null) {
            logger.warn("match files are not existing");
            return;
        }
        String localFilePath = Constants.localDownloadPath + Constants.euroPlayerRankingFiles;
        String downLoadFileUrl = Constants.statsDownloadUrl + Constants.euroPlayerRankingFiles;
        try {
//            boolean result = HttpUtils.downloadFile(downLoadFileUrl, localFilePath);
//            if (!result) return;
//            logger.info("file is download sucessful" + localFilePath);
        } catch (Exception e) {
            logger.error("file download fail" + e);
            return;
        }
//        if (compareMD5(localFilePath)) return;
//        euroPlayerStandingsParser.parseData(localFilePath);
//        String md5New = MD5Util.fileMd5(new File(localFilePath));
//        saveMD5(localFilePath, md5New);
        logger.info("parse euro player ranking data sucessfully");
    }

    /**
     * 下载解析球员球队文件
     */
    public void updateTeamAndPlayersData() {
        logger.info("update euro team and players data");
        if (Constants.euroRosterFiles == null) {
            logger.warn("definate files of roster are not existing");
            return;
        }
        String localFilePath = Constants.localDownloadPath + Constants.euroRosterFiles;
        String downLoadFileUrl = Constants.statsDownloadUrl + Constants.euroRosterFiles;
        try {
//            boolean result = HttpUtils.downloadFile(downLoadFileUrl, localFilePath);
//            if (!result) return;
//            logger.info("file is download sucessful" + localFilePath);
        } catch (Exception e) {
            logger.error("file download fail" + e);
            return;
        }
//        if (compareMD5(localFilePath)) return;
//        euroTeamAndPlayerParser.parseData(localFilePath);
//        String md5New = MD5Util.fileMd5(new File(localFilePath));
//        saveMD5(localFilePath, md5New);
        logger.info("update euro team and players sucessfully");
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
