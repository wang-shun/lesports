package com.lesports.sms.data.parser;


import com.lesports.sms.data.Generator.impl.DefaultLiveScoreGenerator;
import com.lesports.sms.data.Generator.impl.DefaultPlayerStandingGenerator;
import com.lesports.sms.data.Generator.impl.DefaultPlayerStatsGenerator;
import com.lesports.sms.data.Generator.impl.DefaultTeamStandingGenerator;
import com.lesports.sms.data.adapter.impl.*;
import com.lesports.sms.data.adapter.DefualtAdaptor;
import com.lesports.sms.data.model.TransModel;
import com.lesports.sms.data.model.commonImpl.*;
import com.lesports.sms.data.util.FileUtil;
import com.lesports.utils.xml.Parser;
import com.lesports.utils.xml.ParserFactory2;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.FileInputStream;
import java.util.List;


/**
 * lesports-bole.
 *
 * @author pangchuanxiao
 * @since 2016/4/18
 */
@Service("fileParser")
public class FileParser {
    @Resource
    DefaultScheduleAdaptor defaultScheduleAdaptor;
    @Resource
    DefaultTeamStatAdaptor defaultTeamStatAdaptor;
    @Resource
    DefaultPlayerStatAdaptor defaultPlayerStatAdaptor;
    @Resource
    DefaultTeamSeasonAdaptor defaultTeamSeasonAdaptor;
    @Resource
    DefaultPlayerStandingAdaptor defaultPlayerStandingAdaptor;
    @Resource
    DefaultTeamStandingAdaptor defaultTeamStandingAdaptor;
    @Resource
    DefaultLiveScoreAdaptor defaultLiveScoreAdaptor;

    private static Logger LOG = LoggerFactory.getLogger(FileParser.class);

    public boolean nextProcessor(String code, TransModel transModel) {
        List<Object> objects = getAllObjects(code, transModel.getAnotationType(), transModel.getFileUrl());
        if (CollectionUtils.isEmpty(objects)) return false;
        DefualtAdaptor adaptor = getAdaptorByCode(code);
        for (Object object : objects) {
            adaptor.nextProcessor(transModel.getPartnerType(), transModel.getCsid(), object);
        }
        return true;
    }


    private List<Object> getAllObjects(String code, String annotationType, String fileUrl) {
        FileInputStream fileInputStream = FileUtil.getInputStreamByFileURL(fileUrl, "D:\\");
        if (null == fileInputStream) {
            LOG.warn("will not handle this, file not exists : {}.", fileUrl);
            return null;
        }
        try {
            //使用模板将文件流转为对象并发出
            Class clazz = getClassByCode(code);
            if (clazz != null) {
                Parser<DefaultModel> parser = new ParserFactory2().createXmlParser(clazz);
                //使用模板将文件流转为对象并发出
                DefaultModel xmlTemplate = parser.parse(annotationType, fileInputStream);
                return xmlTemplate.getData();
            }
        } catch (Exception e) {
            LOG.error("{}", e.getMessage(), e);
        }
        return null;
    }

    private DefualtAdaptor getAdaptorByCode(String code) {
        if (code.equals("SCHEDULE")) return defaultScheduleAdaptor;
        else if (code.equals("TEAM-STATS")) return defaultTeamStatAdaptor;
        else if (code.equals("PLAYER-STATS")) return defaultPlayerStatAdaptor;
        else if (code.equals("TEAM-SEASON")) return defaultTeamSeasonAdaptor;
        else if (code.equals("PLAYER-STANDING")) return defaultPlayerStandingAdaptor;
        else if (code.equals("TEAM-STANDING")) return defaultTeamStandingAdaptor;
        else if (code.equals("LIVE-SCORE")) return defaultLiveScoreAdaptor;
        return null;
    }

    private Class getClassByCode(String code) {
        if (code.equals("SCHEDULE")) return DefaultSchedule.class;
        else if (code.equals("TEAM-STATS")) return DefaultTeamStats.class;
        else if (code.equals("PLAYER-STATS")) return DefaultPlayerStats.class;
        else if (code.equals("TEAM-SEASON")) return DefaultTeamSeason.class;
        else if (code.equals("PLAYER-STANDING")) return DefaultPlayerStanding.class;
        else if (code.equals("TEAM-STANDING")) return DefualtTeamStanding.class;
        else if (code.equals("LIVE-SCORE")) return DefaultLiveScore.class;
        return null;
    }


}
