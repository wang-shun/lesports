package com.lesports.sms.download.service.toutiao;

import client.SopsApis;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.lesports.LeConstants;
import com.lesports.api.common.*;
import com.lesports.api.common.Sort;
import com.lesports.query.InternalCriteria;
import com.lesports.query.InternalQuery;
import com.lesports.sms.api.common.LiveShowStatus;
import com.lesports.sms.api.common.TTag;
import com.lesports.sms.api.common.VideoContentType;
import com.lesports.sms.api.service.GetEpisodesOfSeasonByMetaEntryIdParam;
import com.lesports.sms.api.service.GetRelatedVideosParam;
import com.lesports.sms.api.vo.TComboEpisode;
import com.lesports.sms.api.vo.TVideo;
import com.lesports.sms.api.vo.TVideoInfo;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.client.SopsInternalApis;
import com.lesports.sms.download.util.Constants;
import com.lesports.sms.download.util.DownloadUtil;
import com.lesports.sms.download.util.HttpUtils;
import com.lesports.sms.model.*;
import com.lesports.sms.download.util.XmlUtil;
import com.lesports.utils.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.io.SAXReader;
import org.jdom.CDATA;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.CriteriaDefinition;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.util.*;

import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * Created by zhonglin on 2016/7/5.
 */
@Service("toutiaoCslVideoService")
public class ToutiaoCslVideoService {

    private static final Logger LOG = LoggerFactory.getLogger(ToutiaoCslVideoService.class);

    //下载、合并、上传给头条的视频
    public void toutiaoCslVideos() {
        try{
            LOG.info("toutiaoVideos begin");
            CompetitionSeason competitionSeason = SbdsInternalApis.getLatestCompetitionSeasonByCid(Constants.CSL_COMPETITION_ID);
            if(competitionSeason == null){
                LOG.error("toutiaoVideos competitionSeason is  null cid:{}", Constants.CSL_COMPETITION_ID);
                return ;
            }

            long csid = competitionSeason.getId();
            String localPath = Constants.filetoutiaovideorootpath +"csl"+File.separator;

            File file = new File(localPath+"csl.xml");
            if(!file.exists()){
                LOG.info("file is not exist: {} " ,localPath+"csl.xml");
                return ;
            }

            //csl已有视频数据
            SAXBuilder builder = new SAXBuilder();
            Document document = builder.build(file);
            Element rootElement = document.getRootElement();

            List<Element> items = rootElement.getChildren("item");
            List<Element> newItemList = Lists.newArrayList();
            List<Element> oldItemList = Lists.newArrayList();
            Map<String,String> existMap = Maps.newHashMap();

            for(Element item:items){
                String vid = item.getChildText("video_id");
                String id = item.getChildText("id");
                if (StringUtils.isBlank(vid) || StringUtils.isBlank(id)) continue;
                String localName = id + "_" + vid + ".mp4";
                File existFile = new File(localPath + localName);

                //已有数据
                Element newItem = item;
                oldItemList.add(newItem);
                existMap.put(id,id);

                //如果存在无操作，否则下载
                if (existFile.exists()) {
                    continue;
                }
                boolean downloadFlag = DownloadUtil.downloadVideo(Long.parseLong(vid), localPath, localName);
                if (!downloadFlag) {
                    LOG.info("download file error: {} ", localPath + localName);
                }
            }

            //新的已结束的赛程
            Pageable pageable = PageUtils.createPageable(0, 100);
            InternalQuery internalQuery = new InternalQuery();
            internalQuery.addCriteria(InternalCriteria.where("cid").is(Constants.CSL_COMPETITION_ID));
            internalQuery.addCriteria(InternalCriteria.where("csid").is(csid));
            internalQuery.addCriteria(InternalCriteria.where("deleted").is(false));
            internalQuery.addCriteria(InternalCriteria.where("status").is(LiveShowStatus.LIVE_END));
            internalQuery.addCriteria(InternalCriteria.where("allow_country").is("CN"));
            internalQuery.setSort(new org.springframework.data.domain.Sort(org.springframework.data.domain.Sort.Direction.DESC, "start_date"));
            internalQuery.with(pageable);
            List<Episode> episodes = SopsInternalApis.getEpisodesByQuery(internalQuery);

            for(Episode episode:episodes){
                //如果节目已经生成过数据 continue
                if(existMap.get(episode.getId()+"")!=null){
                    continue;
                }
                else{
                    pageable = PageUtils.createPageable(0, 5);
                    internalQuery = new InternalQuery();
                    internalQuery.addCriteria(InternalCriteria.where("deleted").is(false));
                    internalQuery.addCriteria(InternalCriteria.where("mids").is(episode.getMid()));
                    internalQuery.addCriteria(InternalCriteria.where("type").is(VideoContentType.OTHER));

                    internalQuery.setPageable(pageable);
                    internalQuery.setSort(new org.springframework.data.domain.Sort(org.springframework.data.domain.Sort.Direction.DESC, "create_at"));
                    List<com.lesports.sms.model.Video> videos = SopsInternalApis.getVideosByQuery(internalQuery);
                    for(Video video:videos){
                        Element videoElement = createOneContentElement(episode,video);
                        if(videoElement==null)continue;
                        newItemList.add(videoElement);
                        File existFile = new File(localPath + episode.getId()+"_" + video.getId() + ".mp4");
                        //如果存在无操作，否则下载
                        if (existFile.exists()) {
                            continue;
                        }
                        boolean downloadFlag = DownloadUtil.downloadVideo(video.getId(),localPath,episode.getId()+"_" + video.getId() + ".mp4");
                        if(!downloadFlag){
                            LOG.info("download toutiao top12 video error name:{}",episode.getId()+"_" + video.getId() + ".mp4 error");
                        }
                    }
                }
            }

            //如果有新的视频，重新生成xml
            if(CollectionUtils.isNotEmpty(newItemList)){
                createXmlFile(newItemList,oldItemList);
            }

        } catch (Exception e) {
            LOG.error("download toutiao top12 videos error", e);
        }

        LOG.info("toutiaoVideos end");
    }



    //生成xml文件
    private boolean createXmlFile(List<Element> newList,List<Element> oldList ){
        try{
            Element root = new Element("DOCUMENT");
            Document Doc  = new Document(root);
            for (Element itemElement: newList) {
                root.addContent(itemElement);
            }

            for(Element itemElement: oldList){
                Element newItemElement = copyOneContentElement(itemElement);
                root.addContent(newItemElement);
            }

            XMLOutputter XMLOut = new XMLOutputter(XmlUtil.FormatXML());
            XMLOut.output(Doc, new FileOutputStream(Constants.filetoutiaovideorootpath +"csl"+File.separator+"csl.xml"));
        }
        catch (Exception e){
            LOG.error("statsCal createXmlFile  error", e);
            return false;
        }
        return true;
    }



    //生成每场比赛的Element
    public Element createOneContentElement(Episode episode,Video video) throws Exception{
        Element item = new Element("item");


        Element id = new Element("id");
        id.addContent(episode.getId()+"");
        item.addContent(id);

        Element type = new Element("type");
        type.addContent("0");
        item.addContent(type);

        Element title1 = new Element("title_1");
        title1.addContent(video.getName());
        item.addContent(title1);

        Element pubDate1 = new Element("pubDate_1");
        pubDate1.addContent(LeDateUtils.formatYMDHMS(LeDateUtils.parseYYYYMMDDHHMMSS(video.getCreateAt())));
        item.addContent(pubDate1);


        String image = "";
        if(video.getImages().get("1080*608") !=null ){
            image = video.getImages().get("1080*608") ;
        }
        else if(video.getImages().get("1280*640") != null){
            image = video.getImages().get("1280*640") ;
        }
        else if(video.getImages().get("960*540") != null){
            image = video.getImages().get("960*540") ;
        }
        else if(video.getImages().get("400*225") != null){
            image = video.getImages().get("400*225") ;
        }

        Element videoCoverUrl1 = new Element("video_cover_url_1");
        videoCoverUrl1.addContent(image);
        item.addContent(videoCoverUrl1);

        Element period1 = new Element("period_1");
        period1.addContent(video.getDuration()+"");
        item.addContent(period1);

        Element playCount1 = new Element("play_count_1");
        playCount1.addContent(PlayApis.getPlayNum(video.getId() + "")+"");
        item.addContent(playCount1);


        String tags = "";
        if(CollectionUtils.isNotEmpty(video.getTagIds())){
            for(Long tagId:video.getTagIds()){
                Tag tag = SopsInternalApis.getTagById(tagId);
                if(StringUtils.isBlank(tags))tags =tag.getName();
                else tags += ","+tag.getName();
            }
        }
        Element tags1 = new Element("tags_1");
        tags1.addContent(tags);
        item.addContent(tags1);

        Element link1 = new Element("link_1");
        link1.addContent(Constants.filetoutiaovideodomain+"toutiao"+"/csl/"+episode.getId()+"_"+video.getId()+".mp4");
        item.addContent(link1);

        Element description1 = new Element("description_1");
        description1.addContent(video.getDesc());
        item.addContent(description1);

        Element videoId = new Element("video_id");
        videoId.addContent(video.getId()+"");
        item.addContent(videoId);

        return item;
    }

    //复制每场的element
    public Element copyOneContentElement(Element oldElement) throws Exception{
        Element item = new Element("item");


        Element id = new Element("id");
        id.addContent(oldElement.getChild("id").getValue());
        item.addContent(id);

        Element type = new Element("type");
        type.addContent(oldElement.getChild("type").getValue());
        item.addContent(type);


        Element title1 = new Element("title_1");
        title1.addContent(oldElement.getChild("title_1").getValue());
        item.addContent(title1);

        Element pubDate1 = new Element("pubDate_1");
        pubDate1.addContent(oldElement.getChild("pubDate_1").getValue());
        item.addContent(pubDate1);

        Element videoCoverUrl1 = new Element("video_cover_url_1");
        videoCoverUrl1.addContent(oldElement.getChild("video_cover_url_1").getValue());
        item.addContent(videoCoverUrl1);

        Element period1 = new Element("period_1");
        period1.addContent(oldElement.getChild("period_1").getValue());
        item.addContent(period1);

        Element playCount1 = new Element("play_count_1");
        playCount1.addContent(oldElement.getChild("play_count_1").getValue());
        item.addContent(playCount1);

        Element tags1 = new Element("tags_1");
        tags1.addContent(oldElement.getChild("tags_1").getValue());
        item.addContent(tags1);

        Element link1 = new Element("link_1");
        link1.addContent(oldElement.getChild("link_1").getValue());
        item.addContent(link1);

        Element description1 = new Element("description_1");
        description1.addContent(oldElement.getChild("description_1").getValue());
        item.addContent(description1);


        Element videoId = new Element("video_id");
        videoId.addContent(oldElement.getChild("video_id").getValue());
        item.addContent(videoId);

        return item;
    }

}
