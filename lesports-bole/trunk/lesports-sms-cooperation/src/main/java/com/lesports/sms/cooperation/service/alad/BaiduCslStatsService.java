package com.lesports.sms.cooperation.service.alad;

import com.google.common.collect.Lists;
import com.lesports.api.common.Direction;
import com.lesports.api.common.Order;
import com.lesports.query.InternalCriteria;
import com.lesports.query.InternalQuery;
import com.lesports.sms.api.common.VideoContentType;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.client.SopsInternalApis;
import com.lesports.sms.cooperation.util.Constants;
import com.lesports.sms.cooperation.util.FtpUtil;
import com.lesports.sms.cooperation.util.XmlUtil;
import com.lesports.sms.model.*;
import com.lesports.utils.LeProperties;
import com.lesports.utils.PageUtils;
import com.lesports.utils.PlayApis;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;

/**
 * Created by zhonglin on 2016/4/27.
 */
@Service("baiduCslStatsService")
public class BaiduCslStatsService {

    private static final Logger LOG = LoggerFactory.getLogger(BaiduCslStatsService.class);
    //中超赛事id
    public static final Long CSL_COMPETITION_ID = 47001L;


    private final String SODA_FIXTURES_FILE_PATH = LeProperties.getString("soda.csl.fixtures.file.path");
    private final String SODA_FIXTURES_FILE_NAMES = LeProperties.getString("soda.csl.fixtures.file.name");



    //生成给百度的中超相关视频，并且上传到ftp服务器
    public void baiduCslStats() {

        String fileName = "baidu2016csl.xml";
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

            long cid = CSL_COMPETITION_ID;
            CompetitionSeason competitionSeason = SbdsInternalApis.getLatestCompetitionSeasonByCid(cid);
            if(competitionSeason == null){
                LOG.error("competitionSeason is  null cid:{}", cid);
                return false;
            }

            //当前轮次
            int round = competitionSeason.getCurrentRound();
            long csid = competitionSeason.getId();

            Element root = new Element("DOCUMENT");
            root.setAttribute("content_method","full");
            Document Doc  = new Document(root);

            Element item = new Element("item");

            Element key = new Element("key");
            key.addContent("中超");
            item.addContent(key);

            Element display = new Element("display");
            Element url = new Element("url");
            url.addContent("http://sports.letv.com/");
            display.addContent(url);
            Element tab = new Element("tab");
            Element type = new Element("type");
            type.addContent("video");
            tab.addContent(type);
            Element tabName = new Element("tab_name");
            tabName.addContent("视频");
            tab.addContent(tabName);

            Element selected = new Element("selected");
            selected.addContent("true");
            tab.addContent(selected);

            Pageable pageable = PageUtils.createPageable(0,60);
            InternalQuery internalQuery = new InternalQuery();
            internalQuery.addCriteria(InternalCriteria.where("cid").is(Constants.CSL_COMPETITION_ID));
            internalQuery.setPageable(pageable);
            internalQuery.setSort(new Sort(Sort.Direction.DESC, "create_at"));
            List<com.lesports.sms.model.Video> videos = SopsInternalApis.getVideosByQuery(internalQuery);

            if (CollectionUtils.isEmpty(videos)) {
                LOG.info("work done. matches  is null");
            }

            for(com.lesports.sms.model.Video video:videos){
                Element videoslist = createVideoListElement(video);
                tab.addContent(videoslist);
            }
            display.addContent(tab);
            item.addContent(display);
            root.addContent(item);

            XMLOutputter XMLOut = new XMLOutputter(FormatXML());
            XMLOut.output(Doc, new FileOutputStream(file));
        }
        catch (Exception e){
            LOG.error("statsCal createXmlFile  error", e);
            return false;
        }
        return true;
    }

    //生成每场比赛的Element
    public Element createVideoListElement(com.lesports.sms.model.Video video) throws Exception{
        Element videoslist = new Element("videoslist");

        Element title = new Element("title");
        title.addContent(video.getName());
        videoslist.addContent(title);

        Element img = new Element("img");
        Map<String,String> images = video.getImages();
        String image = "";
        if(MapUtils.isNotEmpty(images)){
            for (String key : images.keySet()) {
                if(key.equals("120*90")){
                    image = images.get(key);
                    break;
                }
            }
        }
        img.addContent(image);
        videoslist.addContent(img);


        Element time = new Element("time");
        String timeStr = "";

        if(video.getDuration()!=null){
            timeStr = XmlUtil.formatMMSS(video.getDuration());
        }
        else timeStr = "00" ;
        time.addContent(timeStr);
        videoslist.addContent(time);

        Element provider = new Element("provider");
        provider.addContent("乐视体育");
        videoslist.addContent(provider);

        Element clicknum = new Element("clicknum");
        clicknum.addContent(PlayApis.getPlayNum(video.getId() + "") + "");
        videoslist.addContent(clicknum);

        String styleStr = "";
        String colorStr = "";
        String stylecolorStr = "";

        if(video.getType().equals(VideoContentType.RECORD)){
            styleStr = "回放";
            colorStr = "#f13f40";
            stylecolorStr = "#fbc5c5";
        }else if(video.getType().equals(VideoContentType.HIGHLIGHTS)){
            styleStr = "集锦";
            colorStr = "#3197ff";
            stylecolorStr = "#c1e0ff";
        }else if(video.getType().equals(VideoContentType.OTHER)){
            styleStr = "其他";
            colorStr = "#1fb281";
            stylecolorStr = "#bbe8d9\n";
        }


        Element style = new Element("style");
        style.addContent(styleStr);
        videoslist.addContent(style);

        Element color = new Element("color");
        color.addContent(colorStr);
        videoslist.addContent(color);

        Element stylecolor = new Element("stylecolor");
        stylecolor.addContent(stylecolorStr);
        videoslist.addContent(stylecolor);

        Element url = new Element("url");
        url.addContent("http://m.sports.le.com/video/"+video.getId()+".html?ch="+Constants.CSL_BAIDU_CH);
        videoslist.addContent(url);

        return videoslist;
    }

    public Format FormatXML(){
        Format format = Format.getCompactFormat();
        format.setEncoding("utf-8");
        format.setIndent(" ");
        return format;
    }





}
