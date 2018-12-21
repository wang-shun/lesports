package com.lesports.sms.cooperation.service.baidu;

import client.SopsApis;
import com.lesports.query.InternalCriteria;
import com.lesports.query.InternalQuery;
import com.lesports.sms.api.common.TTag;
import com.lesports.sms.api.common.VideoContentType;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.client.SopsInternalApis;
import com.lesports.sms.cooperation.util.BaiduXmlUtil;
import com.lesports.sms.cooperation.util.Constants;
import com.lesports.sms.cooperation.util.XmlUtil;
import com.lesports.sms.model.Album;
import com.lesports.sms.model.Episode;
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
@Service("baiduAlbumItem")
public class BaiduAlbumItem {
    private static final Logger LOG = LoggerFactory.getLogger(BaiduAlbum.class);
    private static final String TEAM_URL = "http://sports.le.com/team/%s.html";
    private static final String MATCH_URL = "http://sports.le.com/match/%s.html";
    private static final String VIDEO_URL = "http://sports.le.com/video/%s.html";
    private static final String ALBUM_BAIDU_CH = "alad_diypro";

    //生成给百度视频的自制节目xml，并且上传到ftp服务器
    public void baiduAlbumItemStats() {

        String fileName = "baiduAlbumItem"+ Constants.fileextraname+".xml";
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
                int curPage = 0;
                boolean firstFlag = true;
                while (true) {
                    Pageable pageable = PageUtils.createPageable(curPage,100);
                    InternalQuery internalQuery = new InternalQuery();
                    internalQuery.addCriteria(InternalCriteria.where("deleted").is(false));
                    internalQuery.addCriteria(InternalCriteria.where("allow_country").is("CN"));
                    internalQuery.addCriteria(InternalCriteria.where("aid").is(Long.parseLong(album.get("aid"))));
                    internalQuery.setPageable(pageable);
                    internalQuery.setSort(new Sort(Sort.Direction.DESC, "create_at"));
                    List<Episode> episodes = SopsInternalApis.getEpisodesByQuery(internalQuery);
//
                    LOG.info("aid: {} , curPage:{} ,size: {} ",album.get("aid"),curPage ,episodes.size());
                    //翻到最后一页了
                    if (org.springframework.util.CollectionUtils.isEmpty(episodes)) {
                        break;
                    }

                    for (Episode episode : episodes) {
                        Element url = createOneContentElement(album,episode,firstFlag);
                        if(url != null){
                            root.addContent(url);
                            firstFlag = false;
                        }
                    }
                    //翻页
                    curPage++;
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
    public static Element createOneContentElement(Map<String,String> albumMap,Episode episode,boolean firstFlag) throws Exception{
        LOG.info("aid: {} , eid: {},firstFlag:{} ",albumMap.get("aid"),episode.getId(),firstFlag);

        Video record = null;

        Set<Episode.SimpleVideo> videoSet = episode.getVideos();
        for(Episode.SimpleVideo video:videoSet){
            if(video.getType().equals(VideoContentType.RECORD)){
                record = SopsInternalApis.getVideoById(video.getVid());
            }
        }

        //节目没有关联回放
        if(record == null){
            return null;
        }

        Element url = new Element("url");

        Element loc = new Element("loc");
        loc.addContent("http://sports.le.com/video/"+record.getId()+".html");
        url.addContent(loc);

        Album album = SopsInternalApis.getAlbumById(Long.parseLong(albumMap.get("aid")));

        Element lastmod = new Element("lastmod");
        if(firstFlag){
            lastmod.addContent(LeDateUtils.formatYMDTHMSZ(new Date()).replace("Z",""));
        }
        else{
            lastmod.addContent(LeDateUtils.formatYMDTHMSZ(LeDateUtils.parseYYYYMMDDHHMMSS(record.getUpdateAt())).replace("Z",""));
        }

        url.addContent(lastmod);

        Element changefreq = new Element("changefreq");
        changefreq.addContent("daily");
        url.addContent(changefreq);

        Element priority = new Element("priority");
        priority.addContent("1.0");
        url.addContent(priority);

        Element data = new Element("data");

        Element tvShowEpisode = new Element("TVShowEpisode");

        Element domain = new Element("domain");
        domain.addContent("综艺");
        tvShowEpisode.addContent(domain);

        Element type = new Element("type");
        type.addContent("TVShowEpisode");
        tvShowEpisode.addContent(type);

        Element name = new Element("name");
        name.addContent(episode.getName());
        tvShowEpisode.addContent(name);

        Element showEpisodeNumber = new Element("showEpisodeNumber");
        showEpisodeNumber.addContent(LeDateUtils.formatYYYY_MM_DD(LeDateUtils.parseYYYYMMDDHHMMSS(record.getCreateAt())));
        tvShowEpisode.addContent(showEpisodeNumber);


        Element tvHost = new Element("tvHost");
        tvHost.addContent(albumMap.get("host"));
        tvShowEpisode.addContent(tvHost);

        Element duration = new Element("duration");
        duration.addContent("P"+record.getDuration()+"S");
        tvShowEpisode.addContent(duration);

        Element thumbnailUrl = new Element("thumbnailUrl");
        if(record.getImages().get("120*90") != null){
            thumbnailUrl.addContent(record.getImages().get("120*90"));
        }
        else if(record.getImages().get("200*150") != null){
            thumbnailUrl.addContent(record.getImages().get("200*150"));
        }
        tvShowEpisode.addContent(thumbnailUrl);

        Element description = new Element("description");
        description.addContent(record.getDesc());
        tvShowEpisode.addContent(description);


        Element playUrl = new Element("playUrl");
        playUrl.addContent("http://sports.le.com/video/"+record.getId()+".html?ch="+ALBUM_BAIDU_CH);
        tvShowEpisode.addContent(playUrl);

        Element tvShowSeriesOf = new Element("tvShowSeriesOf");
        Element refLoc = new Element("ref_loc");
        refLoc.addContent(albumMap.get("url")+"?ch="+ALBUM_BAIDU_CH);
        tvShowSeriesOf.addContent(refLoc);
        tvShowEpisode.addContent(tvShowSeriesOf);

        Element subDomain = new Element("subDomain");
        subDomain.addContent("体育赛事");
        tvShowEpisode.addContent(subDomain);

        data.addContent(tvShowEpisode);
        url.addContent(data);
        return  url;
    }
}
