package com.lesports.sms.cooperation.service.alad;

import client.SopsApis;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lesports.api.common.CallerParam;
import com.lesports.api.common.PageParam;
import com.lesports.sms.api.common.NewsType;
import com.lesports.sms.api.service.GetRelatedNewsParam;
import com.lesports.sms.api.vo.TNews;
import com.lesports.sms.cooperation.util.Constants;
import com.lesports.sms.cooperation.util.OlyUtil;
import com.lesports.sms.cooperation.util.XmlUtil;
import com.lesports.utils.CallerUtils;
import com.lesports.utils.LeDateUtils;
import com.lesports.utils.LeStringUtils;
import com.lesports.utils.PageUtils;
import com.lesports.utils.math.LeNumberUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jdom.CDATA;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by zhonglin on 2016/9/6.
 */
@Service("oneboxS6StatsService")
public class OneboxS6StatsService {
    private static final Logger LOG = LoggerFactory.getLogger(OneboxS6StatsService.class);

    public  static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    public  static SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("MMddHHss");
    private static final String NEWS_URL = "http://sports.le.com/news/%s.html";
    private static final String M_NEWS_URL = "http://m.sports.le.com/news/%s.html";
    private static RestTemplate template =new RestTemplate();
    private static String S6_CH = "so_lol";

    //生成360新闻xml，并且上传到ftp服务器
    public void oneboxS6Stats() {
        String fileName = "onebox2016s6"+ Constants.fileextraname+".xml";
        Boolean flag = createXmlFile( Constants.filelocalpath ,fileName);
        LOG.info("onebox2016s6 begin path:{}", Constants.filelocalpath + fileName);
        //生成文件成功上传文件
        if(flag){
            XmlUtil.uploadXmlFile(fileName,  Constants.filelocalpath, Constants.fileuploadpath);
        }
        else{
            LOG.error("ftpXmlFile error file: {}" ,fileName);
        }
    }

    //生成xml文件
//    public boolean createXmlFile(String path,String file){
//        try{
//            Map<String,String> existNews = Maps.newHashMap();
//
//            Element root = new Element("DOCUMENT");
//            Document Doc  = new Document(root);
//            Element item = new Element("item");
//
//            Element key = new Element("key");
//            key.addContent(new CDATA("s6世界总决赛"));
//            item.addContent(key);
//
//            Element display = new Element("display");
//
//            Element top = new Element("top");
//            Element title = new Element("title");
//            title.addContent(new CDATA("2016年s6世界总决赛_乐视体育专题"));
//            top.addContent(title);
//            Element url = new Element("url");
//            url.addContent(new CDATA("http://www.lesports.com/column/dianjing/yxlm/index.shtml"));
//            top.addContent(url);
//            display.addContent(top);
//
//            //焦点图
//            JSONObject fileContent = template.getForObject(Constants.CMS_DATA_URL+"7120.json", JSONObject.class);
//            List<Map<String,Object>> contents = (List<Map<String,Object>>)fileContent.get("blockContent");
//            int size = 0;
//            if(CollectionUtils.isNotEmpty(contents)){
//                for (Map<String,Object> focus : contents) {
//                    size ++;
//                    Element focusElement = new Element("focus");
//
//                    Element image = new Element("image");
//                    image.addContent(new CDATA((String)focus.get("pic1")));
//                    focusElement.addContent(image);
//
//                    String titleStr = (String)focus.get("title");
//                    Element titleElement = new Element("title");
//                    titleElement.addContent(new CDATA(titleStr));
//                    focusElement.addContent(titleElement);
//
//                    Element urlElement = new Element("url");
//                    urlElement.addContent(new CDATA((String)focus.get("url")+"?ch="+S6_CH));
//                    focusElement.addContent(urlElement);
//                    display.addContent(focusElement);
//                    if(size==3)break;
//                }
//            }
//
//
//            //图集
//            fileContent = template.getForObject(Constants.CMS_DATA_URL+"7123.json", JSONObject.class);
//            contents = (List<Map<String,Object>>)fileContent.get("blockContent");
//            size = 0;
//            Element focusImg = new Element("focus_img");
//            Element focusVideo = new Element("focus_video");
//            if(CollectionUtils.isNotEmpty(contents)){
//                for (Map<String,Object> news : contents) {
//                    size ++;
//                    if(size==1){
//                        Element image = new Element("image");
//                        image.addContent(new CDATA((String)news.get("pic1")));
//                        focusImg.addContent(image);
//
//                        String titleStr = (String)news.get("title");
//                        Element titleElement = new Element("title");
//                        titleElement.addContent(new CDATA(titleStr));
//                        focusImg.addContent(titleElement);
//
//                        Element urlElement = new Element("url");
//                        urlElement.addContent(new CDATA((String)news.get("url")+"?ch="+S6_CH));
//                        focusImg.addContent(urlElement);
//                        display.addContent(focusImg);
//                    }
//                    else{
//                        Element list = new Element("list");
//                        Element image = new Element("image");
//                        image.addContent(new CDATA((String)news.get("pic1")));
//                        list.addContent(image);
//
//                        String titleStr = (String)news.get("title");
//                        Element titleElement = new Element("title");
//                        titleElement.addContent(new CDATA(titleStr));
//                        list.addContent(titleElement);
//
//                        Element urlElement = new Element("url");
//                        urlElement.addContent(new CDATA((String)news.get("url")+"?ch="+S6_CH));
//                        list.addContent(urlElement);
//
//                        focusVideo.addContent(list);
//                    }
//                }
//            }
//            display.addContent(focusVideo);
//
//
//            Element showNews = new Element("show_news");
//
//            //最热新闻
//            fileContent = template.getForObject(Constants.CMS_DATA_URL+"7126.json", JSONObject.class);
//            contents = (List<Map<String,Object>>)fileContent.get("blockContent");
//            size = 0;
//
//            if(!CollectionUtils.isEmpty(contents)){
//                for (Map<String,Object> news : contents) {
//                    size ++;
//                    if(size==1){
//
//                        Element showHot = new Element("show_hot");
//
//                        Element image = new Element("image");
//                        image.addContent(new CDATA((String)news.get("pic1")));
//
//                        showHot.addContent(image);
//
//                        String titleStr = (String)news.get("title");
//                        Element titleElement = new Element("title");
//                        titleElement.addContent(new CDATA(titleStr));
//                        showHot.addContent(titleElement);
//
//                        String descStr = (String)news.get("shorDesc");
//                        if(StringUtils.isNotBlank(descStr)){
//                            int pos = descStr.indexOf("（编辑");
//                            if(pos>0){
//                                descStr = descStr.substring(0,pos);
//                            }
//                            else{
//                                pos = descStr.indexOf("(编辑");
//                                if(pos>0){
//                                    descStr = descStr.substring(0,pos);
//                                }
//                            }
//                        }
//
//                        LOG.info("new  desc: {} ",descStr );
//
//                        Element descElement = new Element("desc");
//                        descElement.addContent(new CDATA(descStr));
//                        showHot.addContent(descElement);
//
//                        Element urlElement = new Element("url");
//                        urlElement.addContent(new CDATA((String)news.get("url")+"?ch="+S6_CH));
//                        showHot.addContent(urlElement);
//
//                        Element mUrlElement = new Element("m_url");
//                        mUrlElement.addContent(new CDATA((String)news.get("url")+"?ch="+S6_CH));
//                        showHot.addContent(mUrlElement);
//
//                        Element pdate = new Element("pdate");
//                        Date date = new Date();
//                        date.setTime((Long)news.get("ctime"));
//                        pdate.addContent(new CDATA(LeDateUtils.formatYMDHMS(date)));
//                        showHot.addContent(pdate);
//
//                        showNews.addContent(showHot);
//                    }
//                    else{
//                        Element showList = new Element("show_list");
//
//                        String titleStr = (String)news.get("title");
//                        Element titleElement = new Element("title");
//                        titleElement.addContent(new CDATA(titleStr));
//                        showList.addContent(titleElement);
//
//                        Element urlElement = new Element("url");
//                        urlElement.addContent(new CDATA(String.format((String)news.get("url")+"?ch="+S6_CH)));
//                        showList.addContent(urlElement);
//
//
//                        Element pdate = new Element("pdate");
//                        Date date = new Date();
//                        date.setTime((Long)news.get("ctime"));
//                        pdate.addContent(new CDATA(LeDateUtils.formatYMDHMS(date)));
//                        showList.addContent(pdate);
//                        showNews.addContent(showList);
//                    }
//
//                }
//            }
//
//            Element moretxt = new Element("moretxt");
//            moretxt.addContent(new CDATA("查看更多>>"));
//            showNews.addContent(moretxt);
//
//            Element moreurl = new Element("moreurl");
//            moreurl.addContent((new CDATA("http://www.lesports.com/column/dianjing/yxlm/index.shtml?ch="+S6_CH)));
//            showNews.addContent(moreurl);
//
//            Element mMoreurl = new Element("m_moreurl");
//            mMoreurl.addContent((new CDATA("http://www.lesports.com/column/dianjing/yxlm/index.shtml?ch="+S6_CH)));
//            showNews.addContent(mMoreurl);
//
//            display.addContent(showNews);
//
//            Element uptime = new Element("uptime");
//            uptime.addContent(new CDATA(LeDateUtils.formatYMDHMS(new Date())));
//            display.addContent(uptime);
//
//            item.addContent(display);
//            root.addContent(item);
//
//            File existPath = new File(path);
//            if(!existPath.exists()){
//                XmlUtil.createPath(path);
//            }
//
//            XMLOutputter XMLOut = new XMLOutputter(XmlUtil.FormatXML());
//            XMLOut.output(Doc, new FileOutputStream(path+file));
//        }
//        catch (Exception e){
//            LOG.error("oneboxS6Stats createXmlFile  error", e);
//            return false;
//        }
//        return true;
//    }

    //生成xml文件
    public boolean createXmlFile(String path,String file){
        try{
            Element root = new Element("DOCUMENT");
            Document Doc  = new Document(root);
            Element item = new Element("item");

            Element key = new Element("key");
            key.addContent(new CDATA("s6世界总决赛"));
            item.addContent(key);
            Element display = new Element("display");

            //焦点图
            JSONObject fileContent = template.getForObject(Constants.CMS_DATA_URL+"7120.json", JSONObject.class);
            List<Map<String,Object>> contents = (List<Map<String,Object>>)fileContent.get("blockContent");
            int size = 0;
            if(CollectionUtils.isNotEmpty(contents)){
                for (Map<String,Object> focus : contents) {
                    size ++;
                    Element focusElement = new Element("focus");

                    Element image = new Element("image");
                    image.addContent(new CDATA((String)focus.get("pic1")));
                    focusElement.addContent(image);

                    String titleStr = (String)focus.get("title");
                    Element titleElement = new Element("title");
                    titleElement.addContent(new CDATA(titleStr));
                    focusElement.addContent(titleElement);

                    Element urlElement = new Element("url");
                    urlElement.addContent(new CDATA((String)focus.get("url")+"?ch="+S6_CH));
                    focusElement.addContent(urlElement);
                    display.addContent(focusElement);
                    if(size==5)break;
                }
            }

            Element showNews = new Element("show_news");

            //最热新闻
            fileContent = template.getForObject(Constants.CMS_DATA_URL+"7126.json", JSONObject.class);
            contents = (List<Map<String,Object>>)fileContent.get("blockContent");
            size = 0;

            if(!CollectionUtils.isEmpty(contents)){
                for (Map<String,Object> news : contents) {
                    size ++;
                    if(size==1){

                        Element head = new Element("head");

                        String titleStr = (String)news.get("title");
                        Element titleElement = new Element("title");
                        titleElement.addContent(new CDATA(titleStr));
                        head.addContent(titleElement);

//                        Element image = new Element("image");
//                        image.addContent(new CDATA((String)news.get("pic1")));
//                        head.addContent(image);


                        String review = (String)news.get("shorDesc");
                        if(StringUtils.isNotBlank(review)){
                            int pos = review.indexOf("（编辑");
                            if(pos>0){
                                review = review.substring(0,pos);
                            }
                            else{
                                pos = review.indexOf("(编辑");
                                if(pos>0){
                                    review = review.substring(0,pos);
                                }
                            }
                        }

                        LOG.info("new  review: {} ",review );

                        Element reviewElement = new Element("review");
                        reviewElement.addContent(new CDATA(review));
                        head.addContent(reviewElement);

                        Element urlElement = new Element("url");
                        urlElement.addContent(new CDATA((String)news.get("url")+"?ch="+S6_CH));
                        head.addContent(urlElement);

                        showNews.addContent(head);
                    }
                    else{
                        Element list = new Element("list");

                        String titleStr = (String)news.get("title");
                        Element titleElement = new Element("title");
                        titleElement.addContent(new CDATA(titleStr));
                        list.addContent(titleElement);

                        Element urlElement = new Element("url");
                        urlElement.addContent(new CDATA(String.format((String)news.get("url")+"?ch="+S6_CH)));
                        list.addContent(urlElement);

                        showNews.addContent(list);
                    }

                }
            }


            Element newsMore = new Element("news_more");
            newsMore.addContent((new CDATA("http://www.lesports.com/column/dianjing/yxlm/index.shtml?ch="+S6_CH)));
            showNews.addContent(newsMore);


            display.addContent(showNews);

            item.addContent(display);
            root.addContent(item);

            File existPath = new File(path);
            if(!existPath.exists()){
                XmlUtil.createPath(path);
            }

            XMLOutputter XMLOut = new XMLOutputter(XmlUtil.FormatXML());
            XMLOut.output(Doc, new FileOutputStream(path+file));
        }
        catch (Exception e){
            LOG.error("oneboxS6Stats createXmlFile  error", e);
            return false;
        }
        return true;
    }
}
