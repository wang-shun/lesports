package com.lesports.sms.data.job;

import com.lesports.sms.data.model.Constants;
import com.lesports.sms.data.service.PlayerRandFbParser;
import com.lesports.sms.data.util.FtpUtil;
import com.lesports.utils.LeProperties;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by lufei1 on 2014/12/11.
 */
@Service("playerFandFbJob")
public class PlayerFandFbJob extends ThirdDataJob {

    private String playerFandFbFiles1415 = LeProperties.getString("playerFandFb.files.1415");

    @Resource
    private PlayerRandFbParser playerRandFbParser;


    public void run() {
        FtpUtil ftpUtil = new FtpUtil(Constants.srHost, Constants.srPort, Constants.srUserName, Constants.srPassword);
        super.downloadAndParseData(ftpUtil, "playerFandFbJob", playerFandFbFiles1415, Constants.sportradarRootPath, "//Sport//", playerRandFbParser);
    }
}
