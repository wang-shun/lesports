package com.lesports.sms.download.service.toutiao;

import client.SopsApis;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.lesports.api.common.*;
import com.lesports.query.InternalCriteria;
import com.lesports.query.InternalQuery;
import com.lesports.sms.api.common.LiveShowStatus;
import com.lesports.sms.api.common.TTag;
import com.lesports.sms.api.common.VideoContentType;
import com.lesports.sms.api.service.GetRelatedVideosParam;
import com.lesports.sms.api.vo.TVideo;
import com.lesports.sms.api.vo.TVideoInfo;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.client.SopsInternalApis;
import com.lesports.sms.download.util.Constants;
import com.lesports.sms.download.util.DownloadUtil;
import com.lesports.sms.download.util.HttpUtils;
import com.lesports.sms.download.util.XmlUtil;
import com.lesports.sms.model.CompetitionSeason;
import com.lesports.sms.model.Episode;
import com.lesports.sms.model.Tag;
import com.lesports.utils.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.io.SAXReader;
import org.jdom.CDATA;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.util.*;

/**
 * Created by zhonglin on 2016/8/25.
 */
@Service("toutiaoTop12Service")
public class ToutiaoTop12Service {

    private static final Logger LOG = LoggerFactory.getLogger(ToutiaoCslVideoService.class);
    private static RestTemplate template =new RestTemplate();


    //下载、合并、上传给头条的视频
    public void toutiaoTop12Videos() {
        LOG.info("download toutiao top12 videos begin");

        String localPath = Constants.filetoutiaovideorootpath + "top12" + File.separator;

        File file = new File(Constants.filetoutiaovideorootpath + "top12" + File.separator + "top12.xml");
        if (!file.exists()) {
            LOG.info("file is not exist: {} ", Constants.filetoutiaovideorootpath + Constants.filetoutiaovideodatafilename);
            return;
        }

        try {
            SAXBuilder builder = new SAXBuilder();
            Document document = builder.build(file);
            Element rootElement = document.getRootElement();

            List<Element> items = rootElement.getChildren("item");
            for(Element item:items){
                String vid = item.getChildText("video_id");
                String id = item.getChildText("id");
                if (StringUtils.isBlank(vid)) continue;
                String localName = vid + ".mp4";
                File existFile = new File(localPath + localName);
                if (existFile.exists()) {
                    continue;
                }
                boolean downloadFlag = DownloadUtil.downloadVideo(Long.parseLong(vid), localPath, localName);
                if (!downloadFlag) {
                    LOG.info("down load file error: {} ", localPath + localName);
                }
            }
        } catch (Exception e) {
            LOG.error("download toutiao top12 videos error", e);
        }
    }
}
