package com.lesports.sms.data.job;

import com.lesports.sms.data.model.Constants;
import com.lesports.sms.data.service.PlayerAndTeamRandF1Parser;
import com.lesports.sms.data.util.FtpUtil;
import com.lesports.utils.LeProperties;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by ruiyuansheng on 2015/5/29.
 */
@Service("playerAndTeamRandF1Job")
public class PlayerAndTeamRandF1Job extends ThirdDataJob {

    private String playerAndTeamRandF1Files2015 = LeProperties.getString("playerAndTeamRandF1.files.2015");

    @Resource
    private PlayerAndTeamRandF1Parser playerAndTeamRandF1Parser;


    public void run() {
        FtpUtil ftpUtil = new FtpUtil(Constants.srHost, Constants.srPort, Constants.srUserName, Constants.srPassword);
        super.downloadAndParseData(ftpUtil, "playerAndTeamRandF1Job", playerAndTeamRandF1Files2015, Constants.sportradarRootPath, "//Sport//", playerAndTeamRandF1Parser);
    }

}
