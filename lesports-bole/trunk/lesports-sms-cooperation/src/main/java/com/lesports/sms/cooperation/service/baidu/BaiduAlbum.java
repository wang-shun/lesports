package com.lesports.sms.cooperation.service.baidu;

import client.SopsApis;
import com.lesports.api.common.CountryCode;
import com.lesports.model.*;
import com.lesports.query.InternalCriteria;
import com.lesports.query.InternalQuery;
import com.lesports.sms.api.common.TTag;
import com.lesports.sms.api.common.VideoContentType;
import com.lesports.sms.api.vo.TAlbum;
import com.lesports.sms.client.SbdsApis;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.client.SopsInternalApis;
import com.lesports.sms.cooperation.util.BaiduXmlUtil;
import com.lesports.sms.cooperation.util.Constants;
import com.lesports.sms.cooperation.util.XmlUtil;
import com.lesports.sms.model.*;
import com.lesports.sms.model.Album;
import com.lesports.sms.model.Video;
import com.lesports.utils.CallerUtils;
import com.lesports.utils.LeDateUtils;
import com.lesports.utils.PageUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by zhonglin on 2016/9/2.
 */
@Service("baiduAlbum")
public class BaiduAlbum {
    private static final Logger LOG = LoggerFactory.getLogger(BaiduAlbum.class);
    private static final String TEAM_URL = "http://sports.le.com/team/%s.html";
    private static final String MATCH_URL = "http://sports.le.com/match/%s.html";
    private static final String VIDEO_URL = "http://sports.le.com/video/%s.html";

    //生成给百度视频的自制节目xml，并且上传到ftp服务器
    public void baiduAlbumStats() {

        String fileName = "baiduAlbum"+Constants.fileextraname+".xml";
        Boolean flag = createXmlFile(Constants.filelocalpath+fileName);

        //生成文件成功上传文件x
        if(flag){
            XmlUtil.uploadXmlFile(fileName, Constants.filelocalpath, Constants.fileuploadpath);
        }
        else{
            LOG.error("ftpXmlFile error file: {}" ,fileName);
        }

    }

    //生成xml文件
    public boolean createXmlFile(String file){
        try{
            Element root = new Element("urlset");
            Document Doc  = new Document(root);

            List<Map<String,String>> albums = BaiduXmlUtil.getAlbumMap();
            for(Map<String,String> album:albums){
                Element url = createOneContentElement(album);
                if(url != null){
                    root.addContent(url);
                }
            }

            XMLOutputter XMLOut = new XMLOutputter(XmlUtil.FormatXML());
            XMLOut.output(Doc, new FileOutputStream(file));
        }
        catch (Exception e){
            LOG.error("statsCal createXmlFile  error", e);
            return false;
        }
        return true;
    }


    //每个album的Element
    public static Element createOneContentElement(Map<String,String> albumMap) throws Exception{
        Element url = new Element("url");

        Element loc = new Element("loc");
        loc.addContent(albumMap.get("url"));
        url.addContent(loc);

        Album album = SopsInternalApis.getAlbumById(Long.parseLong(albumMap.get("aid")));

        Element lastmod = new Element("lastmod");
        lastmod.addContent(LeDateUtils.formatYMDTHMSZ(new Date()).replace("Z",""));
        url.addContent(lastmod);

        Element changefreq = new Element("changefreq");
        changefreq.addContent("daily");
        url.addContent(changefreq);

        Element priority = new Element("priority");
        priority.addContent("1.0");
        url.addContent(priority);

        Element data = new Element("data");

        Element tvShowSeries = new Element("TVShowSeries");

        Element domain = new Element("domain");
        domain.addContent("综艺");
        tvShowSeries.addContent(domain);

        Element type = new Element("type");
        type.addContent("TVShowSeries");
        tvShowSeries.addContent(type);

        Element name = new Element("name");
        name.addContent(album.getName());
        tvShowSeries.addContent(name);

        Element tvHost = new Element("tvHost");
        tvHost.addContent(albumMap.get("host"));
        tvShowSeries.addContent(tvHost);

        Element description = new Element("description");
        description.addContent(albumMap.get("desc"));
        tvShowSeries.addContent(description);


        Element detailUrl = new Element("detailUrl");
        detailUrl.addContent(albumMap.get("url"));
        tvShowSeries.addContent(detailUrl);

        Element verticalCoverImage = new Element("verticalCoverImage");
        verticalCoverImage.addContent(albumMap.get("logo"));
        tvShowSeries.addContent(verticalCoverImage);

        Element genre = new Element("genre");
        genre.addContent("体育");
        tvShowSeries.addContent(genre);


        if(CollectionUtils.isNotEmpty(album.getTagIds())){
            for(Long tagId:album.getTagIds()){
                TTag tag = SopsApis.getTTagById(tagId, CallerUtils.getDefaultCaller());
                if(tag == null) continue;
                Element tagElement = new Element("tag");
                tagElement.addContent(tag.getName());
                tvShowSeries.addContent(tagElement);
            }
        }

        Element inLanguage = new Element("inLanguage");
        inLanguage.addContent("汉语普通话");
        tvShowSeries.addContent(inLanguage);

        //专辑相关的节目
        Pageable pageable = PageUtils.createPageable(0,5);
        InternalQuery internalQuery = new InternalQuery();
        internalQuery.addCriteria(InternalCriteria.where("deleted").is(false));
        internalQuery.addCriteria(InternalCriteria.where("allow_country").is("CN"));
        internalQuery.addCriteria(InternalCriteria.where("aid").is(Long.parseLong(albumMap.get("aid"))));
        internalQuery.setPageable(pageable);
        internalQuery.setSort(new Sort(Sort.Direction.DESC, "create_at"));
        List<Episode> episodes = SopsInternalApis.getEpisodesByQuery(internalQuery);

        //过滤出最新的一期回放
        if(CollectionUtils.isNotEmpty(episodes)){
            Video record = null;
            for(Episode episode:episodes){
                Set<Episode.SimpleVideo> videoSet = episode.getVideos();
                for(Episode.SimpleVideo video:videoSet){
                    if(video.getType().equals(VideoContentType.RECORD)){
                        Element newestShowEpisode = new Element("newestShowEpisode");
                        record = SopsInternalApis.getVideoById(video.getVid());
                        newestShowEpisode.addContent(LeDateUtils.formatYYYY_MM_DD(LeDateUtils.parseYYYYMMDDHHMMSS(record.getCreateAt())));
                        tvShowSeries.addContent(newestShowEpisode);
                        break;
                    }
                }
                if(record != null){
                    break;
                }
            }
        }
        else{
            LOG.info("episodes is null , aid: {}",albumMap.get("aid"));
        }


        Element subDomain = new Element("subDomain");
        subDomain.addContent("体育赛事");
        tvShowSeries.addContent(subDomain);


        data.addContent(tvShowSeries);
        url.addContent(data);
        return  url;
    }
}
