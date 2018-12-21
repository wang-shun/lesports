package com.lesports.sms.data.util;

import com.google.common.collect.Lists;
import com.lesports.sms.data.model.Constants;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.*;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.List;

/**
 * Created by gengchengliang on 2015/4/27.
 */
public class FileUtil {

    private static final Logger LOG = LoggerFactory.getLogger(FileUtil.class);
    private static FileInputStream fis = null;
    private static FileChannel fc = null;
    private static FileLock flock = null;
    private static final int HTTP_CONNECTION_TIME_OUT_IN_MILL_SECONDS = 10 * 1000;
    private static final int HTTP_READ_TIME_OUT_IN_MILL_SECONDS = 10 * 1000;
    private static final int FAIL_SLEEP_TIME_IN_MILL_SECONDS = 2 * 1000;
    private static final int FTP_LOGIN_TIME_OUT_IN_SECONDS = 60;
    private static final int FAIL_MAX_RETRY_COUNT = 3;

    public static void lock(File xmlFile) {
        try {
            fis = new FileInputStream(xmlFile);
            fc = fis.getChannel();
            flock = fc.tryLock();
            if (flock.isValid()) {
                LOG.info(xmlFile.getName() + " is locked");
            }
        } catch (Exception e) {
            LOG.error(xmlFile.getName() + " lock error");
        }
    }

    public static void unlock(File xmlFile) {
        try {
            flock.release();
            System.out.println(xmlFile.getName() + "is released");
            fc.close();
            fis.close();
        } catch (Exception e) {
            LOG.error(xmlFile.getName() + " unlock error");
        }
    }

    public static FileInputStream getInputStreamByFileURL(String fileUri, String localPath1) {
        URL url = null;
        try {
            url = new URL(fileUri);
        } catch (MalformedURLException e) {
            LOG.error("{}", e.getMessage(), e);
        }
        String schema = url.getProtocol();
        String localPath = localPath1;
        if ("ftp".equalsIgnoreCase(schema)) {
            String host = url.getHost();
            int port = url.getPort();
            String username = getUsername(url), password = getPassword(url);
            String remotePath = getPathWithoutName(url), fileName = getFileName(url);
            downloadFileFromFtp(host, port, username, password, remotePath, fileName, localPath);
            LOG.info("have down load the file:{}", fileName);
            localPath = localPath + fileName;
        } else if ("http".equalsIgnoreCase(schema)) {
            String fileName = getFileName(url), username = getUsername(url), password = getPassword(url);
            downloadFileFromHttp(url, localPath, username, password);
            LOG.info("have down load the file:{}", fileName);
        } else if ("file".equalsIgnoreCase(schema)) {
            localPath = url.getPath();
        }

        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(new File(localPath));
        } catch (FileNotFoundException e) {
            LOG.error("{}", e.getMessage(), e);
        }
        return fileInputStream;
    }

    public static List<String> getAllFilesUrlFromDir(String ftpHost, int ftpPort, String ftpUsername, String ftpPassword,
                                                     String ftpDirPath) {
        List<String> lists = Lists.newArrayList();
        boolean result = true;
        FtpUtil ftpUtil = new FtpUtil(ftpHost, ftpPort, ftpUsername, ftpPassword);
        try {
            if (!ftpUtil.loginFtp(FTP_LOGIN_TIME_OUT_IN_SECONDS)) {
                return lists;
            }
            String[] fileNames = ftpUtil.getPlayerFiles(ftpDirPath);
            if (fileNames.length <= 0) return lists;
            for (int i = 0; i < fileNames.length; i++) {
                String currentUrl = "ftp://" + ftpUsername + ":" + ftpPassword + "@" + ftpHost + ":" + ftpPort + ftpDirPath + fileNames[i];
                lists.add(currentUrl);
            }
            return lists;
        } catch (IOException e) {
            LOG.error("{}", e);
            return lists;
        } finally {
            ftpUtil.logOutFtp();
        }
    }


    private static Boolean downloadFileFromFtp(String ftpHost, int ftpPort, String ftpUsername, String ftpPassword,
                                               String ftpPath, String fileName, String localRootPath) {
        boolean result = true;
        FtpUtil ftpUtil = new FtpUtil(ftpHost, ftpPort, ftpUsername, ftpPassword);
        try {
            if (!ftpUtil.loginFtp(FTP_LOGIN_TIME_OUT_IN_SECONDS)) {
                return false;
            }
            boolean downloadResult = ftpUtil.downloadFile(fileName, localRootPath, ftpPath);
            if (!downloadResult) {
                LOG.error("download file fail!{}", fileName);
                result = false;
            } else {
                LOG.info("download file success!{}", fileName);
            }
        } catch (IOException e) {
            LOG.error("{}", e);
            result = false;
        } finally {
            ftpUtil.logOutFtp();
        }
        return result;
    }

    public static Boolean downloadFileFromHttp(URL url, String filePath, String username, String password) {
        Authenticator.setDefault(new CustomAuthenticator(username, password));
        boolean isSuccess = false;
        int tryCount = 0;
        while (isSuccess == false && tryCount++ < FAIL_MAX_RETRY_COUNT) {
            try {
                FileUtils.copyURLToFile(url, new File(filePath), HTTP_CONNECTION_TIME_OUT_IN_MILL_SECONDS, HTTP_READ_TIME_OUT_IN_MILL_SECONDS);
                isSuccess = true;
            } catch (FileNotFoundException e) {
                LOG.warn("file can not be found on this url : {}, local path : {}", url, filePath);
                return false;
            } catch (ConnectException e) {
                LOG.warn("connection to the url time out : {}", url);
                sleep(FAIL_SLEEP_TIME_IN_MILL_SECONDS);
            } catch (SocketTimeoutException e) {
                LOG.warn("read time overtime");
                sleep(FAIL_SLEEP_TIME_IN_MILL_SECONDS);
            } catch (Exception e) {
                LOG.error("{}", e.getMessage(), e);
            }
        }
        LOG.info("download file from : {}, result : {}.", url, isSuccess);
        return isSuccess;
    }

    private static void sleep(int milisec) {
        try {
            Thread.sleep(milisec);
        } catch (InterruptedException e1) {
            LOG.error("{}", e1.getMessage(), e1);
        }
    }

    private static String getUsername(URL url) {
        if (StringUtils.isNotEmpty(url.getUserInfo())) {
            String[] userInfo = StringUtils.split(url.getUserInfo(), ":");
            return userInfo[0];
        }
        return null;
    }

    private static String getPassword(URL url) {
        if (StringUtils.isNotEmpty(url.getUserInfo())) {
            String[] userInfo = StringUtils.split(url.getUserInfo(), ":");
            return userInfo[1];
        }
        return null;
    }

    private static String getFileName(URL url) {
        if (url == null || StringUtils.isBlank(url.getPath())) return null;
        if (url.getPath().contains("FILE_LIST")) {
            return StringUtils.substringAfterLast(url.getPath(), "/");
        } else {
            return StringUtils.substringAfterLast(url.getPath(), "/");
        }
    }

    private static String getPathWithoutName(URL url) {
        if (StringUtils.isNotEmpty(url.getPath())) {
            return StringUtils.substringBeforeLast(url.getPath(), "/");
        }
        return null;
    }

    public static class CustomAuthenticator extends Authenticator {
        private String username;
        private String password;

        public CustomAuthenticator(String username, String password) {
            this.username = username;
            this.password = password;
        }

        @Override
        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(username, password.toCharArray());
        }
    }
}

