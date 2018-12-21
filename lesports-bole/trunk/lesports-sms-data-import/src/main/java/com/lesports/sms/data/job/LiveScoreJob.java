package com.lesports.sms.data.job;

import com.lesports.sms.data.model.Constants;
import com.lesports.sms.data.service.LiveScoreParser;
import com.lesports.sms.data.util.FtpUtil;
import com.lesports.utils.LeProperties;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service("liveScoreJob")
public class LiveScoreJob extends ThirdDataJob {
	private String liveScoreDelta = LeProperties.getString("liveScore.files.delta");
    private String liveScoreFull = LeProperties.getString("liveScore.files.full");
    @Resource
    private LiveScoreParser liveScoreParser;

    public void run(String args) {
        FtpUtil ftpUtil = new FtpUtil(Constants.srHost, Constants.srPort, Constants.srUserName, Constants.srPassword);
        if("delta".equals(args)) {
            super.downloadAndParseData(ftpUtil, "liveScoreJob", liveScoreDelta, Constants.sportradarRootPath, "//Sport//livescore//", liveScoreParser);
        }else if("full".equals(args)){
            super.downloadAndParseData(ftpUtil, "liveScoreJob", liveScoreFull, Constants.sportradarRootPath, "//Sport//livescore//", liveScoreParser);
        }
    }
}
