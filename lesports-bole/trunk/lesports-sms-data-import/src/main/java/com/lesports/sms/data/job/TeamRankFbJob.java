package com.lesports.sms.data.job;

import com.lesports.sms.data.model.Constants;
import com.lesports.sms.data.service.TeamRankFbParser;
import com.lesports.sms.data.util.FtpUtil;
import com.lesports.utils.LeProperties;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by lufei1 on 2014/12/11.
 */
@Service("teamRankFbJob")
public class TeamRankFbJob extends ThirdDataJob {

    private String teamRankFbFiles1415 = LeProperties.getString("teamRankFb.files.1415");

    @Resource
    private TeamRankFbParser teamRankFbParser;

    public void run() {
        FtpUtil ftpUtil = new FtpUtil(Constants.srHost, Constants.srPort, Constants.srUserName, Constants.srPassword);
        super.downloadAndParseData(ftpUtil, "teamRankFbJob", teamRankFbFiles1415, Constants.sportradarRootPath, "//Sport//", teamRankFbParser);
    }
}
