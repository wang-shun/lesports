package com.lesports.sms.data.job;

import com.lesports.sms.data.model.Constants;
import com.lesports.sms.data.util.FtpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Created by zhonglin on 2016/1/12.
 */

@Service("moveSportradarFileJob")
public class MoveSportradarFileJob {
    private String jobName = "moveSportradarFileJob";
    private static Logger logger = LoggerFactory.getLogger(MoveSportradarFileJob.class);

    public void run() {
        FtpUtil ftpUtil = new FtpUtil(Constants.srHost, Constants.srPort, Constants.srUserName, Constants.srPassword);
        try {
            logger.info("{} job execute begin", jobName);
            if (!ftpUtil.loginFtp(60)) {
                logger.error("login ftp:{} fail", ftpUtil.getFtpName());
                return;
            }
            ftpUtil.moveAndDeleteSportradarFiles("//Sport//", "//Sport//copy//");
            logger.info("{} job execute end", jobName);
        } catch (Exception e) {
            logger.error("download and parse " + jobName + " file error", e);
        } finally {
            ftpUtil.logOutFtp();
        }
    }



}
