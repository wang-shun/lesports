package com.lesports.sms.cooperation.util;

import com.lesports.utils.LeDateUtils;
import com.lesports.utils.LeProperties;
import org.apache.commons.net.ftp.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;

/**
 * Created by lufei1 on 2014/12/11.
 */
public class FtpUtil {
    private static Logger logger = LoggerFactory.getLogger(FtpUtil.class);
    private static String matchFbDetailFiles1415 = LeProperties.getString("matchFb.details.files.1415");

    private FTPClient ftpClient;
    private String ftpName;
    private String host;
    private int port;
    private String user;
    private String password;

    public FtpUtil(String host, int port, String user, String Password) {
        ftpName = host + ":" + port;
        this.host = host;
        this.port = port;
        this.user = user;
        this.password = Password;
        this.ftpClient = new FTPClient();
    }


    public boolean loginFtp(int conTimeout) throws IOException {
        boolean isLogin = false;
        FTPClientConfig ftpClientConfig = new FTPClientConfig();
        ftpClientConfig.setServerTimeZoneId(TimeZone.getDefault().getID());
        ftpClient.setControlEncoding("GBK");
        ftpClient.configure(ftpClientConfig);
        try {
            if (this.port > 0) {
                this.ftpClient.connect(this.host, this.port);
            } else {
                this.ftpClient.connect(this.host);
            }
            // FTP服务器连接回答
            int reply = this.ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                this.ftpClient.disconnect();
                logger.error("登录FTP:{}服务失败！", ftpName);
                return isLogin;
            }
            ftpClient.login(user, password);
            // 设置传输协议
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            ftpClient.setBufferSize(1024 * 2);
            ftpClient.setDataTimeout(60 * 1000);
            ftpClient.setConnectTimeout(conTimeout * 1000);
            ftpClient.setDefaultTimeout(60 * 1000);
            logger.info("user:{}成功登陆FTP:{}服务器", user, ftpName);
            isLogin = true;
        } catch (Exception e) {
            logger.error(user + "登录FTP:" + ftpName + "服务失败！", e);
        }
        return isLogin;
    }



    public void logOutFtp() {
        if (null != ftpClient && ftpClient.isConnected()) {
            try {
                boolean reuslt = ftpClient.logout();// 退出FTP服务器
                if (reuslt) {
                    logger.info("成功退出服务器:{}", ftpName);
                }
            } catch (IOException e) {
                logger.error("退出FTP服务器异常！" + e);
            } finally {
                try {
                    ftpClient.disconnect();// 关闭FTP服务器的连接
                } catch (IOException e) {
                    logger.error("关闭FTP服务器的连接异常！", e);
                }
            }
        }
    }


    public void uploadFile(String  orgFile,String fileName, String uploadPath) {
        try {
            ftpClient.enterLocalPassiveMode();
            ftpClient.makeDirectory(uploadPath);
            ftpClient.changeWorkingDirectory(uploadPath);
            InputStream inputStream = new FileInputStream(new File(orgFile));
            ftpClient.storeFile(fileName,inputStream);
        }
        catch(Exception e){
            logger.error("上传file失败", e);
        }
    }

    public String getFtpName() {
        return ftpName;
    }

    public void setFtpName(String ftpName) {
        this.ftpName = ftpName;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }



}
