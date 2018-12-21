package com.lesports.sms.cooperation.util;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;


import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by zhonglin on 2016/7/4.
 */
public class OlyUtil {
    //开幕式、闭幕式
    public static final Map<String, String> olyCeremonyMap = new HashMap<String, String>();
    //奥运场馆
    public static final Map<String, String> venuesMap = new HashMap<String, String>();
    //奥运场馆
//    public static final Map<String, String> venuesTagMap = new HashMap<String, String>();
    //奥运大项标签
    public static final Map<String, String> eventMap = new HashMap<String, String>();
    //奥运大项标签
//    public static final Map<String, String> eventTagMap = new HashMap<String, String>();

    //奥运赛事id
    public static final Long OLY_COMPETITION_ID = 100507001L;
    //中国字典id
    public static final Long OLY_CHINA_COUNTY_ID = 114588000L;

    //战报tagId
    public static final Long OLY_REPORT_TAG_ID = 100491008L;
    //夺金tagId
    public static final Long OLY_GOLD_TAG_ID = 100608008L;
    //奥运tagId
    public static final Long OLY_OLY_TAG_ID = 100119008L;
    //奥运运动员/国家队 名单地址
    public static final String OLY_MEMBER_URL = "http://v.api.le.com/hd/oly/member.xml";


//    // pc 主页
//    public static final String OLY_PC_INDEX_URL = "http://2016.lesports.com?ch="+OLY_SOGOU_CH;
    //pc新闻主页
    public static final String OLY_PC_NEWS_URL = "http://2016.lesports.com/news";
//    //pc赛程页
//    public static final String OLY_PC_SCHEDULE_URL = "http://2016.lesports.com/schedule?ch="+OLY_SOGOU_CH;
//    //pc每日赛程
////    public static final String OLY_PC_INDEX_URL = "http://www.lesports.com/olmpics2016/schedule/";
//    //pc奖牌榜页
//    public static final String OLY_PC_MEDALS_URL = "http://2016.lesports.com/medals?ch="+OLY_SOGOU_CH;
//    //pc成绩页
//    public static final String OLY_PC_RESULTS_URL = "http://2016.lesports.com/results?ch="+OLY_SOGOU_CH;
//    //pc出场名单页
//    public static final String OLY_PC_ENTRYLIST_URL = "http://2016.lesports.com/entrylist?ch="+OLY_SOGOU_CH;
//    //pc项目列表页
//    public static final String OLY_PC_SPORTS_URL = "http://2016.lesports.com/sports?ch="+OLY_SOGOU_CH;
//    //pc项目单项页
//    public static final String OLY_PC_SINGLE_SCHEDULE_URL = "http://2016.lesports.com/sports_";
//    //pc中国军团
//    public static final String OLY_PC_CHINA_URL = "http://2016.lesports.com/chin?ch="+OLY_SOGOU_CH;
//    //pc图集
//    public static final String OLY_PC_PICTURE_URL = "http://2016.lesports.com/pictures?ch="+OLY_SOGOU_CH;
//
//
//
//    //m站主页
//    public static final String OLY_MOBILE_INDEX_URL = "http://m.lesports.com/2016?ch="+OLY_SOGOU_CH;
    //m站新闻主页
    public static final String OLY_MOBILE_NEWS_URL = "http://m.lesports.com/2016/news";
//    //m站赛程页
//    public static final String OLY_MOBILE_SCHEDULE_URL = "http://m.sports.le.com/2016/schedule?ch="+OLY_SOGOU_CH;
//    //m站每日赛程
////    public static final String OLY_MOBILE_INDEX_URL = "http://m.sports.le.com/2016/schedule_";
//    //m站奖牌榜页
//    public static final String OLY_MOBILE_MEDALS_URL = "http://m.sports.le.com/2016/medals?ch="+OLY_SOGOU_CH;
//    //m站成绩页
////    public static final String OLY_MOBILE_RESULTS_URL = "http://m.sports.le.com/2016/results?ch="+OLY_SOGOU_CH;
//    //m站出场名单页
////    public static final String OLY_MOBILE_ENTRYLIST_URL = "http://m.sports.le.com/2016/entrylist?ch="+OLY_SOGOU_CH;
//    //m站项目列表页
//    public static final String OLY_MOBILE_SPORTS_URL = "http://m.sports.le.com/2016/sports?ch="+OLY_SOGOU_CH;
//    //m站项目单项页
////    public static final String OLY_MOBILE_SINGLE_SCHEDULE_URL = "http://m.sports.le.com/2016/sports_";
//    //m站中国军团
//    public static final String OLY_MOBILE_CHINA_URL = "http://m.sports.le.com/2016/china?ch="+OLY_SOGOU_CH;
//    //m站图集
////    public static final String OLY_MOBILE_PICTURE_URL = "http://m.sports.le.com/2016/pictures?ch="+OLY_SOGOU_CH;
//
//
//    //pc金牌赛程
//    public static final String OLY_PC_GOLD_URL = "http://www.lesports.com:9850/2016/searchschedule?ch="+OLY_SOGOU_CH+"#filter/date/20160603/country/114588000";
//    //pc比赛直播页
//    public static final String OLY_PC_MATCH_URL = "http://2016.lesports.com/match/";
//    //pc比赛直播页
//    public static final String OLY_PC_VIDEOS_URL = "http://so.lesports.com/s?wd=奥运&ch="+OLY_SOGOU_CH;
//    //pc视频播放页
//    public static final String OLY_PC_VIDEO_URL = "http://2016.lesports.com/video/";
//    //pc开幕式
//    public static final String OLY_PC_OPENING_URL = "http://2016.lesports.com?ch="+OLY_SOGOU_CH;;
//    //pc闭幕式
//    public static final String OLY_PC_CLOSING_URL = "http://2016.lesports.com?ch="+OLY_SOGOU_CH;
//    //pc冠军面对面
//    public static final String OLY_PC_GOLD_INTERVIEW = "";
//
//    //m站金牌赛程
//    public static final String OLY_MOBILE_GOLD_URL = "http://m.lesports.com/olmpic2016/schedule?ch="+OLY_SOGOU_CH;
//    //m站比赛直播页
//    public static final String OLY_MOBILE_MATCH_URL = "http://m.lesports.com/2016/match/";
//    //m站视频频道页
//    public static final String OLY_MOBILE_VIDEOS_URL = "http://so.lesports.com/s?wd=奥运&ch="+OLY_SOGOU_CH;
//    //m站视频播放页
//    public static final String OLY_MOBILE_VIDEO_URL  = "http://m.lesports.com/2016/video/";
//
//    //mobile开幕式
//    public static final String OLY_MOBILE_OPENING_URL = "http://2016.lesports.com?ch="+OLY_SOGOU_CH;
//    //mobile闭幕式
//    public static final String OLY_MOBILE_CLOSING_URL = "http://2016.lesports.com?ch="+OLY_SOGOU_CH;
//    //pc冠军面对面
//    public static final String OLY_MOBILE_GOLD_INTERVIEW = "";

    static {
        //奥运名词

//        olyCeremonyMap.put("100601008","开幕式");
//        olyCeremonyMap.put("100503008","闭幕式");

        olyCeremonyMap.put("开幕式","100601008");
        olyCeremonyMap.put("闭幕式","100503008");
        olyCeremonyMap.put("奥运场馆","100714008");

        //奥运场馆
        venuesMap.put("马拉卡纳体育场","100516008");
        venuesMap.put("奥林匹克水上中心","100516008");
        venuesMap.put("里约奥林匹克体育场","100715008");
        venuesMap.put("卡里奥卡竞技场","100716008");
        venuesMap.put("里约会展中心","100517008");
//        venuesMap.put("奥运场馆","100714008");

//        venuesTagMap.put("100516008","马拉卡纳体育场");
//        venuesTagMap.put("100516008","奥林匹克水上中心");
//        venuesTagMap.put("100715008","里约奥林匹克体育场");
//        venuesTagMap.put("100716008","卡里奥卡竞技场");
//        venuesTagMap.put("100517008","里约会展中心");
//        venuesTagMap.put("100714008","奥运场馆");


        //大项标签
        eventMap.put("自行车","100019008");  //只是自行车
        eventMap.put("帆船","100599008");
        eventMap.put("七人制橄榄球","100598008");  //橄榄球
        eventMap.put("高尔夫","100592008");   //奥运会（高尔夫）
        eventMap.put("花样游泳","100703008");
        eventMap.put("击剑","100699008");
        eventMap.put("皮划艇","100697008");
        eventMap.put("举重","100502008");
        eventMap.put("篮球","100014008");
        eventMap.put("马术","100025008");
        eventMap.put("水球","100704008");
        eventMap.put("排球","100097008");
        eventMap.put("乒乓球","100115008");
        eventMap.put("曲棍球","100700008");
        eventMap.put("拳击","100076008");
        eventMap.put("柔道","100701008");
        eventMap.put("赛艇","100702008");
        eventMap.put("沙滩排球","100596008");
        eventMap.put("射击","100499008");
        eventMap.put("射箭","100497008");
        eventMap.put("手球","100597008");
        eventMap.put("摔跤","100077008");
        eventMap.put("跆拳道","100500008");
        eventMap.put("蹦床","100501008");
        eventMap.put("体操","100108008");
        eventMap.put("田径","100096008");
        eventMap.put("跳水","114768000");
        eventMap.put("铁人三项","100600008");
        eventMap.put("网球","100021008");
        eventMap.put("现代五项","100498008");
        eventMap.put("艺术体操","100695008");
        eventMap.put("游泳","100106008");
        eventMap.put("羽毛球","100067008");
        eventMap.put("足球","100088008");

//        eventTagMap.put("100019008","自行车");  //只是自行车
//        eventTagMap.put("100599008","帆船");
//        eventTagMap.put("100598008","七人制橄榄球");  //橄榄球
//        eventTagMap.put("100592008","高尔夫");   //奥运会（高尔夫）
//        eventTagMap.put("100703008","花样游泳");
//        eventTagMap.put("100699008","击剑");
//        eventTagMap.put("100697008","皮划艇");
//        eventTagMap.put("100502008","举重");
//        eventTagMap.put("100014008","篮球");
//        eventTagMap.put("100025008","马术");
//        eventTagMap.put("100704008","水球");
//        eventTagMap.put("100097008","排球");
//        eventTagMap.put("100115008","乒乓球");
//        eventTagMap.put("100700008","曲棍球");
//        eventTagMap.put("100076008","拳击");
//        eventTagMap.put("100701008","柔道");
//        eventTagMap.put("100702008","赛艇");
//        eventTagMap.put("100596008","沙滩排球");
//        eventTagMap.put("100499008","射击");
//        eventTagMap.put("100497008","射箭");
//        eventTagMap.put("100597008","手球");
//        eventTagMap.put("100077008","摔跤");
//        eventTagMap.put("100500008","跆拳道");
//        eventTagMap.put("100501008","蹦床");
//        eventTagMap.put("100108008","体操");
//        eventTagMap.put("100096008","田径");
//        eventTagMap.put("114768000","跳水");
//        eventTagMap.put("100600008","铁人三项");
//        eventTagMap.put("100021008","网球");
//        eventTagMap.put("100498008","现代五项");
//        eventTagMap.put("100695008","艺术体操");
//        eventTagMap.put("100106008","游泳");
//        eventTagMap.put("100067008","羽毛球");
//        eventTagMap.put("100088008","足球");

    }

    public static Map<String,String> getMemberTagMap(){
        Map<String,String> memberTagMap = Maps.newHashMap();

        try {
            URL url = new URL(OLY_MEMBER_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.connect();
            InputStream stream = conn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    stream, "UTF-8"));
            StringBuffer document = new StringBuffer();
            String line = null;
            while ((line = reader.readLine()) != null) {
                document.append(line +"\n");
            }
            String text = document.toString().replace("﻿<","<");

            Document doc = DocumentHelper.parseText(text);
            Element root = doc.getRootElement();
            Iterator<Element> items = root.elementIterator("item");
            while (items.hasNext()) {
                Element item = items.next();
                memberTagMap.put(item.element("name").getTextTrim(),item.element("id").getTextTrim());
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println("memberTagMap: " + memberTagMap);
        return memberTagMap;
    }

    public static Map<String,String> getMemberMap(){
        Map<String,String> memberMap = Maps.newHashMap();

        try {
            URL url = new URL(OLY_MEMBER_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.connect();
            InputStream stream = conn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    stream, "UTF-8"));
            StringBuffer document = new StringBuffer();
            String line = null;
            while ((line = reader.readLine()) != null) {
                document.append(line +"\n");
            }
            String text = document.toString().replace("﻿<","<");
            System.out.println("text: " + text);
            Document doc = DocumentHelper.parseText(text);
            Element root = doc.getRootElement();
            Iterator<Element> items = root.elementIterator("item");
            while (items.hasNext()) {
                Element item = items.next();
                memberMap.put(item.element("name").getTextTrim(),item.element("id").getTextTrim());
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return memberMap;
    }

    public static Map<String,String> getMemberIdMap(){
        Map<String,String> memberMap = Maps.newHashMap();

        try {
            URL url = new URL(OLY_MEMBER_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.connect();
            InputStream stream = conn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    stream, "UTF-8"));
            StringBuffer document = new StringBuffer();
            String line = null;
            while ((line = reader.readLine()) != null) {
                document.append(line +"\n");
            }
            String text = document.toString().replace("﻿<","<");
            System.out.println("text: " + text);
            Document doc = DocumentHelper.parseText(text);
            Element root = doc.getRootElement();
            Iterator<Element> items = root.elementIterator("item");
            while (items.hasNext()) {
                Element item = items.next();
                memberMap.put(item.element("name").getTextTrim(),item.element("memberId").getTextTrim());
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return memberMap;
    }


    public static void main(String[] args) {
        Map<String, String> memberMap = getMemberTagMap();
        System.out.println("memberMap:"+memberMap);
    }

}
