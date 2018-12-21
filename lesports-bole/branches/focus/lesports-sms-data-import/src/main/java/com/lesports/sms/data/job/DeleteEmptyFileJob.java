package com.lesports.sms.data.job;

import com.lesports.sms.data.model.Constants;
import com.lesports.sms.data.util.FtpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Created by ruiyuansheng on 2015/6/22.
 */

@Service("deleteEmptyFileJob")
public class DeleteEmptyFileJob {
    private String jobName = "deleteEmptyFileJob";
    private static Logger logger = LoggerFactory.getLogger(DeleteEmptyFileJob.class);

    public void run(String arg) {
        FtpUtil ftpUtil = new FtpUtil(Constants.srHost, Constants.srPort, Constants.srUserName, Constants.srPassword);
        try {

            logger.info("{} job execute begin", jobName);
            if (!ftpUtil.loginFtp(60)) {
                logger.error("login ftp:{} fail", ftpUtil.getFtpName());
                return;
            }
            if ("sport".equals(arg))
                ftpUtil.deleteEmptyFile("//Sport//");
            if ("livescore".equals(arg))
                ftpUtil.deleteEmptyFile("//Sport//livescore//");
            logger.info("{} job execute end", jobName);
        } catch (Exception e) {
            logger.error("delete empty file " + jobName + " file error", e);
        } finally {
            ftpUtil.logOutFtp();

        }
    }


}
