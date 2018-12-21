package com.lesports.sms.data.job;

import com.lesports.sms.data.model.Constants;
import com.lesports.sms.data.service.MatchFbDetailParser;
import com.lesports.sms.data.util.FtpUtil;
import com.lesports.utils.LeProperties;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Service("matchFbDetailJob")
public class MatchFbDetailJob extends ThirdDataJobDetail {

    private String matchFbDetailFiles1415 = LeProperties.getString("matchFb.details.files.1415");

    @Resource
    private MatchFbDetailParser matchFbDetailParser;

    public void run() {
        FtpUtil ftpUtil = new FtpUtil(Constants.srHost, Constants.srPort, Constants.srUserName, Constants.srPassword);
        super.downloadAndParseData(ftpUtil, "matchFbDetailJob", matchFbDetailFiles1415, Constants.sportradarRootPath, "//Sport//", matchFbDetailParser);
    }
}
