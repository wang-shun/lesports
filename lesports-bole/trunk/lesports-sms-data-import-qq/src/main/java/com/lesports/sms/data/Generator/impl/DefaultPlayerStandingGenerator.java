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
 * Created by qiaohongxin on 2016/12/2.
 */
@Service("defaultPlayerStandingGenerator")
public class DefaultPlayerStandingGenerator extends Generator {
    @Resource
    FileParser fileParser;
    private static Logger LOG = LoggerFactory.getLogger(DefaultPlayerStandingGenerator.class);

    public boolean nextProcessor() {
        List<TransModel> standings = getFileUrl(Constants.playerStandings);
        if (CollectionUtils.isEmpty(standings)) {
            return false;
        }
        for (TransModel transModel : standings) {
            fileParser.nextProcessor("PLAYER-STANDING", transModel);
        }

        return true;
    }
}