package com.lesports.sms.cooperation.service.copaAmerican;

import com.google.common.collect.Lists;
import com.lesports.query.InternalCriteria;
import com.lesports.query.InternalQuery;
import com.lesports.sms.client.SopsInternalApis;
import com.lesports.sms.model.News;
import com.lesports.sms.model.Video;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by qiaohognxin on 2016/4/23.
 */
@Service("BaiduNewsService")
public class Baidu_newsGenerator extends AbstractService {
    private static final Logger LOG = LoggerFactory.getLogger(Baidu_newsGenerator.class);

    public String getFilePath() {
        String filePath = "newslist_mobile_copaAmerican2016.xml";
        return filePath;
    }

    public Document getDocument() {
        Element root = createRooElement("DOCUMENT");
        root.addContent(getItemElement());
        return new Document(root);
    }

    private Element getItemElement() {
        Element item = createRooElement("item");//一级目录
        item.addContent(createElement("key", "美洲杯"));
        item.addContent(getDisplayElement());
        return item;
    }

    private Element getDisplayElement() {
        Element displayElement = createRooElement("display");
        displayElement.addContent(createElement("url", newsUrl+baidu_logo));
        displayElement.addContent(getTabNewsElement());

        return displayElement;
    }


    //添加新闻tab
    private Element getTabNewsElement() {
        Element tabElement2 = createRooElement("tab");
        tabElement2.addContent(createElement("type", "news"));
        tabElement2.addContent(createElement("tab_name", "新闻"));
        updateElement(tabElement2, getNewsListElements());
        return tabElement2;
    }

    //新闻列表
    private List<Element> getNewsListElements() {
        List<Element> newsLit = Lists.newArrayList();
        InternalQuery q = new InternalQuery();
        q.addCriteria(new InternalCriteria("tag_ids", "eq", copaNewTagId));
        List<News> news = SopsInternalApis.getNewsByQuery(q);
        if (CollectionUtils.isEmpty(news)) return null;
        for (News currentnew : news) {
            Element newsListElement = createRooElement("newslist");
            newsListElement.addContent(createElement("title", currentnew.getName()));
            newsListElement.addContent(createElement("img", MapUtils.isEmpty(currentnew.getImageUrl()) ? "" : currentnew.getImageUrl().get("120*90")));
            Video currentVideo = SopsInternalApis.getVideoById(currentnew.getVid());
            newsListElement.addContent(createElement("time", currentVideo == null ? "0" : currentVideo.getDuration().toString()));
            newsListElement.addContent(createElement("provider", "乐视体育"));
            newsListElement.addContent(createElement("url", currentVideo == null ? "" : "http://sports.le.com/video/" + currentnew.getVid() + ".html"));

            newsLit.add(newsListElement);
        }
        return newsLit;
    }

}
