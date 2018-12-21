package com.lesports.sms.data.job;

import com.lesports.sms.data.model.Constants;
import com.lesports.sms.data.util.FtpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service("moveFileJob")
public class MoveFileJob {
    private String jobName = "moveFileJob";
    private static Logger logger = LoggerFactory.getLogger(MoveFileJob.class);

    public void run() {
        FtpUtil ftpUtil = new FtpUtil(Constants.srHost, Constants.srPort, Constants.srUserName, Constants.srPassword);
        try {

            //Stopwatch stopwatch = new Stopwatch();
            //stopwatch.start();
            logger.info("{} job execute begin", jobName);
            if (!ftpUtil.loginFtp(60)) {
                logger.error("login ftp:{} fail", ftpUtil.getFtpName());
                return;
            }
            ftpUtil.moveAndDeleteFiles("//Sport//", "//Sport//copy//");
            logger.info("{} job execute end", jobName);
        } catch (Exception e) {
            logger.error("download and parse " + jobName + " file error", e);
        } finally {
            ftpUtil.logOutFtp();
        }
    }

}
