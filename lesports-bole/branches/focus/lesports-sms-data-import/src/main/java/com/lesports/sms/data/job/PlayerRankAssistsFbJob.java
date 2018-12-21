package com.lesports.sms.data.job;

import com.lesports.sms.data.model.Constants;
import com.lesports.sms.data.service.PlayerRankAssistsFbParser;
import com.lesports.sms.data.util.FtpUtil;
import com.lesports.utils.LeProperties;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by lufei1 on 2014/12/11.
 */
@Service("playerRankAssistsFbJob")
public class PlayerRankAssistsFbJob extends ThirdDataJob {

    private String playerRankAssistsFbFiles1415 = LeProperties.getString("playerRankAssistsFb.files.1415");

    @Resource
    private PlayerRankAssistsFbParser playerRankAssistsFbParser;


    public void run() {
        FtpUtil ftpUtil = new FtpUtil(Constants.srHost, Constants.srPort, Constants.srUserName, Constants.srPassword);
        super.downloadAndParseData(ftpUtil, "playerRankAssistsFbJob", playerRankAssistsFbFiles1415, Constants.sportradarRootPath, "//Sport//", playerRankAssistsFbParser);
    }
}


