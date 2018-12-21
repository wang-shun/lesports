package com.lesports.sms.data.job;

import com.google.common.collect.Lists;
import com.lesports.sms.client.SopsInternalApis;
import com.lesports.sms.data.model.Constants;
import com.lesports.sms.data.service.soda.*;
import com.lesports.sms.data.util.MD5Util;
import com.lesports.sms.model.DataImportConfig;
import com.lesports.utils.LeProperties;
import org.apache.commons.collections.CollectionUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Created by ruiyuansheng on 2016/2/23.
 */
@Service("sodaDataJob")
public class SodaDataJob {


    private static Logger logger = LoggerFactory.getLogger(SodaDataJob.class);
    private final String SODA_MATCH_FILE_PATH = LeProperties.getString("soda.match.file.path");
    private final String SODA_MATCH_FILE_NAMES = LeProperties.getString("soda.match.file.names");
    private final String SODA_MATCH__STAT_FILE_PATH = LeProperties.getString("soda.match.stat.file.path");
    private final String SODA_MATCH_STAT_FILE_NAMES = LeProperties.getString("soda.match.stat.file.names");
    private final String SODA_RANKING_FILE_PATH = LeProperties.getString("soda.ranking.file.path");
    private final String SODA_RANKING_FILE_NAMES = LeProperties.getString("soda.ranking.file.names");
    private final String SODA_PLAYER_GOAL_FILE_PATH = LeProperties.getString("soda.player.goal.file.path");
    private final String SODA_PLAYER_GOAL_FILE_NAMES = LeProperties.getString("soda.player.goal.file.names");
    private final String SODA_PLAYER_ASSIST_FILE_PATH = LeProperties.getString("soda.player.assist.file.path");
    private final String SODA_PLAYER_ASSIST_FILE_NAMES = LeProperties.getString("soda.player.assist.file.names");

    private final String SODA_FIXTURES_FILE_PATH = LeProperties.getString("soda.csl.fixtures.file.path");
    ;
    private final String SODA_FIXTURES_FILE_NAMES = LeProperties.getString("soda.csl.fixtures.file.name");
    ;

    private final String SODA_MATCH_RESULT_FILE_PATH = LeProperties.getString("soda.csl.matchresult.file.path");
    ;
    private final String SODA_TIME_LINE_FILE_PATH = LeProperties.getString("soda.csl.timeline.file.path");
    ;

    @Resource
    private SodaMatchParser sodaMatchParser;

    @Resource
    private SodaTeamParser sodaTeamParser;

    @Resource
    private SodaLineupParser sodaLineupParser;

    @Resource
    private SodaRankingParser sodaRankingParser;

    @Resource
    private SodaHistoryMatchParser sodaHistoryMatchParser;

    @Resource
    private SodaTopPlayerGoalParser sodaTopPlayerGoalParser;

    @Resource
    private SodaAssistParser sodaAssistParser;

    @Resource
    private SodaPlayerParser sodaPlayerParser;

    @Resource
    private SodaMatchResultParser sodaMatchResultParser;

    @Resource
    private SodaTimeLineParser sodaTimeLineParser;


    /**
     * 导入赛程
     */
    public void importSodaMatch() {
        logger.info("【import soda match】 start");
        String name = SODA_MATCH_FILE_NAMES;
        String[] fileNames = name.split("\\|");
        if (null != fileNames) {
            for (String fileName : fileNames) {
                String filePath = SODA_MATCH_FILE_PATH + fileName;
                if (compareMD5(filePath)) continue;
                String md5New = MD5Util.fileMd5(new File(filePath));
                sodaMatchParser.parseData(filePath);
                saveMD5(filePath, md5New);
            }
        }
        logger.info("【import soda match】 end");
    }


    /**
     * 导入历史数据
     */
    public void importSodaConfrontation() {
        logger.info("【import soda Confrontation】 start");
        String filePath = SODA_MATCH__STAT_FILE_PATH ;
        File historyMatchFile = new File(filePath);
        File[] files = historyMatchFile.listFiles();
        if (null != files) {
            for (File file : files) {
                String matchStatFilePath = file.getAbsolutePath();
                if (compareMD5(matchStatFilePath)) continue;
                String md5New = MD5Util.fileMd5(new File(matchStatFilePath));
                sodaHistoryMatchParser.parseData(matchStatFilePath);
                saveMD5(matchStatFilePath, md5New);
            }
        }
        logger.info("【import soda Confrontation】 end");
    }

    /**
     * 积分榜
     */
    public void importSodaRanking() {
        logger.info("【import soda ranking】 start");
        String name = SODA_RANKING_FILE_NAMES;
        String[] fileNames = name.split("\\|");
        if (null != fileNames) {
            for (String fileName : fileNames) {
                String filePath = SODA_RANKING_FILE_PATH + fileName;
                if (compareMD5(filePath)) continue;
                String md5New = MD5Util.fileMd5(new File(filePath));
                sodaRankingParser.parseData(filePath);
                saveMD5(filePath, md5New);

            }
        }
        logger.info("【import soda ranking】 end");
    }

    /**
     * 射手榜
     */
    public void importSodaTopPlayerGoalParser() {
        logger.info("【import soda SodaTopPlayerGoal】 start");
        String name = SODA_PLAYER_GOAL_FILE_NAMES;
        String[] fileNames = name.split("\\|");
        if (null != fileNames) {
            for (String fileName : fileNames) {
                String filePath = SODA_PLAYER_GOAL_FILE_PATH + fileName;
                if (compareMD5(filePath)) continue;
                String md5New = MD5Util.fileMd5(new File(filePath));
                sodaTopPlayerGoalParser.parseData(filePath);
                saveMD5(filePath, md5New);

            }
        }
        logger.info("【import soda SodaTopPlayerGoal】 end");
    }

    /**
     * 助攻榜
     */
    public void importSodaAssistParser() {
        logger.info("【import soda SodaTopPlayerAssist】 start");
        String name = SODA_PLAYER_ASSIST_FILE_NAMES;
        String[] fileNames = name.split("\\|");
        if (null != fileNames) {
            for (String fileName : fileNames) {
                String filePath = SODA_PLAYER_ASSIST_FILE_PATH + fileName;
                if (compareMD5(filePath)) continue;
                String md5New = MD5Util.fileMd5(new File(filePath));
                sodaAssistParser.parseData(filePath);
                saveMD5(filePath, md5New);

            }
        }
        logger.info("【import soda SodaTopPlayerAssist】 end");
    }

    /**
     * 导入球员信息
     */
    public void importSodaPlayer() {
        logger.info("【import soda Player】 start");

        logger.info("【import soda Player】 end");
    }

    /**
     * 导入球队信息
     */
    public void importSodaTeam() {
        logger.info("【import soda team】 start");


        logger.info("【import soda team】 end");
    }

    /**
     * 导入出场球员
     */
    public void importSodaLineup() {
        logger.info("【import soda lineup】 start");

        logger.info("【import soda lineup】 end");
    }

    /**
     * 导入实时统计
     */
    public void importSodaMatchResult() {
        logger.info("【import soda matchResult】 begin");
//        Date date = LeDateUtils.parseYYYYMMDD("20160311");
        Date date = new Date();
        String filePath = SODA_FIXTURES_FILE_PATH + SODA_FIXTURES_FILE_NAMES;
        //当前需要处理的比赛sodaId
//        logger.info("filePath:{}",filePath);
        List<String> ids = getFixtures(date, filePath);
//        logger.info("ids:{}" ,ids);
        if (CollectionUtils.isNotEmpty(ids)) {
            for (String id : ids) {
                String name = "s9-282-2016-" + id + "-matchresult.xml";
                filePath = SODA_MATCH_RESULT_FILE_PATH + name;
//                logger.info("result filePath:{}", filePath);
                if (compareMD5(filePath)) continue;
                String md5New = MD5Util.fileMd5(new File(filePath));
                sodaMatchResultParser.parseData(filePath);
                saveMD5(filePath, md5New);
            }
        }
        logger.info("【import soda matchResult】 end");
    }

    /**
     * 导入关键事件
     */
    public void importSodaTimeLine() {
        logger.info("【import soda timeLine】 start");
//        Date date = LeDateUtils.parseYYYYMMDD("20160311");
        Date date = new Date();
        String filePath = SODA_FIXTURES_FILE_PATH + SODA_FIXTURES_FILE_NAMES;
        //当前需要处理的比赛sodaId
//        logger.info("filePath:{}",filePath);
        List<String> ids = getFixtures(date, filePath);
//        logger.info("ids:{}" , ids);
        if (CollectionUtils.isNotEmpty(ids)) {
            for (String id : ids) {
                String name = "s8-282-2016-" + id + "-timeline.xml";
                filePath = SODA_TIME_LINE_FILE_PATH + name;
//                logger.info("result filePath:{}", filePath);
                if (compareMD5(filePath)) continue;
                String md5New = MD5Util.fileMd5(new File(filePath));
                sodaTimeLineParser.parseData(filePath);
                saveMD5(filePath, md5New);
            }
        }
        logger.info("【import soda timeLine】 end");
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

    //获取当天开赛的比赛的sodaId
    public List<String> getFixtures(Date date, String file) {
        List<String> ids = Lists.newArrayList();
        try {
            File xmlFile = new File(file);
            if (!xmlFile.exists()) {
                logger.warn("parsing the file:{} not exists", file);
                return ids;
            }

            SAXReader reader = new SAXReader();
            Document document = reader.read(xmlFile);
            Element rootElement = document.getRootElement();

            Element competitionElement = rootElement.element("Competition");
            Iterator<Element> matchIterator = competitionElement.elementIterator("MatchData");
            while (matchIterator.hasNext()) {
                Element matchElement = matchIterator.next();
                //未开赛的
                if ("1".equals(matchElement.attributeValue("periodId"))) {
                    continue;
                }

                //不是今天结束的
//                if("8".equals(matchElement.attributeValue("periodId"))){
//                    String matchDate = matchElement.elementText("Date");
//                    if(!matchDate.startsWith(LeDateUtils.formatYYYY_MM_DD(date))){
//                        continue;
//                    }
//                }

                //今天比赛中和已结束的match
                ids.add(matchElement.attributeValue("id"));
            }

        } catch (Exception e) {
            logger.error("getFixtures error:{} ", e);
        }
        return ids;
    }

    public static void main(String[] args) {
        String s1 = LeProperties.getString("soda.csl.fixtures.file.path");
        ;
        String s2 = LeProperties.getString("soda.csl.fixtures.file.name");
        ;
        String filePath = s1 + s2;
        System.out.println("filePath: " + filePath);
    }

}
