package com.lesports.sms.data.utils;

import com.lesports.sms.data.Constants;
import org.apache.commons.net.ftp.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * Created by qiaohongxin on 2016/03/17.
 */
public class FtpUtil {
    private static Logger LOG = LoggerFactory.getLogger(FtpUtil.class);

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
                LOG.error("登录FTP:{}服务失败！", ftpName);
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
            LOG.info("user:{}成功登陆FTP:{}服务器", user, ftpName);
            isLogin = true;
        } catch (Exception e) {
            LOG.error(user + "登录FTP:" + ftpName + "服务失败！", e);
        }
        return isLogin;
    }

    public Date getFileLastModifiedTime(String remotePath, String remoteFileName) throws IOException {
        ftpClient.changeWorkingDirectory(remotePath);
        FTPFile[] ftpFiles = ftpClient.listFiles(remoteFileName);
        if (ftpFiles.length != 1) {
            LOG.warn("file:{} not exists on remote", remoteFileName);
            return null;
        }

        return ftpFiles[0].getTimestamp().getTime();
    }

    public boolean downloadFile(String remoteFileName, String localRootPath, String remoteDownLoadPath) throws IOException {
        String strFilePath = localRootPath + remoteFileName;
        BufferedOutputStream outStream = null;
        boolean success = false;
        try {
            ftpClient.enterLocalPassiveMode();
            ftpClient.changeWorkingDirectory(remoteDownLoadPath);
            FTPFile[] ftpFiles = ftpClient.listFiles(remoteFileName);
            if (ftpFiles.length != 1) {
                    LOG.warn("file:{} not exists on remote,localRootPath:{},remoteDownLoadPath:{}", remoteFileName, localRootPath, remoteDownLoadPath);
                return success;
            }
            outStream = new BufferedOutputStream(new FileOutputStream(strFilePath));
            if (ftpClient.retrieveFile(remoteFileName, outStream)) {
                LOG.info("file:{}下载成功,最后修改时间：{}", remoteFileName, ftpClient.getModificationTime(remoteFileName));
                success = true;
            }
        } catch (IOException e) {
            LOG.error(remoteFileName + "下载失败,localRootPath:{" + localRootPath + "},remoteDownLoadPath:{" + remoteDownLoadPath + "}", e);
        } finally {
            if (null != outStream) {
                outStream.flush();
                outStream.close();
            }
        }
        return success;
    }

    public boolean deleteFile(String remoteFileName, String remoteDownLoadPath) {
        boolean success = false;
        try {
            ftpClient.enterLocalPassiveMode();
            ftpClient.changeWorkingDirectory(remoteDownLoadPath);
            if (ftpClient.deleteFile(remoteFileName)) {
                LOG.info("file:{}删除成功", remoteFileName);
                success = true;
            }
        } catch (Exception e) {
            LOG.error(remoteFileName + "删除失败", e);
        }
        return success;
    }

    public void logOutFtp() {
        if (null != ftpClient && ftpClient.isConnected()) {
            try {
                boolean reuslt = ftpClient.logout();// 退出FTP服务器
                if (reuslt) {
                    LOG.info("成功退出服务器:{}", ftpName);
                }
            } catch (IOException e) {
                LOG.error("退出FTP服务器异常！" + e);
            } finally {
                try {
                    ftpClient.disconnect();// 关闭FTP服务器的连接
                } catch (IOException e) {
                    LOG.error("关闭FTP服务器的连接异常！", e);
                }
            }
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
