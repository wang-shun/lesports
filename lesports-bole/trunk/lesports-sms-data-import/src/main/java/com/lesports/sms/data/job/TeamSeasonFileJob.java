package com.lesports.sms.data.job;

import com.lesports.sms.data.model.Constants;
import com.lesports.sms.data.service.PlayerFbParser;
import com.lesports.sms.data.service.TeamRankFbParser;
import com.lesports.sms.data.util.FtpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service("teamSeasonFileJob")
public class TeamSeasonFileJob {
    private String jobName = "teamSeasonFileJob";
    private static Logger logger = LoggerFactory.getLogger(TeamSeasonFileJob.class);
    @Resource
    private PlayerFbParser playerFbParser;
    public void run() {
        logger.info("teamSeasonFileJob:Begin");
        playerFbParser.parseData();
    }


}
