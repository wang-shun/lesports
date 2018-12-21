package com.lesports.sms.cooperation.service.olympic;

import client.SopsApis;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lesports.api.common.CallerParam;
import com.lesports.api.common.PageParam;
import com.lesports.sms.api.common.NewsType;
import com.lesports.sms.api.common.TTag;
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
 * Created by zhonglin on 2016/7/27.
 */
@Service("olyNews360Service")
public class OlyNews360Service {
    private static final Logger LOG = LoggerFactory.getLogger(OlyNews360Service.class);

    public  static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    public  static SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("MMddHHss");
    private static final String NEWS_URL = "http://sports.le.com/news/%s.html";
    private static final String M_NEWS_URL = "http://m.sports.le.com/news/%s.html";
    private static RestTemplate template =new RestTemplate();

    //生成360新闻xml，并且上传到ftp服务器
    public void olyNews360() {
        String fileName = "oly_news_360"+ Constants.fileextraname+".xml";
        Boolean flag = createXmlFile(Constants.fileolyrootpath ,fileName);
        LOG.info("OlyNews360 begin path:{}",Constants.fileolyrootpath + fileName);
        //生成文件成功上传文件
        if(flag){
            XmlUtil.uploadXmlFile(fileName, Constants.fileolyrootpath, Constants.fileolyuploadpath);
        }
        else{
            LOG.error("ftpXmlFile error file: {}" ,fileName);
        }
    }

    //生成xml文件
    public boolean createXmlFile(String path,String file){
        try{
            Map<String,String> existNews = Maps.newHashMap();

            Element root = new Element("DOCUMENT");
            Document Doc  = new Document(root);
            Element item = new Element("item");

            Element key = new Element("key");
            key.addContent(new CDATA("奥运"));
            item.addContent(key);

            Element display = new Element("display");

            Element top = new Element("top");
            Element title = new Element("title");
            title.addContent(new CDATA("2016年巴西里约奥运会_乐视体育专题"));
            top.addContent(title);
            Element url = new Element("url");
            url.addContent(new CDATA("http://2016.lesports.com/"));
            top.addContent(url);
            display.addContent(top);

            //焦点图
            JSONObject fileContent = template.getForObject(Constants.CMS_DATA_URL+"5826.json", JSONObject.class);
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
//                    if(titleStr.length()>16){
//                        titleStr = titleStr.substring(0,16);
//                    }
                    Element titleElement = new Element("title");
                    titleElement.addContent(new CDATA(titleStr));
                    focusElement.addContent(titleElement);

                    Element urlElement = new Element("url");
                    urlElement.addContent(new CDATA((String)focus.get("url")+"?ch="+Constants.OLY_360_NEW_CH));
                    existNews.put((String)focus.get("url"),(String)focus.get("url"));
                    focusElement.addContent(urlElement);
                    display.addContent(focusElement);
                    if(size==3)break;
                }
            }


            //图集
            GetRelatedNewsParam p = new GetRelatedNewsParam();
            p.setRelatedId(OlyUtil.OLY_OLY_TAG_ID);
            List<NewsType> typeList = Lists.newArrayList(NewsType.findByValue(3));
            p.setTypes(typeList);
            PageParam page = PageUtils.createPageParam(0,4);
            CallerParam caller = CallerUtils.getDefaultCaller();
            List<Long> ids = SopsApis.getNewsIdsByRelatedId(p, page, caller);

            if(!CollectionUtils.isEmpty(ids)){
                List<TNews> newses = SopsApis.getTNewsByIds(ids, caller);
                if(CollectionUtils.isNotEmpty(newses)){
                    for(TNews news:newses){
                        if(existNews.get(String.format(NEWS_URL,news.getId()))!=null){
                            continue;
                        }
                        size ++;
                        Element focusImg = new Element("focus_img");

                        Element image = new Element("image");
                        image.addContent(new CDATA(news.getCoverImage().replace(".jpg","/169_180_93.jpg")));
                        focusImg.addContent(image);

                        String titleStr = news.getName();
//                        if(titleStr.length()>16){
//                            titleStr = titleStr.substring(0,16);
//                        }
                        Element titleElement = new Element("title");
                        titleElement.addContent(new CDATA(titleStr));
                        focusImg.addContent(titleElement);

                        Element urlElement = new Element("url");
                        urlElement.addContent(new CDATA(String.format(NEWS_URL,news.getId())+"?ch="+Constants.OLY_360_NEW_CH));
                        focusImg.addContent(urlElement);
                        existNews.put(String.format(NEWS_URL,news.getId()),String.format(NEWS_URL,news.getId()));
                        display.addContent(focusImg);
                        break;
                    }
                }
            }

            //视频
            p = new GetRelatedNewsParam();
            p.setRelatedId(OlyUtil.OLY_OLY_TAG_ID);
            typeList = Lists.newArrayList(NewsType.findByValue(1));
            p.setTypes(typeList);
            page = PageUtils.createPageParam(0,5);
            ids = SopsApis.getNewsIdsByRelatedId(p, page, caller);

            size = 0;
            if(!CollectionUtils.isEmpty(ids)){
                List<TNews> newses = SopsApis.getTNewsByIds(ids, caller);
                Element focusVideo = new Element("focus_video");
                if(CollectionUtils.isNotEmpty(newses)){
                    for(TNews news:newses){
                        if(existNews.get(String.format(NEWS_URL,news.getId()))!=null){
                            continue;
                        }
                        if(news.getVideoImages().get("400*300")==null){
                            continue;
                        }
                        size ++;
                        Element list = new Element("list");

                        Element image = new Element("image");
                        image.addContent(new CDATA(news.getVideoImages().get("400*300")));
                        list.addContent(image);

                        String titleStr = news.getName();
//                        if(titleStr.length()>16){
//                            titleStr = titleStr.substring(0,16);
//                        }
                        Element titleElement = new Element("title");
                        titleElement.addContent(new CDATA(titleStr));
                        list.addContent(titleElement);

                        Element urlElement = new Element("url");
                        urlElement.addContent(new CDATA(String.format(NEWS_URL,news.getId())+"?ch="+Constants.OLY_360_NEW_CH));
                        list.addContent(urlElement);
                        existNews.put(String.format(NEWS_URL,news.getId()),String.format(NEWS_URL,news.getId()));

                        focusVideo.addContent(list);
                        if(size==2){
                            break;
                        }
                    }
                }
                display.addContent(focusVideo);
            }

            Element showNews = new Element("show_news");

            //最热新闻
            int hotSize = 0;
            p = new GetRelatedNewsParam();
            p.setRelatedId(OlyUtil.OLY_OLY_TAG_ID);
            List<Long> typeArray = LeStringUtils.commaString2LongList("0,2,3");
            typeList = Lists.newArrayList();
            for (Long typeValue : typeArray) {
                NewsType t = NewsType.findByValue(LeNumberUtils.toInt(typeValue));
                if (t != null) {
                    typeList.add(t);
                }
            }
            p.setTypes(typeList);
            page = PageUtils.createPageParam(0,10);
            ids = SopsApis.getNewsIdsByRelatedId(p, page, caller);

            if(!CollectionUtils.isEmpty(ids)){
                List<TNews> newses = SopsApis.getTNewsByIds(ids, caller);
                if(CollectionUtils.isNotEmpty(newses)){
                    for(TNews news:newses) {
                        if (existNews.get(String.format(NEWS_URL, news.getId())) != null) {
                            continue;
                        }
                        if(StringUtils.isBlank(news.getDesc())){
                            continue;
                        }
                        hotSize ++;
                        Element showHot = new Element("show_hot");

                        Element image = new Element("image");
                        image.addContent(new CDATA(news.getCoverImage().replace(".jpg","/169_85_56.jpg")));

                        showHot.addContent(image);

                        String titleStr = news.getName();
                        Element titleElement = new Element("title");
                        titleElement.addContent(new CDATA(titleStr));
                        showHot.addContent(titleElement);

                        String descStr = news.getDesc();
                        if(StringUtils.isBlank(descStr)){
                            descStr = titleStr;
                        }

                        int pos = descStr.indexOf("（编辑");
                        if(pos>0){
                            descStr = descStr.substring(0,pos);
                        }
                        else{
                            pos = descStr.indexOf("(编辑");
                            if(pos>0){
                                descStr = descStr.substring(0,pos);
                            }
                        }
                        Element descElement = new Element("desc");
                        descElement.addContent(new CDATA(descStr));
                        showHot.addContent(descElement);

                        Element urlElement = new Element("url");
                        urlElement.addContent(new CDATA(String.format(NEWS_URL,news.getId())+"?ch="+Constants.OLY_360_NEW_CH));
                        showHot.addContent(urlElement);

                        Element mUrlElement = new Element("m_url");
                        mUrlElement.addContent(new CDATA(String.format(M_NEWS_URL,news.getId())+"?ch="+Constants.OLY_360_NEW_CH));
                        showHot.addContent(mUrlElement);

                        Element pdate = new Element("pdate");
                        pdate.addContent(new CDATA(LeDateUtils.formatYMDHMS(LeDateUtils.parseYYYYMMDDHHMMSS(news.getPublishAt()))));
                        showHot.addContent(pdate);
                        existNews.put(String.format(NEWS_URL,news.getId()),String.format(NEWS_URL,news.getId()));

                        showNews.addContent(showHot);

                        if(hotSize==1)break;
                    }

                }
            }


            //最新新闻列表
            p = new GetRelatedNewsParam();
            p.setRelatedId(OlyUtil.OLY_OLY_TAG_ID);
            typeArray = LeStringUtils.commaString2LongList("0,1,2,3");
            typeList = Lists.newArrayList();
            for (Long typeValue : typeArray) {
                NewsType t = NewsType.findByValue(LeNumberUtils.toInt(typeValue));
                if (t != null) {
                    typeList.add(t);
                }
            }
            p.setTypes(typeList);
            page = PageUtils.createPageParam(0,20);
            ids = SopsApis.getNewsIdsByRelatedId(p, page, caller);

            size = 0;
            if(!CollectionUtils.isEmpty(ids)){
                List<TNews> newses = SopsApis.getTNewsByIds(ids, caller);
                if(CollectionUtils.isNotEmpty(newses)){

                    for(TNews news:newses){
                        if(existNews.get(String.format(NEWS_URL,news.getId()))!=null){
                            continue;
                        }
                        size ++ ;

                        Element showList = new Element("show_list");

                        String titleStr = news.getName();
//                        if(titleStr.length()>20){
//                            titleStr = titleStr.substring(0,20);
//                        }
                        Element titleElement = new Element("title");
                        titleElement.addContent(new CDATA(titleStr));
                        showList.addContent(titleElement);

                        Element urlElement = new Element("url");
                        urlElement.addContent(new CDATA(String.format(NEWS_URL,news.getId())+"?ch="+Constants.OLY_360_NEW_CH));
                        showList.addContent(urlElement);


                        Element pdate = new Element("pdate");
                        pdate.addContent(new CDATA(LeDateUtils.formatYMDHMS(LeDateUtils.parseYYYYMMDDHHMMSS(news.getPublishAt()))));
                        showList.addContent(pdate);
                        existNews.put(String.format(NEWS_URL,news.getId()),String.format(NEWS_URL,news.getId()));

                        showNews.addContent(showList);
                        if(size==4){
                            break;
                        }
                    }

                }
            }

            Element moretxt = new Element("moretxt");
            moretxt.addContent(new CDATA("查看更多>>"));
            showNews.addContent(moretxt);

            Element moreurl = new Element("moreurl");
            moreurl.addContent((new CDATA(OlyUtil.OLY_PC_NEWS_URL+"?ch="+Constants.OLY_360_NEW_CH)));
            showNews.addContent(moreurl);

            Element mMoreurl = new Element("m_moreurl");
            mMoreurl.addContent((new CDATA(OlyUtil.OLY_MOBILE_NEWS_URL+"?ch="+Constants.OLY_360_NEW_CH)));
            showNews.addContent(mMoreurl);

            display.addContent(showNews);

            Element uptime = new Element("uptime");
            uptime.addContent(new CDATA(LeDateUtils.formatYMDHMS(new Date())));
            display.addContent(uptime);

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
            LOG.error("sogouOlyNewsStats createXmlFile  error", e);
            return false;
        }
        return true;
    }

}
