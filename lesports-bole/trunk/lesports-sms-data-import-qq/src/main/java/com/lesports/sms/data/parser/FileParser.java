package com.lesports.sms.data.parser;


import com.lesports.sms.data.adapter.impl.*;
import com.lesports.sms.data.adapter.DefualtAdaptor;
import com.lesports.sms.data.model.DefaultModel;
import com.lesports.sms.data.model.TransModel;
import com.lesports.sms.data.model.commonImpl.*;
import com.lesports.sms.data.util.HttpRestUtil;
import com.lesports.sms.data.util.JsonPath.JsonParser;
import com.lesports.sms.data.util.JsonPath.JsonParserFactory;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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
            adaptor.nextProcessor(transModel, object);
        }
        return true;
    }


    private List<Object> getAllObjects(String code, String annotationType, String fileUrl) {
        String jsonData = HttpRestUtil.getData(fileUrl);
        if (null == jsonData) {
            LOG.warn("will not handle this, json content not exists : {}.", fileUrl);
            return null;
        }
        try {
            //使用模板将文件流转为对象并发出
            Class clazz = getClassByCode(code);
            if (clazz != null) {
                JsonParser<DefaultModel> parser = new JsonParserFactory().createJsonParser(clazz);
                //使用模板将文件流转为对象并发出
                DefaultModel jsonTemplate = parser.parse(annotationType, jsonData);
                return jsonTemplate.getData();
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
        else if (code.equals("TEAM-STANDING")) return DefaultTeamStanding.class;
        else if (code.equals("LIVE-SCORE")) return DefaultLiveScore.class;
        return null;
    }


}
