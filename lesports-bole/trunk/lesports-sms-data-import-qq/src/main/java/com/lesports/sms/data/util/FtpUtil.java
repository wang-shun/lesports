package com.lesports.sms.data.util;

import com.lesports.utils.LeDateUtils;
import com.lesports.utils.LeProperties;
import org.apache.commons.net.ftp.*;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
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

    public Date getFileLastModifiedTime(String remotePath, String remoteFileName) throws IOException {
        ftpClient.changeWorkingDirectory(remotePath);
        FTPFile[] ftpFiles = ftpClient.listFiles(remoteFileName);
        if (ftpFiles.length != 1) {
            logger.warn("file:{} not exists on remote", remoteFileName);
            return null;
        }

        return ftpFiles[0].getTimestamp().getTime();
    }

    public void deleteEmptyFile(String remoteDownLoadPath) {
        try {

            ftpClient.enterLocalPassiveMode();
            ftpClient.changeWorkingDirectory(remoteDownLoadPath);
            FTPFile[] ftpFiles = ftpClient.listFiles(remoteDownLoadPath, new FTPFileFilter() {
                @Override
                public boolean accept(FTPFile file) {
                    Calendar calendar = file.getTimestamp();
                    TimeZone tz = TimeZone.getTimeZone("GMT");
                    calendar.setTimeZone(tz);
                    Long lastTime = calendar.getTimeInMillis();
                    Long currentTime = new Date().getTime();
                    Long time = currentTime - lastTime;
                    Long fiveMinitues = 60000L;
                    if (file.getSize() == 0 && time > fiveMinitues)
                        return true;
                    return false;
                }
            });
            if (null != ftpFiles && ftpFiles.length > 0) {
                for (FTPFile ftpFile : ftpFiles) {
                    String pathName = ftpFile.getName();
                    if (ftpClient.deleteFile(pathName)) {
                        logger.info("file:{}删除成功", pathName);
                    } else {
                        logger.error(pathName + "删除失败");
                    }
                }
            }
            logger.info("delete empty file sucess");
        } catch (IOException e) {
            logger.error("delete empty file error", e);
        }
    }

    public String[] getPlayerFiles(String remoteDownLoadPath) {
        List<String> fileList = new ArrayList<>();
        try {
            ftpClient.enterLocalPassiveMode();
            ftpClient.changeWorkingDirectory(remoteDownLoadPath);
            String[] list = ftpClient.listNames();
            return list;
        } catch (IOException e) {
            logger.error("delete  file error", e);
            return null;
        }
    }



    public void moveAndDeleteSportradarFiles(String remoteDownLoadPath, String remoteDownLoadPathCopy) {
        try {
            ftpClient.enterLocalPassiveMode();
            ftpClient.changeWorkingDirectory(remoteDownLoadPath);
            FTPFile[] list = ftpClient.listFiles();

            Date d = new Date();
            Date fileDate = null;
            String[] lists = null;
            String s = "";
            String typeDir = "";
            String copyToDir = "";

            //需要处理的文件时间节点
            Date oldFileDate = LeDateUtils.addMonth(d, -3);

            ftpClient.makeDirectory(remoteDownLoadPathCopy);
            for (FTPFile ftpFile : list) {
                s = ftpFile.getName();
                fileDate = ftpFile.getTimestamp().getTime();
                lists = s.split("\\.");
                if (lists == null || lists.length < 1) continue;

                if (!"copy|livescore|copyold|oldData".contains(s)) {
                    if (!fileDate.before(oldFileDate)) {
                        continue;
                    }
                    typeDir = remoteDownLoadPathCopy + lists[0] + "//";
                    ftpClient.makeDirectory(typeDir);
                    if (lists.length > 1) copyToDir = typeDir + lists[1] + "//";
                    else copyToDir = typeDir;
                    ftpClient.makeDirectory(copyToDir);
                    boolean moved = ftpClient.rename(remoteDownLoadPath + s, copyToDir + s);
                    if (moved) {
                        deleteFile(s, remoteDownLoadPath);
                    } else {
                        logger.warn("file:{} move to ftp directory(copy)", s);
                    }
                }
            }

        } catch (Exception e) {
            logger.error("获取file名称失败", e);
        }
    }

    public void moveAndDeleteOldFiles(String remoteDownLoadPath, String remoteDownLoadPathCopy) {
        try {
            ftpClient.enterLocalPassiveMode();
            ftpClient.changeWorkingDirectory(remoteDownLoadPath);
            FTPFile[] list = ftpClient.listFiles();

            Date d = new Date();
            Date fileDate = null;
            String[] lists = null;
            String s = "";
            String typeDir = "";
            String copyToDir = "";

            ftpClient.makeDirectory(remoteDownLoadPathCopy);
            for (FTPFile ftpFile : list) {
                s = ftpFile.getName();
                if (s.contains("2015") || s.contains("1516") || s.contains("2014") || s.contains("1415")) {
                    fileDate = ftpFile.getTimestamp().getTime();
                    if (s.contains("copy") || s.contains("copyold") || s.contains("history") || s.contains("hk_share") || s.contains("statistics") || s.contains("livescore") || s.contains("oldData") || s.contains("odds")) {
                        // if (!"copy|copyold|history|hk_share|odds|statistics|livescore|copyold|oldData|".contains(s)) {
                        continue;
                    }
                    try {
                        boolean moved = ftpClient.rename(remoteDownLoadPath + s, copyToDir + s);
                        if (moved) {
                            deleteFile(s, remoteDownLoadPath);
                        } else {
                            logger.warn("file:{} move to ftp directory(copy)", s);
                        }
                    } catch (Exception e) {
                        logger.error("获取file名称失败", e);
                        continue;
                    }
                } else continue;
            }
        } catch (Exception e) {
            logger.error("获取file名称失败", e);
        }

    }

    public void moveInvalidFiles(String remoteDownLoadPath, String remoteDownLoadPathCopy) {
        try {
            ftpClient.enterLocalPassiveMode();
            ftpClient.changeWorkingDirectory(remoteDownLoadPath);
            FTPFile[] list = ftpClient.listFiles();

            Date d = new Date();
            Date fileDate = null;
            String[] lists = null;
            String s = "";
            String typeDir = "";
            String copyToDir = "";

            ftpClient.makeDirectory(remoteDownLoadPathCopy);
            for (FTPFile ftpFile : list) {
                s = ftpFile.getName();
//                for (String type : Constants.deleteFileType) {
//                    if (s.startsWith(type)) {
//                        typeDir = remoteDownLoadPathCopy + type + "//";
//                        ftpClient.makeDirectory(typeDir);
//                        try {
//                            boolean moved = ftpClient.rename(remoteDownLoadPath + s, typeDir + s);
//                            if (moved) {
//                                deleteFile(s, remoteDownLoadPath);
//                            } else {
//                                logger.warn("file:{} move to ftp directory(copy)", s);
//                            }
//                        } catch (Exception e) {
//                            logger.error("获取file名称失败", e);
//                            continue;
//                        }
//
//                    } else continue;
//                }
            }
        } catch (Exception e) {
            logger.error("获取file名称失败", e);
        }

    }

    public List<String> getNamesByReg(String reg, String remoteDownLoadPath) {
        List<String> list = new ArrayList<String>();
        try {
            ftpClient.enterLocalPassiveMode();
            ftpClient.changeWorkingDirectory(remoteDownLoadPath);
            String[] s = null;
            if (reg == null) {
                s = ftpClient.listNames();
            } else {
                s = ftpClient.listNames(reg);
            }

            if (s != null && s.length > 0) {
                for (int i = 0; i < s.length; i++) {
                    if (!matchFbDetailFiles1415.contains(s[i].substring(0, s[i].lastIndexOf(".xml")))) {
                        list.add(s[i]);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("获取file名称失败", e);
        }
        return list;
    }

    public List<String> getNamesBytype(String reg, String remoteDownLoadPath) {
        List<String> list = new ArrayList<String>();
        try {
            ftpClient.enterLocalPassiveMode();
            ftpClient.changeWorkingDirectory(remoteDownLoadPath);
            String[] s = null;

            s = ftpClient.listNames();

            if (s != null && s.length > 0) {
                for (int i = 0; i < s.length; i++) {
                    {
                        if (s[i].contains(reg)) {
                            list.add(s[i]);
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("获取file名称失败", e);
        }
        return list;
    }


    public boolean downloadFile(String remoteFileName, String localRootPath, String remoteDownLoadPath) throws
            IOException {
        String strFilePath = localRootPath + remoteFileName;
        BufferedOutputStream outStream = null;
        boolean success = false;
            ftpClient.enterLocalPassiveMode();
            ftpClient.changeWorkingDirectory(remoteDownLoadPath);
            FTPFile[] ftpFiles = ftpClient.listFiles(remoteFileName);
            if (ftpFiles.length != 1) {
                logger.warn("file:{} not exists on remote,localRootPath:{},remoteDownLoadPath:{}", remoteFileName, localRootPath, remoteDownLoadPath);
                return success;
            }
            outStream = new BufferedOutputStream(new FileOutputStream(strFilePath));
            if (ftpClient.retrieveFile(remoteFileName, outStream)) {
                logger.info("file:{}下载成功,最后修改时间：{}", remoteFileName, ftpClient.getModificationTime(remoteFileName));
                success = true;
            }
            if (null != outStream) {
                outStream.flush();
                outStream.close();
        }
        return success;
    }

    public boolean deleteFile(String remoteFileName, String remoteDownLoadPath) {
        boolean success = false;
        try {
            ftpClient.enterLocalPassiveMode();
            ftpClient.changeWorkingDirectory(remoteDownLoadPath);
            if (ftpClient.deleteFile(remoteFileName)) {
                logger.info("file:{}删除成功", remoteFileName);
                success = true;
            }
        } catch (Exception e) {
            logger.error(remoteFileName + "删除失败", e);
        }
        return success;
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
