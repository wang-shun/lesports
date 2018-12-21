package com.lesports.sms.data.processor.stats;

import com.lesports.sms.data.model.stats.StatsPlayerStanding;
import com.lesports.sms.data.processor.BeanProcessor;
import com.lesports.sms.data.processor.olympic.AbstractProcessor;
import org.slf4j.Logger;

/**
 * Created by qiaohongxin on 2016/5/12.
 */
public class PlayerStandingProcessor extends AbstractProcessor implements BeanProcessor<StatsPlayerStanding> {
    private static Logger LOG = org.slf4j.LoggerFactory.getLogger(PlayerStandingProcessor.class);

    public Boolean process(String fileType,StatsPlayerStanding standing) {
        return false;
    }
    }
