package com.lesports.sms.download.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URL;

/**
 * Created by qiaohongxin on 2015/10/9.
 */
public class HttpUtils {
    private static Logger logger = LoggerFactory.getLogger(HttpUtils.class);
    private static int maxTryCount = 3;

    public static Boolean downloadFile(String URL, String filePath) throws Exception {
        Authenticator.setDefault(new CustomAuthenticator());
        boolean isSuccess = false;
        int tryCount = 0;
        while (isSuccess == false && tryCount++ < maxTryCount) {
            try {
                org.apache.commons.io.FileUtils.copyURLToFile(new URL(URL), new File(filePath), 60 * 1000, 60 * 1000);
            } catch (Exception e) {
                if (e instanceof java.net.ConnectException) {
                    logger.warn("connection to the url time out",e);
                    return isSuccess;
                }
                if (e instanceof java.io.FileNotFoundException) {
                    logger.warn("file can not be found on this url" + URL,e);
                    return isSuccess;
                }
                if (e instanceof java.net.SocketTimeoutException) {
                    logger.warn("read time overtime",e);
                    Thread.sleep(2 * 1000);
                    continue;
                }
            }
            isSuccess = true;
        }
        return isSuccess;
    }

    public static class CustomAuthenticator extends Authenticator {
        protected PasswordAuthentication getPasswordAuthentication() {
            String username = Constants.statsUserName;
            String password = Constants.statsPassWord;
            return new PasswordAuthentication(username, password.toCharArray());
        }
    }
}
