package com.lesports.sms.cooperation.util;

import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.client.SopsInternalApis;
import com.lesports.sms.model.DictEntry;
import com.lesports.sms.model.Tag;
import com.lesports.utils.LeDateUtils;
import com.lesports.utils.LeProperties;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * Created by zhonglin on 2016/5/23.
 */
public class XmlUtil {

    //文件生成路径
//    private static final String ROOTPATH = "E:\\baidu\\";
//    private static final  String ROOTPATH = "/letv/data/hd/baidu/";
    private static final String UPLOADPATH = "/hd/baidu/";


    private static final Logger LOG = LoggerFactory.getLogger(XmlUtil.class);


    //上传文件
    public static  boolean uploadXmlFile(String file,String localPath,String uploadPath){
        String srHost = LeProperties.getString("aladdin.ftp.host");
        int srPort = LeProperties.getInt("aladdin.ftp.port", 0);
        String srUserName = LeProperties.getString("aladdin.ftp.userName");
        String srPassword = LeProperties.getString("aladdin.ftp.password");
        FtpUtil ftpUtil = new FtpUtil(srHost, srPort, srUserName, srPassword);
        try {
            LOG.info("uploadAladdinXmlFile execute begin");
            if (!ftpUtil.loginFtp(60)) {
                LOG.error("login aladding ftp: fail ");
                return false;
            }
            ftpUtil.uploadFile(localPath + file,file, uploadPath.replace("\\","/"));
        } catch (Exception e) {
            LOG.error("statsCal createXmlFile  error", e);
            return false;
        } finally {
            ftpUtil.logOutFtp();
        }
        return true;
    }


    public static  boolean uploadBaiduXmlFile(String file,String localPath,String uploadPath){
        String srHost = LeProperties.getString("baidu.ftp.host");
        int srPort = LeProperties.getInt("baidu.ftp.port", 0);
        String srUserName = LeProperties.getString("baidu.ftp.userName");
        String srPassword = LeProperties.getString("baidu.ftp.password");
        FtpUtil ftpUtil = new FtpUtil(srHost, srPort, srUserName, srPassword);
        try {
            LOG.info("uploadAladdinXmlFile execute begin");
            if (!ftpUtil.loginFtp(60)) {
                LOG.error("login aladding ftp: fail ");
                return false;
            }
            ftpUtil.uploadFile(localPath + file,file, uploadPath.replace("\\","/"));
        } catch (Exception e) {
            LOG.error("statsCal createXmlFile  error", e);
            return false;
        } finally {
            ftpUtil.logOutFtp();
        }
        return true;
    }


    public static String getWeekNick(Date date) {
        String weekNick = "";
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int week = cal.get(Calendar.DAY_OF_WEEK);
        switch (week) {
            case 1:
                weekNick = "周日";
                break;
            case 2:
                weekNick = "周一";
                break;
            case 3:
                weekNick = "周二";
                break;
            case 4:
                weekNick = "周三";
                break;
            case 5:
                weekNick = "周四";
                break;
            case 6:
                weekNick = "周五";
                break;
            case 7:
                weekNick = "周六";
                break;
        }
        return weekNick;
    }

    public static Format FormatXML() {
        Format format = Format.getCompactFormat();
        format.setEncoding("utf-8");
        format.setIndent(" ");
        return format;
    }

    public static   String formatMMSS(long seconds) {
        if (seconds <= 0) {
            return "00:00";
        }
        if (seconds < 10) {
            return "00:0"+seconds;
        }
        if (seconds < 60) {
            return "00:"+seconds;
        }
        StringBuilder result = new StringBuilder();
        long[] time = new long[2];
        time[0] =  seconds / 60 ; // 分
        time[1] = seconds % 60; // 秒
        boolean end = false;
        for (int i = 0; i < 2; i++) {
            long v = time[i];
            if (v == 0 && !end) {
                continue;
            }
            end = true;
            if (v < 10) {
                result.append("0");
            }
            result.append(v);
            if (i == 1) {
                return result.toString();
            }
            result.append(":");
        }
        return result.toString();
    }

    public static void createPath(String filePath) {
        String paths[] = filePath.split("/");
        String dir = paths[0];
        for (int i = 0; i < paths.length - 1; i++) {
            try {
                dir = dir + "/" + paths[i + 1];
                File dirFile = new File(dir);
                if (!dirFile.exists()) {
                    dirFile.mkdir();
                    LOG.info("创建目录为：" + dir);
                }
            } catch (Exception err) {
                LOG.info("ELS - Chart : 文件夹创建发生异常");
            }
        }
    }
}
