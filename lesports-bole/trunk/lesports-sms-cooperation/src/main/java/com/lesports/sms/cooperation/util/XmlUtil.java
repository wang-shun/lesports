package com.lesports.sms.cooperation.util;

import com.lesports.sms.api.common.MatchStatus;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.client.SopsInternalApis;
import com.lesports.sms.model.DictEntry;
import com.lesports.sms.model.Match;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


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
//        String srHost = LeProperties.getString("aladdin.ftp.host");
//        int srPort = LeProperties.getInt("aladdin.ftp.port", 0);
//        String srUserName = LeProperties.getString("aladdin.ftp.userName");
//        String srPassword = LeProperties.getString("aladdin.ftp.password");
//        FtpUtil ftpUtil = new FtpUtil(srHost, srPort, srUserName, srPassword);
//        try {
//            LOG.info("uploadAladdinXmlFile execute begin");
//            if (!ftpUtil.loginFtp(60)) {
//                LOG.error("login aladding ftp: fail ");
//                return false;
//            }
//            ftpUtil.uploadFile(localPath + file,file, uploadPath.replace("\\","/"));
//        } catch (Exception e) {
//            LOG.error("statsCal createXmlFile  error", e);
//            return false;
//        } finally {
//            ftpUtil.logOutFtp();
//        }

        String srHost1 = LeProperties.getString("aladdin.ftp.host1");
        int srPort1 = LeProperties.getInt("aladdin.ftp.port1", 0);
        String srUserName1 = LeProperties.getString("aladdin.ftp.userName1");
        String srPassword1 = LeProperties.getString("aladdin.ftp.password1");
        FtpUtil ftpUtil1 = new FtpUtil(srHost1, srPort1, srUserName1, srPassword1);
        try {
            LOG.info("uploadAladdinXmlFile new1 execute begin");
            if (!ftpUtil1.loginFtp(60)) {
                LOG.error("login aladding new1 ftp: fail ");
                return false;
            }
            ftpUtil1.uploadFile(localPath + file,file, uploadPath.replace("\\","/"));
        } catch (Exception e) {
            LOG.error("statsCal createXmlFile new1  error", e);
            return false;
        } finally {
            ftpUtil1.logOutFtp();
        }

        String srHost2 = LeProperties.getString("aladdin.ftp.host2");
        int srPort2 = LeProperties.getInt("aladdin.ftp.port2", 0);
        String srUserName2 = LeProperties.getString("aladdin.ftp.userName2");
        String srPassword2 = LeProperties.getString("aladdin.ftp.password2");
        FtpUtil ftpUtil2 = new FtpUtil(srHost2, srPort2, srUserName2, srPassword2);
        try {
            LOG.info("uploadAladdinXmlFile new2 execute begin");
            if (!ftpUtil2.loginFtp(60)) {
                LOG.error("login aladding new2 ftp: fail ");
                return false;
            }
            ftpUtil2.uploadFile(localPath + file,file, uploadPath.replace("\\","/"));
        } catch (Exception e) {
            LOG.error("statsCal createXmlFile new2  error", e);
            return false;
        } finally {
            ftpUtil2.logOutFtp();
        }
        return true;
    }


    public static  boolean uploadBaiduXmlFile(String file,String localPath,String uploadPath){
        String srHost1 = LeProperties.getString("baidu.ftp.host1");
        int srPort1 = LeProperties.getInt("baidu.ftp.port1", 0);
        String srUserName1 = LeProperties.getString("baidu.ftp.userName1");
        String srPassword1 = LeProperties.getString("baidu.ftp.password1");
        FtpUtil ftpUtil1 = new FtpUtil(srHost1, srPort1, srUserName1, srPassword1);
        try {
            LOG.info("uploadBaiduXmlFile new1 execute begin");
            if (!ftpUtil1.loginFtp(60)) {
                LOG.error("login baidu new1 ftp: fail ");
                return false;
            }
            ftpUtil1.uploadFile(localPath + file,file, uploadPath.replace("\\","/"));
        } catch (Exception e) {
            LOG.error("statsCal createXmlFile new1  error", e);
            return false;
        } finally {
            ftpUtil1.logOutFtp();
        }

        String srHost2 = LeProperties.getString("baidu.ftp.host2");
        int srPort2 = LeProperties.getInt("baidu.ftp.port2", 0);
        String srUserName2 = LeProperties.getString("baidu.ftp.userName2");
        String srPassword2 = LeProperties.getString("baidu.ftp.password2");
        FtpUtil ftpUtil2 = new FtpUtil(srHost2, srPort2, srUserName2, srPassword2);
        try {
            LOG.info("uploadBaiduXmlFile new2 execute begin");
            if (!ftpUtil2.loginFtp(60)) {
                LOG.error("login baidu new2 ftp: fail ");
                return false;
            }
            ftpUtil2.uploadFile(localPath + file,file, uploadPath.replace("\\","/"));
        } catch (Exception e) {
            LOG.error("statsCal createXmlFile new2  error", e);
            return false;
        } finally {
            ftpUtil2.logOutFtp();
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


    public static int daysBetween(String smdate,String bdate){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");
        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(sdf.parse(smdate));
            long time1 = cal.getTimeInMillis();
            cal.setTime(sdf.parse(bdate));
            long time2 = cal.getTimeInMillis();
            long between_days=(time2-time1)/(1000*3600*24);
            return Integer.parseInt(String.valueOf(between_days));
        } catch (Exception err) {
            LOG.info("计算两个日期相差天数出错,smdate:{} ,bdate: {} ",smdate,bdate);
        }
        return 0;
    }

    public static String getScore(MatchStatus status, String homeScore, String awayScore) {
        if (status.toString().equals("MATCH_NOT_START")) {
            return "VS";

        } else return homeScore + "-" + awayScore;
    }

    public static String getMatchTime(String time) {
        if (time == null) return "00:00";
        String hour = time.substring(8, 10);
        String minute = time.substring(10, 12);
        return hour + ":" + minute;
    }

    public static String getMatchDate(String time) {
        String month = time.substring(4, 6);
        String day = time.substring(6, 8);
        return month + "-" + day;
    }

    public static String getLogo(String logo) {
        if (StringUtils.isBlank(logo)) return "";
        int poi = logo.lastIndexOf(".");
        if(poi>0){
            String validLogo = logo.substring(0,poi)+"/11_100_100" +logo.substring(poi);
            return validLogo;
        }
        return logo;
    }

    public static String getRoundDate(List<Match> matches,String roundName) {
        if(CollectionUtils.isEmpty(matches)) return  roundName;
        String roundDate = roundName;
        if(matches.size()>1){
            Match firstMatch = matches.get(0);
            Match lastMatch = matches.get(matches.size()-1);
            roundDate = roundName+"(" + firstMatch.getStartDate().substring(4,6)+"/"+firstMatch.getStartDate().substring(6,8);
            roundDate += "-"+ lastMatch.getStartDate().substring(4,6)+"/"+lastMatch.getStartDate().substring(6,8) +")";
        }
        else{
            return roundName+"(" +matches.get(0).getStartDate().substring(4,6)+"/"+matches.get(0).getStartDate().substring(6,8)+")";
        }
        return roundDate;
    }

    public static int getRoundNum(String round) {
        int i = 0;
        Pattern pattern = Pattern.compile("第(.+?)轮");
        Matcher matcher = pattern.matcher(round);
        if(matcher.find()){
            i = Integer.parseInt(matcher.group(1));
        }
        return i;
    }

}
