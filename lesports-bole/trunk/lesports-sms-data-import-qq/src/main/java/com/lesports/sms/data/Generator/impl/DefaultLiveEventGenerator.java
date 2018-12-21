package com.lesports.sms.data.Generator.impl;

import com.lesports.sms.data.Generator.Generator;
import com.lesports.sms.data.model.Constants;
import com.lesports.sms.data.model.TransModel;
import com.lesports.sms.data.parser.FileParser;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by qiaohongxin on 2016/11/17.
 */
@Service("defaultLiveEventGenerator")
public class DefaultLiveEventGenerator extends Generator {
    @Resource
    FileParser fileParser;
    private static Logger LOG = LoggerFactory.getLogger(DefaultLiveEventGenerator.class);

    public boolean nextProcessor() {
        List<TransModel> schedules = getFileUrl(Constants.liveEvents);
        if (CollectionUtils.isEmpty(schedules)) {
            return false;
        }
        for (TransModel transModel : schedules) {
            fileParser.nextProcessor("LIVE-EVENT", transModel);
        }

        return true;
    }


}