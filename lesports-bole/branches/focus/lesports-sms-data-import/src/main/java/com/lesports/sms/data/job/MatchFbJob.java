package com.lesports.sms.data.job;

import com.lesports.sms.data.model.Constants;
import com.lesports.sms.data.service.MatchFbParser;
import com.lesports.sms.data.util.FtpUtil;
import com.lesports.utils.LeProperties;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by lufei1 on 2014/12/11.
 */
@Service("matchFbJob")
public class MatchFbJob extends ThirdDataJob {

    private String matchFbFiles1415 = LeProperties.getString("matchFb.files.1415");

    @Resource
    private MatchFbParser matchFbParser;

    public void run() {
        FtpUtil ftpUtil = new FtpUtil(Constants.srHost, Constants.srPort, Constants.srUserName, Constants.srPassword);
        super.downloadAndParseData(ftpUtil, "matchFbJob", matchFbFiles1415, Constants.sportradarRootPath, "//Sport//", matchFbParser);
    }
}
