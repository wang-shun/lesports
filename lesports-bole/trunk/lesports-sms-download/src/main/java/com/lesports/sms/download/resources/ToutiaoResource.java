package com.lesports.sms.download.resources;

import client.SopsApis;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.lesports.api.common.CallerParam;
import com.lesports.api.common.CountryCode;
import com.lesports.api.common.LanguageCode;
import com.lesports.api.common.PageParam;
import com.lesports.jersey.AlternateMediaType;
import com.lesports.jersey.annotation.ENCRYPT;
import com.lesports.jersey.annotation.LJSONP;
import com.lesports.jersey.core.LeStatus;
import com.lesports.jersey.exception.LeWebApplicationException;
import com.lesports.jersey.support.annotation.NoEnvelopeResponse;
import com.lesports.sms.api.common.TTag;
import com.lesports.sms.api.vo.TComboEpisode;
import com.lesports.sms.api.vo.TVideo;
import com.lesports.sms.client.SopsInternalApis;
import com.lesports.sms.download.util.Constants;
import com.lesports.sms.model.Episode;
import com.lesports.sms.model.Tag;
import com.lesports.sms.model.Video;
import com.lesports.utils.CallerUtils;
import com.lesports.utils.LeDateUtils;
import com.lesports.utils.PlayApis;
import com.lesports.utils.security.LeSignature;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.jdom.CDATA;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by zhonglin on 2016/7/7.
 */
@Path("/")
public class ToutiaoResource {
    private static final Logger LOG = LoggerFactory.getLogger(ToutiaoResource.class);

    @NoEnvelopeResponse
    @LJSONP
    @GET
    @Path("toutiao/videos")
    @Produces({AlternateMediaType.UTF_8_APPLICATION_JSON})
    public Object getCompetitionVideos(){
        Map<String,Object> result = new  HashMap<String,Object>();
        result.put("message","没有数据");
        result.put("error",true);
        try {

            String cslLocalPath = Constants.filetoutiaovideorootpath+"csl"+File.separator;
            String top12LocalPath = Constants.filetoutiaovideorootpath+"top12"+File.separator;

//            String cslLocalPath = "E:\\toutiao\\csl\\";
//            String top12LocalPath =  "E:\\top12\\";

            List<Map<String,Object>> videos = Lists.newArrayList();
            result.put("app_url_iOS","https://itunes.apple.com/app/id983202217");
            result.put("app_url_and","http://h5api.mobile.lesports.com/download?from=1001");
            result.put("in_banner_download","http://i2.letvimg.com/lc04_iscms/201607/11/11/53/5f59b0288e6944df98798e159bb78e8f.png");
            result.put("in_banner_open","http://i0.letvimg.com/lc06_iscms/201607/11/11/53/bcca49dff3864267a4cde3ac95c43cf2.png");
            result.put("below_banner_download","http://i2.letvimg.com/lc03_iscms/201608/30/11/35/2b8a3761cb8f4d92bfe81e4accc343a9.png");
            result.put("below_banner_open","http://i3.letvimg.com/lc04_iscms/201608/30/11/35/43e24c5ab38643888d390cee3d33a8c5.png");
            result.put("schem_iOS","letvsportslaunch://com.lesports.glivesports/open?from=h5&to=race&id=%s");
            result.put("openURL_and","letvsportslaunch://com.lesports.glivesports/open?from=h5&to=race&id=%s");
            result.put("package_name","com.lesports.glivesports");

            //csl的数据
            File file = new File(cslLocalPath+"csl.xml");
            if(!file.exists()){
                LOG.info("file is not exist: {} " ,cslLocalPath+"csl.xml");
                return result;
            }
            SAXReader reader = new SAXReader();
            Document document = reader.read(file);
            Element rootElement = document.getRootElement();

            Iterator<Element> itemIterator = rootElement.elementIterator("item");

            while(itemIterator.hasNext()){
                Element item = itemIterator.next();
                String eid= item.element("id").getText();
                String vid = item.element("video_id").getText();

                File existFile = new File(cslLocalPath + eid + "_" + vid + ".mp4");
                if(!existFile.exists()){
                    continue;
                }

                Map<String,Object> video = createVideoElement(item);
                videos.add(video);
            }

            //top12的数据
            file = new File(top12LocalPath+"top12.xml");
            if(!file.exists()){
                LOG.info("file is not exist: {} " ,top12LocalPath+"top12.xml");
                return result;
            }
            reader = new SAXReader();
            document = reader.read(file);
            rootElement = document.getRootElement();

            itemIterator = rootElement.elementIterator("item");

            while(itemIterator.hasNext()){
                Element item = itemIterator.next();
                String vid = item.element("video_id").getText();

                File existFile = new File(top12LocalPath + vid + ".mp4");
                if(!existFile.exists()){
                    continue;
                }
                Map<String,Object> video = createVideoElement(item);
                videos.add(video);
            }

            result.put("videos",videos);
            result.put("message","");
            result.put("error",false);
            return  result;

        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new LeWebApplicationException(e.getMessage(), LeStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //生成每个视频的数据
    public Map<String,Object> createVideoElement(Element itemElement) throws Exception{
        Map<String,Object> video = new HashMap<String,Object>();
        video.put("type",0);
        video.put("title_1",itemElement.element("title_1").getText());
        video.put("pubDate_1",itemElement.element("pubDate_1").getText());
        video.put("video_cover_url_1",itemElement.element("video_cover_url_1").getText());
        String period1 = itemElement.element("period_1").getText();
        if(StringUtils.isNotBlank(period1)){
            video.put("period_1",Integer.parseInt(period1));
        }
        else{
            video.put("period_1",0);
        }
        String playCount11 = itemElement.element("video_id").getText();
        if(StringUtils.isNotBlank(playCount11)){
            video.put("play_count_1",Integer.parseInt(playCount11));
        }
        else{
            video.put("play_count_1",0);
        }
        video.put("tags_1",itemElement.element("tags_1").getText());
        video.put("link_1",itemElement.element("link_1").getText());
        video.put("description_1",itemElement.element("description_1").getText());
        String videoId = itemElement.element("video_id").getText();
        if(StringUtils.isNotBlank(videoId)){
            video.put("video_id",Long.parseLong(videoId));
        }
        else{
            video.put("video_id",0L);
        }
        return video;
    }
}
