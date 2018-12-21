package com.lesports.sms.data.Generator.impl;

import com.google.common.collect.Lists;
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
@Service("defaultPlayerCareerGenerator")
public class DefaultPlayerCareerGenerator extends Generator {
    @Resource
    FileParser fileParser;
    private static Logger LOG = LoggerFactory.getLogger(DefaultPlayerCareerGenerator.class);

    public boolean nextProcessor() {
        List<TransModel> playerstats = Lists.newArrayList();//getFileUrl(Constants.pl);
        if (CollectionUtils.isEmpty(playerstats)) {
            return false;
        }
        for (TransModel transModel : playerstats) {
            fileParser.nextProcessor("PLAYER-STATS", transModel);
        }

        return true;
    }


}
