package com.lesports.sms.data.util;

import com.lesports.sms.data.model.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URL;

/**
 * Created by qiaohongxin on 2015/10/9.
 */
public class HttpUtils {
    private static Logger logger = LoggerFactory.getLogger(HttpUtils.class);
    private static int maxTryCount = 3;

    public static void downloadFile(String URL, String filePath) throws Exception {
        Authenticator.setDefault(new CustomAuthenticator());
        boolean result = false;
        int tryCount = 0;
        while (!result && tryCount++ < maxTryCount) {
            org.apache.commons.io.FileUtils.copyURLToFile(new URL(URL), new File(filePath), 10 * 1000, 10 * 1000);
            result = true;
        }
    }

    public static class CustomAuthenticator extends Authenticator {
        protected PasswordAuthentication getPasswordAuthentication() {
            String username = Constants.statsUserName;
            String password = Constants.statsPassWord;
            return new PasswordAuthentication(username, password.toCharArray());
        }
    }
}
