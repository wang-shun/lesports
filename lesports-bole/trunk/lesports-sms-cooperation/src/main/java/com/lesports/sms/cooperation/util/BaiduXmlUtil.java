package com.lesports.sms.cooperation.util;

import ch.qos.logback.core.joran.spi.XMLUtil;
import client.SopsApis;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lesports.sms.api.common.Platform;
import com.lesports.sms.api.common.TTag;
import com.lesports.sms.api.vo.TVideo;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.client.SopsInternalApis;
import com.lesports.sms.model.DictEntry;
import com.lesports.sms.model.Tag;
import com.lesports.sms.model.Video;
import com.lesports.utils.CallerUtils;
import com.lesports.utils.LeDateUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.DocumentHelper;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by zhonglin on 2016/6/29.
 */
public class BaiduXmlUtil {
    private static final Logger LOG = LoggerFactory.getLogger(BaiduXmlUtil.class);
    public  static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");

    //自制节目信息地址
    public static final String BAIDU_ALBUM_URL = "http://v.api.le.com/hd/album.xml";

//    //生成search每场比赛的Element
//    public static Element createSearchVideoElement(com.lesports.sms.model.Video video) throws Exception{
//        Element url = new Element("url");
//
//        Element loc = new Element("loc");
//        loc.addContent("http://sports.le.com/video/"+video.getId()+".html");
//        url.addContent(loc);
//
//        Element lastmod = new Element("lastmod");
//        lastmod.addContent(LeDateUtils.formatYYYY_MM_DD(LeDateUtils.parseYYYYMMDDHHMMSS(video.getUpdateAt())));
//        url.addContent(lastmod);
//
//        Element changefreq = new Element("changefreq");
//        changefreq.addContent("always");
//        url.addContent(changefreq);
//
//        Element priority = new Element("priority");
//        priority.addContent("1.0");
//        url.addContent(priority);
//
//        Element data = new Element("data");
//        Element display = new Element("display");
//
//        Element id = new Element("id");
//        id.addContent(video.getId()+"");
//        display.addContent(id);
//
//        Element sourceTime = new Element("sourceTime");
//        sourceTime.addContent(LeDateUtils.parseYYYYMMDDHHMMSS(video.getUpdateAt()).getTime()/1000+"");
//        display.addContent(sourceTime);
//
//        Element name = new Element("name");
//        name.addContent(decode(video.getName().trim()));
//        display.addContent(name);
//
//        Element originalName = new Element("originalName");
//        originalName.addContent(decode(video.getName().trim()));
//        display.addContent(originalName);
//
//        String tagStr = "";
//        for(Long tagId:video.getTagIds()){
//            Tag tag = SopsInternalApis.getTagById(tagId);
//            if(StringUtils.isBlank(tagStr)){
//                tagStr = tag.getName();
//            }
//            else{
//                tagStr = tagStr + "$$" + tag.getName();
//            }
//        }
//
//        Element keyword = new Element("keyword");
//        if(StringUtils.isNotBlank(tagStr)){
//            keyword.addContent(tagStr);
//        }
//        else{
//            keyword.addContent(decode(video.getName().trim()));
//        }
//        display.addContent(keyword);
//
//        Element tags = new Element("tags");
//        tags.addContent(tagStr);
//        display.addContent(tags);
//
//        Element category = new Element("category");
//        category.addContent("体育");
//        display.addContent(category);
//
//        Element image = new Element("image");
//        if(video.getImages().get("640*320") != null){
//            image.addContent(video.getImages().get("640*320"));
//        }
//        else if(video.getImages().get("400*300") != null){
//            image.addContent(video.getImages().get("400*300"));
//        }
//        display.addContent(image);
//
//        if(video.getPlatforms().contains(Platform.PC)) {
//            Element detailUrlForWeb = new Element("detailUrlForWeb");
//            detailUrlForWeb.addContent("http://sports.le.com/v ideo/" + video.getId() + ".html?ch=baidu_search");
//            display.addContent(detailUrlForWeb);
//        }
//
//        Element detailUrlForH5 = new Element("detailUrlForH5");
//        detailUrlForH5.addContent("http://m.sports.le.com/video/"+video.getId()+".html?ch=baidu_search");
//        display.addContent(detailUrlForH5);
//
//        Element detailUrlForApp = new Element("detailUrlForApp");
//        detailUrlForApp.addContent("http://m.sports.le.com/video/"+video.getId()+".html?ch=baidu_search");
//        display.addContent(detailUrlForApp);
//
//        if(video.getPlatforms().contains(Platform.PC)) {
//            Element playUrlForWeb = new Element("playUrlForWeb");
//            playUrlForWeb.addContent("http://sports.le.com/video/" + video.getId() + ".html?ch=baidu_search");
//            display.addContent(playUrlForWeb);
//        }
//
//        Element playUrlForH5 = new Element("playUrlForH5");
//        playUrlForH5.addContent("http://m.sports.le.com/video/"+video.getId()+".html?ch=baidu_search");
//        display.addContent(playUrlForH5);
//
//        Element playUrlForApp = new Element("playUrlForApp");
//        playUrlForApp.addContent("http://m.sports.le.com/video/"+video.getId()+".html?ch=baidu_search");
//        display.addContent(playUrlForApp);
//
//        Element support = new Element("support");
//        String platformStr = "h5$$app";
//        platformStr = "pc$$h5$$app";
//        support.addContent(platformStr);
//        display.addContent(support);
//
//        Element videoFormat = new Element("videoFormat");
//        videoFormat.addContent("mp4");
//        display.addContent(videoFormat);
//
//        Element videoSize = new Element("videoSize");
//        videoSize.addContent("0");
//        display.addContent(videoSize);
//
//        Element description = new Element("description");
//        description.addContent(decode(video.getDesc()));
//        display.addContent(description);
//
//        Element language = new Element("language");
//        language.addContent("国语");
//        display.addContent(language);
//
//        Element Number2 = new Element("Number2");
//        Number2.addContent(video.getCreateAt().substring(0,8));
//        display.addContent(Number2);
//
//        Element isPurchase = new Element("isPurchase");
//        isPurchase.addContent("0");
//        display.addContent(isPurchase);
//
//        Element isDelete = new Element("isDelete");
//        if(video.getDeleted()){
//            isDelete.addContent("1");
//        }
//        else {
//            isDelete.addContent("0");
//        }
//        display.addContent(isDelete);
//
//        Element isOriginal = new Element("isOriginal");
//        isOriginal.addContent("1");
//        display.addContent(isOriginal);
//
//        Element duration = new Element("duration");
//        if(video.getDuration()!=null){
//            duration.addContent(video.getDuration().toString());
//        }
//        else {
//            duration.addContent("0");
//        }
//        display.addContent(duration);
//
//        Element isPGC = new Element("isPGC");
//        isPGC.addContent("1");
//        display.addContent(isPGC);
//
//
//        data.addContent(display);
//        url.addContent(data);
//        return  url;
//    }


//    //生成search每场比赛的Element
////    public static Element createSearchOlyVideoElement(TVideo video) throws Exception{
//    public static Element createSearchOlyVideoElement(Video video) throws Exception{
//        Element url = new Element("url");
//
//        Element loc = new Element("loc");
//        loc.addContent("http://sports.le.com/video/"+video.getId()+".html");
//        url.addContent(loc);
//
//        Element lastmod = new Element("lastmod");
//        lastmod.addContent(LeDateUtils.formatYYYY_MM_DD(LeDateUtils.parseYYYYMMDDHHMMSS(video.getUpdateAt())));
//        url.addContent(lastmod);
//
//        Element changefreq = new Element("changefreq");
//        changefreq.addContent("always");
//        url.addContent(changefreq);
//
//        Element priority = new Element("priority");
//        priority.addContent("1.0");
//        url.addContent(priority);
//
//        Element data = new Element("data");
//        Element display = new Element("display");
//
//        Element id = new Element("id");
//        id.addContent(video.getId()+"");
//        display.addContent(id);
//
//        Element sourceTime = new Element("sourceTime");
//        sourceTime.addContent(LeDateUtils.parseYYYYMMDDHHMMSS(video.getUpdateAt()).getTime()/1000+"");
//        display.addContent(sourceTime);
//
//        Element name = new Element("name");
//        name.addContent(decode(video.getName().trim()));
//        display.addContent(name);
//
//        Element originalName = new Element("originalName");
//        originalName.addContent(decode(video.getName().trim()));
//        display.addContent(originalName);
//
//        String tagStr = "";
//        LOG.info("tags: ",video.getTagIds());
//        if(CollectionUtils.isNotEmpty(video.getTagIds())){
//            for(Long tagId:video.getTagIds()){
//                TTag tag = SopsApis.getTTagById(tagId,CallerUtils.getDefaultCaller());
//                if(tag == null) continue;
//                if(StringUtils.isBlank(tagStr)){
//                    tagStr = tag.getName();
//                }
//                else{
//                    tagStr += "$$" + tag.getName();
//                }
//            }
//        }
//
//        Element keyword = new Element("keyword");
//        if(StringUtils.isNotBlank(tagStr)){
//            keyword.addContent(tagStr);
//        }
//        else{
//            keyword.addContent(decode(video.getName().trim()));
//        }
//        display.addContent(keyword);
//
//        Element tags = new Element("tags");
//        tags.addContent(tagStr);
//        display.addContent(tags);
//
//        Element category = new Element("category");
//        category.addContent("体育");
//        display.addContent(category);
//
//        Element image = new Element("image");
//        if(video.getImages().get("640*320") != null){
//            image.addContent(video.getImages().get("640*320"));
//        }
//        else if(video.getImages().get("400*300") != null){
//            image.addContent(video.getImages().get("400*300"));
//        }
//        display.addContent(image);
//
//        Element detailUrlForWeb = new Element("detailUrlForWeb");
//        detailUrlForWeb.addContent("http://sports.le.com/video/"+video.getId()+".html?ch="+OlyUtil.OLY_BAIDU_VIDEO_CH);
//        display.addContent(detailUrlForWeb);
//
//
//        Element detailUrlForH5 = new Element("detailUrlForH5");
//        detailUrlForH5.addContent("http://m.sports.le.com/video/"+video.getId()+".html?ch="+OlyUtil.OLY_BAIDU_VIDEO_CH);
//        display.addContent(detailUrlForH5);
//
//        Element detailUrlForApp = new Element("detailUrlForApp");
//        detailUrlForApp.addContent("http://m.sports.le.com/video/"+video.getId()+".html?ch="+OlyUtil.OLY_BAIDU_VIDEO_CH);
//        display.addContent(detailUrlForApp);
//
//        Element playUrlForWeb = new Element("playUrlForWeb");
//        playUrlForWeb.addContent("http://sports.le.com/video/" + video.getId() + ".html?ch="+OlyUtil.OLY_BAIDU_VIDEO_CH);
//        display.addContent(playUrlForWeb);
//
//        Element playUrlForH5 = new Element("playUrlForH5");
//        playUrlForH5.addContent("http://m.sports.le.com/video/"+video.getId()+".html?ch="+OlyUtil.OLY_BAIDU_VIDEO_CH);
//        display.addContent(playUrlForH5);
//
//        Element playUrlForApp = new Element("playUrlForApp");
//        playUrlForApp.addContent("http://m.sports.le.com/video/"+video.getId()+".html?ch="+OlyUtil.OLY_BAIDU_VIDEO_CH);
//        display.addContent(playUrlForApp);
//
//        Element support = new Element("support");
//        String platformStr = "pc$$h5$$app";
//
//        support.addContent(platformStr);
//        display.addContent(support);
//
//        Element videoFormat = new Element("videoFormat");
//        videoFormat.addContent("mp4");
//        display.addContent(videoFormat);
//
//        Element videoSize = new Element("videoSize");
//        videoSize.addContent("0");
//        display.addContent(videoSize);
//
//        Element description = new Element("description");
//        description.addContent(decode(video.getDesc()));
//        display.addContent(description);
//
//        Element language = new Element("language");
//        language.addContent("国语");
//        display.addContent(language);
//
//        Element Number2 = new Element("Number2");
//        Number2.addContent(video.getCreateAt().substring(0,8));
//        display.addContent(Number2);
//
//        Element isPurchase = new Element("isPurchase");
//        isPurchase.addContent("0");
//        display.addContent(isPurchase);
//
//        Element isDelete = new Element("isDelete");
//        if(video.getDeleted()){
//            isDelete.addContent("1");
//        }
//        else {
//            isDelete.addContent("0");
//        }
//        display.addContent(isDelete);
//
//        Element isOriginal = new Element("isOriginal");
//        isOriginal.addContent("1");
//        display.addContent(isOriginal);
//
//        Element duration = new Element("duration");
//        duration.addContent(video.getDuration()+"");
//        display.addContent(duration);
//
//        Element isPGC = new Element("isPGC");
//        isPGC.addContent("1");
//        display.addContent(isPGC);
//
//
//        data.addContent(display);
//        url.addContent(data);
//        return  url;
//    }


    //每个video的Element
    public static Element createVideoElement(Video video,String ch) throws Exception{
        Element url = new Element("url");

        Element loc = new Element("loc");
        loc.addContent("http://sports.le.com/video/"+video.getId()+".html");
        url.addContent(loc);

        Element lastmod = new Element("lastmod");
        lastmod.addContent(LeDateUtils.formatYYYY_MM_DD(LeDateUtils.parseYYYYMMDDHHMMSS(video.getUpdateAt())));
        url.addContent(lastmod);

        Element changefreq = new Element("changefreq");
        changefreq.addContent("always");
        url.addContent(changefreq);

        Element priority = new Element("priority");
        priority.addContent("1.0");
        url.addContent(priority);

        Element data = new Element("data");
        Element display = new Element("display");

        Element id = new Element("id");
        id.addContent(video.getId()+"");
        display.addContent(id);

        Element sourceTime = new Element("sourceTime");
        sourceTime.addContent(LeDateUtils.parseYYYYMMDDHHMMSS(video.getUpdateAt()).getTime()/1000+"");
        display.addContent(sourceTime);

        Element name = new Element("name");
        name.addContent(decode(video.getName().trim()));
        display.addContent(name);

        Element originalName = new Element("originalName");
        originalName.addContent(decode(video.getName().trim()));
        display.addContent(originalName);

        String tagStr = "";
        LOG.info("tags: ",video.getTagIds());
        if(CollectionUtils.isNotEmpty(video.getTagIds())){
            for(Long tagId:video.getTagIds()){
                TTag tag = SopsApis.getTTagById(tagId,CallerUtils.getDefaultCaller());
                if(tag == null) continue;
                if(StringUtils.isBlank(tagStr)){
                    tagStr = tag.getName();
                }
                else{
                    tagStr += "$$" + tag.getName();
                }
            }
        }

        Element keyword = new Element("keyword");
        if(StringUtils.isNotBlank(tagStr)){
            keyword.addContent(tagStr);
        }
        else{
            keyword.addContent(decode(video.getName().trim()));
        }
        display.addContent(keyword);

        Element tags = new Element("tags");
        tags.addContent(tagStr);
        display.addContent(tags);

        Element category = new Element("category");
        category.addContent("体育");
        display.addContent(category);

        Element image = new Element("image");
        if(video.getImages().get("640*320") != null){
            image.addContent(video.getImages().get("640*320"));
        }
        else if(video.getImages().get("400*300") != null){
            image.addContent(video.getImages().get("400*300"));
        }
        display.addContent(image);

        Element detailUrlForWeb = new Element("detailUrlForWeb");
        detailUrlForWeb.addContent("http://sports.le.com/video/"+video.getId()+".html?ch="+ch);
        display.addContent(detailUrlForWeb);


        Element detailUrlForH5 = new Element("detailUrlForH5");
        detailUrlForH5.addContent("http://m.sports.le.com/video/"+video.getId()+".html?ch="+ch);
        display.addContent(detailUrlForH5);

        Element detailUrlForApp = new Element("detailUrlForApp");
        detailUrlForApp.addContent("http://m.sports.le.com/video/"+video.getId()+".html?ch="+ch);
        display.addContent(detailUrlForApp);

        Element playUrlForWeb = new Element("playUrlForWeb");
        playUrlForWeb.addContent("http://sports.le.com/video/" + video.getId() + ".html?ch="+ch);
        display.addContent(playUrlForWeb);

        Element playUrlForH5 = new Element("playUrlForH5");
        playUrlForH5.addContent("http://m.sports.le.com/video/"+video.getId()+".html?ch="+ch);
        display.addContent(playUrlForH5);

        Element playUrlForApp = new Element("playUrlForApp");
        playUrlForApp.addContent("http://m.sports.le.com/video/"+video.getId()+".html?ch="+ch);
        display.addContent(playUrlForApp);

        Element support = new Element("support");
        String platformStr = "pc$$h5$$app";

        support.addContent(platformStr);
        display.addContent(support);

        Element videoFormat = new Element("videoFormat");
        videoFormat.addContent("mp4");
        display.addContent(videoFormat);

        Element videoSize = new Element("videoSize");
        videoSize.addContent("0");
        display.addContent(videoSize);

        Element description = new Element("description");
        description.addContent(decode(video.getDesc()));
        display.addContent(description);

        Element language = new Element("language");
        language.addContent("国语");
        display.addContent(language);

        Element Number2 = new Element("Number2");
        Number2.addContent(video.getCreateAt().substring(0,8));
        display.addContent(Number2);

        Element isPurchase = new Element("isPurchase");
        isPurchase.addContent("0");
        display.addContent(isPurchase);

        Element isDelete = new Element("isDelete");
        if(video.getDeleted()){
            isDelete.addContent("1");
        }
        else {
            isDelete.addContent("0");
        }
        display.addContent(isDelete);

        Element isOriginal = new Element("isOriginal");
        isOriginal.addContent("1");
        display.addContent(isOriginal);

        Element duration = new Element("duration");
        duration.addContent(video.getDuration()+"");
        display.addContent(duration);

        Element isPGC = new Element("isPGC");
        isPGC.addContent("1");
        display.addContent(isPGC);

        data.addContent(display);
        url.addContent(data);
        return  url;
    }


    //每个video的Element
    public static Element createOlyVideoElement(Video video,String ch) throws Exception{
        Element url = new Element("url");

        Element loc = new Element("loc");
        loc.addContent("http://sports.le.com/video/"+video.getId()+".html");
        url.addContent(loc);

        Element lastmod = new Element("lastmod");
        lastmod.addContent(LeDateUtils.formatYMDTHMSZ(LeDateUtils.parseYYYYMMDDHHMMSS(video.getUpdateAt())).replace("Z",""));
        url.addContent(lastmod);

        Element changefreq = new Element("changefreq");
        changefreq.addContent("always");
        url.addContent(changefreq);

        Element priority = new Element("priority");
        priority.addContent("1.0");
        url.addContent(priority);

        Element data = new Element("data");
        Element display = new Element("display");

        Element id = new Element("id");
        id.addContent(video.getId()+"");
        display.addContent(id);

        Element sourceTime = new Element("sourceTime");
        sourceTime.addContent(LeDateUtils.parseYYYYMMDDHHMMSS(video.getUpdateAt()).getTime()/1000+"");
        display.addContent(sourceTime);

        Element name = new Element("name");
        name.addContent(decode(video.getName().trim()));
        display.addContent(name);

        Element originalName = new Element("originalName");
        originalName.addContent(decode(video.getName().trim()));
        display.addContent(originalName);

        String tagStr = "";
        LOG.info("tags: ",video.getTagIds());
        if(CollectionUtils.isNotEmpty(video.getTagIds())){
            for(Long tagId:video.getTagIds()){
                TTag tag = SopsApis.getTTagById(tagId,CallerUtils.getDefaultCaller());
                if(tag == null) continue;
                if(StringUtils.isBlank(tagStr)){
                    tagStr = tag.getName();
                }
                else{
                    tagStr += "$$" + tag.getName();
                }
            }
        }

        Element keyword = new Element("keyword");
        if(StringUtils.isNotBlank(tagStr)){
            keyword.addContent(tagStr);
        }
        else{
            keyword.addContent(decode(video.getName().trim()));
        }
        display.addContent(keyword);

        Element tags = new Element("tags");
        tags.addContent(tagStr);
        display.addContent(tags);

        Element category = new Element("category");
        category.addContent("体育");
        display.addContent(category);

        Element image = new Element("image");
        if(video.getImages().get("400*300") != null){
            image.addContent(video.getImages().get("400*300"));
        }
        else if(video.getImages().get("200*150") != null){
            image.addContent(video.getImages().get("200*150"));
        }
        else if(video.getImages().get("120*90") != null){
            image.addContent(video.getImages().get("120*90"));
        }
        display.addContent(image);

        Element detailUrlForWeb = new Element("detailUrlForWeb");
        detailUrlForWeb.addContent("http://sports.le.com/video/"+video.getId()+".html?ch="+ch);
        display.addContent(detailUrlForWeb);


        Element detailUrlForH5 = new Element("detailUrlForH5");
        detailUrlForH5.addContent("http://m.sports.le.com/video/"+video.getId()+".html?ch="+ch);
        display.addContent(detailUrlForH5);

        Element detailUrlForApp = new Element("detailUrlForApp");
        detailUrlForApp.addContent("http://m.sports.le.com/video/"+video.getId()+".html?ch="+ch);
        display.addContent(detailUrlForApp);

        Element playUrlForWeb = new Element("playUrlForWeb");
        playUrlForWeb.addContent("http://sports.le.com/video/" + video.getId() + ".html?ch="+ch);
        display.addContent(playUrlForWeb);

        Element playUrlForH5 = new Element("playUrlForH5");
        playUrlForH5.addContent("http://m.sports.le.com/video/"+video.getId()+".html?ch="+ch);
        display.addContent(playUrlForH5);

        Element playUrlForApp = new Element("playUrlForApp");
        playUrlForApp.addContent("http://m.sports.le.com/video/"+video.getId()+".html?ch="+ch);
        display.addContent(playUrlForApp);

        Element support = new Element("support");
        String platformStr = "pc$$h5$$app";

        support.addContent(platformStr);
        display.addContent(support);

        Element videoFormat = new Element("videoFormat");
        videoFormat.addContent("mp4");
        display.addContent(videoFormat);

        Element videoSize = new Element("videoSize");
        videoSize.addContent("0");
        display.addContent(videoSize);

        Element description = new Element("description");
        description.addContent(decode(video.getDesc()));
        display.addContent(description);

        Element language = new Element("language");
        language.addContent("国语");
        display.addContent(language);

        Element Number2 = new Element("Number2");
        Number2.addContent(video.getCreateAt().substring(0,8));
        display.addContent(Number2);

        Element isPurchase = new Element("isPurchase");
        isPurchase.addContent("0");
        display.addContent(isPurchase);

        Element isDelete = new Element("isDelete");
        if(video.getDeleted()){
            isDelete.addContent("1");
        }
        else {
            isDelete.addContent("0");
        }
        display.addContent(isDelete);

        Element isOriginal = new Element("isOriginal");
        isOriginal.addContent("1");
        display.addContent(isOriginal);

        Element duration = new Element("duration");
        duration.addContent(video.getDuration()+"");
        display.addContent(duration);

        Element isPGC = new Element("isPGC");
        isPGC.addContent("1");
        display.addContent(isPGC);

        data.addContent(display);
        url.addContent(data);
        return  url;
    }


    //生成和上传数据文件
    public  static boolean  createAndUploadXmlFile(Document document,String fileName,String timePath, int fileSize,List<String> fileList,boolean uploadFlag ){
        boolean result = false;
        try {

            String filePath  = Constants.filebaidurootpath + timePath.replace("\\","/") ;
            String file =  fileName + fileSize + ".xml";
            LOG.info("createAndUploadXmlFile :{} begin", filePath + file);
            File path = new File(filePath);
            if(!path.exists()){
                XmlUtil.createPath(filePath);
            }

            XMLOutputter XMLOut = new XMLOutputter(XmlUtil.FormatXML());
            XMLOut.output(document, new FileOutputStream(filePath + file));
            LOG.info(filePath + file + " create success");

            if(uploadFlag){
                XmlUtil.uploadXmlFile(file,filePath,Constants.filebaiduuploadpath+timePath.replace("\\","/"));
            }
            fileList.add(timePath+file);
            LOG.info("createAndUploadXmlFile :{} end", filePath + file);
            result = true;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }





    //生成和上传索引文件
    public static void createAndUploadIndexXmlFile(List<String> list,String domain,String fileHead,boolean uploadFlag){
        Element root = new Element("sitemapindex");
        Document Doc  = new Document(root);
        String fileName = fileHead + ".xml";
        //循环内容
        for(String file:list){
            Element sitMapElement = new Element("sitemap");
            Element locElement = new Element("loc");
            LOG.info("uploadPath: {}, file: {} " ,Constants.filebaiduuploadpath,file.replace("\\","/"));
            locElement.addContent("http://"+domain+Constants.filebaiduuploadpath + file.replace("\\","/"));
            sitMapElement.addContent(locElement);

            Element lastmodElement = new Element("lastmod");
            lastmodElement.addContent(LeDateUtils.formatYYYY_MM_DD(new Date()));
            sitMapElement.addContent(lastmodElement);
            root.addContent(sitMapElement);
        }

        try {

            XMLOutputter XMLOut = new XMLOutputter(XmlUtil.FormatXML());
            XMLOut.output(Doc, new FileOutputStream(Constants.filebaidurootpath  + fileName));
            if(uploadFlag){
                XmlUtil.uploadXmlFile(fileName,Constants.filebaidurootpath,Constants.filebaiduuploadpath);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    //生成和上传索引文件
    public static void createAndUploadOlyIndexXmlFile(List<String> list,String domain,String fileHead,boolean uploadFlag){
        Element root = new Element("sitemapindex");
        Document Doc  = new Document(root);
        String fileName = fileHead + ".xml";
        //循环内容
        for(String file:list){
            Element sitMapElement = new Element("sitemap");
            Element locElement = new Element("loc");
            LOG.info("uploadPath: {}, file: {} " ,Constants.filebaiduuploadpath,file.replace("\\","/"));
            locElement.addContent("http://"+domain+Constants.filebaiduuploadpath + file.replace("\\","/"));
            sitMapElement.addContent(locElement);

            Element lastmodElement = new Element("lastmod");
            lastmodElement.addContent(LeDateUtils.formatYMDHMS(new Date()));
            sitMapElement.addContent(lastmodElement);
            root.addContent(sitMapElement);
        }

        try {

            XMLOutputter XMLOut = new XMLOutputter(XmlUtil.FormatXML());
            XMLOut.output(Doc, new FileOutputStream(Constants.filebaidurootpath  + fileName));
            if(uploadFlag){
                XmlUtil.uploadXmlFile(fileName,Constants.filebaidurootpath,Constants.filebaiduuploadpath);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }


    //index文件 增加
    public static void addAndUploadIndexXmlFile(List<String> list,String domain,String indexFile,boolean uploadFlag){
        try {
            File existFile = new File(Constants.filebaidurootpath+indexFile);
            if(!existFile.exists()){
                LOG.info("create new File : {} ,path: {} ",indexFile,Constants.filebaiduuploadpath);
                Element root = new Element("sitemapindex");
                Document Doc  = new Document(root);
                XMLOutputter XMLOut = new XMLOutputter(XmlUtil.FormatXML());
                XMLOut.output(Doc, new FileOutputStream(Constants.filebaidurootpath+indexFile));
            }
            SAXBuilder builder = new SAXBuilder();
            InputStream file = new FileInputStream(Constants.filebaidurootpath+indexFile);
            Document document = builder.build(file);
            Element root = document.getRootElement();

            for(String loc:list){
                Element sitMapElement = new Element("sitemap");
                Element locElement = new Element("loc");
                locElement.addContent("http://"+domain+Constants.filebaiduuploadpath+loc.replace("\\","/"));
                sitMapElement.addContent(locElement);

                Element lastmodElement = new Element("lastmod");
                lastmodElement.addContent(LeDateUtils.formatYYYY_MM_DD(new Date()));
                sitMapElement.addContent(lastmodElement);
                root.addContent(sitMapElement);
            }

            XMLOutputter XMLOut = new XMLOutputter(XmlUtil.FormatXML());
            XMLOut.output(document, new FileOutputStream(Constants.filebaidurootpath+indexFile));
            if(uploadFlag){
                XmlUtil.uploadXmlFile(indexFile,Constants.filebaidurootpath,Constants.filebaiduuploadpath);
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }


    public static String  getTimePath(Date date){
        String timePath = "";
        String time = LeDateUtils.formatYYYY_MM_DD(date);
        String[] times = time.split("-");
        timePath = times[0] + File.separator + times[1] + File.separator;
        return  timePath;
    }

    public static String decode(String in) {
        StringBuffer out = new StringBuffer(); // Used to hold the output.
        char current; // Used to reference the current character.
        if (in == null || ("".equals(in)))
            return ""; // vacancy test.
        for (int i = 0; i < in.length(); i++) {
            current = in.charAt(i);
            if ((current == 0x9) || (current == 0xA) || (current == 0xD)
                    || ((current > 0x20) && (current <= 0xD7FF))
                    || ((current >= 0xE000) && (current <= 0xFFFD))
                    || ((current >= 0x10000) && (current <= 0x10FFFF))
                    || (current < 0x0))
                out.append(current);
        }
        return out.toString().trim();
    }

    public static List<Map<String,String>> getAlbumMap(){
        List<Map<String,String>> albums = Lists.newArrayList();

        try {
            URL url = new URL(BAIDU_ALBUM_URL);
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
            org.dom4j.Document doc = DocumentHelper.parseText(text);
            org.dom4j.Element root = doc.getRootElement();
            Iterator<org.dom4j.Element> items = root.elementIterator("item");
            while (items.hasNext()) {
                org.dom4j.Element item = items.next();
                Map<String,String> albumMap = Maps.newHashMap();
                albumMap.put("aid",item.element("aid").getTextTrim());
                albumMap.put("url",item.element("url").getTextTrim());
                albumMap.put("host",item.element("host").getTextTrim());
                albumMap.put("desc",item.element("desc").getTextTrim());
                albumMap.put("logo",item.element("logo").getTextTrim());
                albums.add(albumMap);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return albums;
    }


}
