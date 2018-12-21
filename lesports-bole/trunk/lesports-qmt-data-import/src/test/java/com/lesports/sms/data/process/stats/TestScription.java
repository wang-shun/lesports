package com.lesports.sms.data.process.stats;

import com.google.common.collect.Lists;
import com.hankcs.hanlp.HanLP;
import com.lesports.AbstractIntegrationTest;
import com.lesports.api.common.LangString;
import com.lesports.api.common.LanguageCode;
import com.lesports.qmt.sbd.SbdLiveEventInternalApis;
import com.lesports.qmt.sbd.model.LiveEvent;
import com.lesports.qmt.sbd.model.Partner;
import com.lesports.qmt.sbd.model.PartnerType;
import com.lesports.sms.data.Generator.impl.*;
import com.lesports.sms.data.model.commonImpl.*;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

//import org.im4java.core.GMOperation;

/**
 * Created by qiaohongxin on 2016/7/26.
 */
public class TestScription extends AbstractIntegrationTest {

    @Resource
    private DefaultScheduleGenerator scheduleGenerator;
    @Resource
    private DefaultTeamStatsGenerator teamStatsGenerator;
    @Resource
    private DefaultPlayerStatsGenerator playerStatsGenerator;
    @Resource
    private DefaultTeamSeasonGenerator teamSeasonGenerator;
    @Resource
    private DefaultPlayerStandingGenerator playerStandingGenerator;
    @Resource
    private DefaultTeamStandingGenerator teamStandingGenerator;
    @Resource
    private DefaultLiveScoreGenerator defaultLiveScoreGenerator;
    @Resource
    private DefaultLiveEventGenerator defaultLiveEventGenerator;
    @Resource
    private DefaultMatchPreviewGenerator defaultMatchPreviewGenerator;

    @Test
    public void testSchedulePreview() {
        defaultMatchPreviewGenerator.nextProcessor();
    }

    @Test
    public void testSchedule() {
        scheduleGenerator.nextProcessor();
    }

    @Test
    public void testTeamStats() {
        teamStatsGenerator.nextProcessor();
    }

    @Test
    public void testTeamStandings() {
        teamStandingGenerator.nextProcessor();
    }

    @Test
    public void testPlayerStats() {
        playerStatsGenerator.nextProcessor();
    }

    @Test
    public void testTeamSeason() {
        teamSeasonGenerator.nextProcessor();
    }

    @Test
    public void testPlayerStanding() {
        playerStandingGenerator.nextProcessor();
    }

    @Test
    public void testLiveScore() {
        defaultLiveScoreGenerator.nextProcessor();
    }

    @Test
    public void testLiveEvent() {
        defaultLiveEventGenerator.nextProcessor();
    }

    @Test
    public void sportrarsImport() {
        int count = 0;
        try {
            // 创建可读入的Excel工作簿
            File file = new File("D:\\test.xlsx");//根据文件名创建一个文件对象
            InputStream is = new FileInputStream(file);
            XSSFWorkbook xssfWorkbook = new XSSFWorkbook(is);
            XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(0);
            int rows = xssfSheet.getLastRowNum();//得到所有的行
            for (int i = 1; i < rows; i++) {
                XSSFRow xssfRow = xssfSheet.getRow(i);
                String name = xssfRow.getCell(0).getStringCellValue();
                Object id = xssfRow.getCell(1).getNumericCellValue();
                String idString = id.toString().replace(".0", "");
                Object partnerTypeString = xssfRow.getCell(2).getNumericCellValue();
                Integer partnerType = Integer.valueOf(partnerTypeString.toString().replace(".0", ""));
                LiveEvent validLiveEvent = new LiveEvent();
                validLiveEvent.setName(name);
                validLiveEvent.setMultiLangNames(getMultiLang(name));
                Partner partner = new Partner();
                partner.setType(partnerType == 499 ? PartnerType.STATS : PartnerType.SODA);
                partner.setId(idString);
                validLiveEvent.setPartners(Lists.newArrayList(partner));
                validLiveEvent.setParentType(0L);
                validLiveEvent.setDeleted(false);
                Long result = SbdLiveEventInternalApis.saveLiveEvent(validLiveEvent);
                System.out.print("the current count:" + result);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.print("the current count:" + count);
    }

    @Test
    public void LiveEventImport() {
        int count = 0;
        try {
            // 创建可读入的Excel工作簿
            File file = new File("D:\\EVENTDetail.xlsx");//根据文件名创建一个文件对象
            InputStream is = new FileInputStream(file);
            XSSFWorkbook xssfWorkbook = new XSSFWorkbook(is);
            XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(0);
            int rows = xssfSheet.getLastRowNum();//得到所有的行
            for (int i = 1; i < rows; i++) {
                XSSFRow xssfRow = xssfSheet.getRow(i);
                String parentName = xssfRow.getCell(0).getStringCellValue();
                String name = xssfRow.getCell(1).getStringCellValue();
                Object id = null;
                try {
                    id = xssfRow.getCell(2).getStringCellValue();
                } catch (Exception e) {
                    id = xssfRow.getCell(2).getNumericCellValue();
                }
                String idString = id.toString().replace(".0", "");
                Object partnerTypeString = null;
                try {
                    partnerTypeString = xssfRow.getCell(3).getStringCellValue();
                } catch (Exception e) {
                    partnerTypeString = xssfRow.getCell(3).getNumericCellValue();
                }
                Integer partnerType = Integer.valueOf(partnerTypeString.toString().replace(".0", ""));
                LiveEvent validLiveEvent = new LiveEvent();
                validLiveEvent.setName(name);
                validLiveEvent.setMultiLangNames(getMultiLang(name));
                Partner partner = new Partner();
                partner.setType(partnerType == 499 ? PartnerType.STATS : PartnerType.SPORTRADAR);
                partner.setId(idString);
                validLiveEvent.setDeleted(false);
                validLiveEvent.setPartners(Lists.newArrayList(partner));
                LiveEvent liveEvent = SbdLiveEventInternalApis.getLiveEventByNameAndParentId(parentName, 0L);
                if (liveEvent != null) {
                    validLiveEvent.setParentType(liveEvent.getId());
                }
                Long result = SbdLiveEventInternalApis.saveLiveEvent(validLiveEvent);
                System.out.print("the current count:" + result);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.print("the current count:" + count);
    }

    public List<LangString> getMultiLang(String value) {
        List<LangString> langStrings = Lists.newArrayList();
        if (null != value) {
            LangString cn = new LangString();
            cn.setLanguage(LanguageCode.ZH_CN);
            cn.setValue(value);
            langStrings.add(cn);
            LangString hk = new LangString();
            hk.setLanguage(LanguageCode.ZH_HK);
            hk.setValue(HanLP.convertToTraditionalChinese(value));
            langStrings.add(hk);
            LangString us = new LangString();
            hk.setLanguage(LanguageCode.EN_US);
            hk.setValue(value);
            langStrings.add(us);
            LangString ushk = new LangString();
            hk.setLanguage(LanguageCode.EN_HK);
            hk.setValue(value);
            langStrings.add(ushk);
        }
        return langStrings;
    }

    @Test
    public void updateEventDetailId() {
        Long redCard = 100157000L;
        Long yellowCard = 100158000L;
        Long substitution = 104656000L;
        Long goal = 100159000L;
        Long freeGoal = 100156000L;
        Long wulongGoal = 104658000L;
        Long touGoal = 104660000L;

        LiveEvent event = SbdLiveEventInternalApis.getLiveEventByNameAndParentId("红牌", 0L);
        event.setId(redCard);
        SbdLiveEventInternalApis.saveLiveEvent(event);
        LiveEvent event1 = SbdLiveEventInternalApis.getLiveEventByNameAndParentId("黄牌", 0L);
        event1.setId(yellowCard);
        SbdLiveEventInternalApis.saveLiveEvent(event1);
        LiveEvent event2 = SbdLiveEventInternalApis.getLiveEventByNameAndParentId("换人", 0L);
        event2.setId(substitution);
        SbdLiveEventInternalApis.saveLiveEvent(event2);
        LiveEvent event3 = SbdLiveEventInternalApis.getLiveEventByNameAndParentId("进球", 0L);
        event3.setId(goal);
        SbdLiveEventInternalApis.saveLiveEvent(event3);
        LiveEvent event4 = SbdLiveEventInternalApis.getLiveEventByNameAndParentId("乌龙球", 100159000L);
        event4.setId(wulongGoal);
        SbdLiveEventInternalApis.saveLiveEvent(event4);
        LiveEvent event5 = SbdLiveEventInternalApis.getLiveEventByNameAndParentId("头球", 100159000L);
        event5.setId(touGoal);
        SbdLiveEventInternalApis.saveLiveEvent(event5);
        LiveEvent event6 = SbdLiveEventInternalApis.getLiveEventByNameAndParentId("点球", 100159000L);
        event6.setId(freeGoal);
        SbdLiveEventInternalApis.saveLiveEvent(event6);

    }
}







