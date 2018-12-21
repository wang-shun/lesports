package com.lesports.sms.data.job;

import com.lesports.sms.data.model.Constants;
import com.lesports.sms.data.service.CupFbParser;
import com.lesports.sms.data.util.FtpUtil;
import com.lesports.utils.LeProperties;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service("cupFbJob")
public class CupFbJob extends ThirdDataJob {
	private String cupFbFiles1415 = LeProperties.getString("cupFb.files.1415");
	@Resource
	private CupFbParser cupFbParser;

	public void run() {
		FtpUtil ftpUtil = new FtpUtil(Constants.srHost, Constants.srPort, Constants.srUserName, Constants.srPassword);
		super.downloadAndParseData(ftpUtil, "cupFbJob", cupFbFiles1415, Constants.sportradarRootPath, "//Sport//", cupFbParser);
	}
}
