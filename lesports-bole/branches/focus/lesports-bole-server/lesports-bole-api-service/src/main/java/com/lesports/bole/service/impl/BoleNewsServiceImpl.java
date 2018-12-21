package com.lesports.bole.service.impl;

import com.lesports.bole.api.vo.TBImage;
import com.lesports.bole.api.vo.TBNews;
import com.lesports.bole.api.vo.TBParagraph;
import com.lesports.bole.service.BoleNewsService;
import com.lesports.crawler.model.source.SourceNews;
import com.lesports.crawler.model.source.SourceNewsImage;
import com.lesports.crawler.model.source.SourceNewsParagraph;
import com.lesports.crawler.repository.SourceNewsRepository;
import com.lesports.utils.math.LeNumberUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * trunk.
 *
 * @author pangchuanxiao
 * @since 2016/1/11
 */
@Component
public class BoleNewsServiceImpl implements BoleNewsService {
    @Resource
    private SourceNewsRepository sourceNewsRepository;

    @Override
    public TBNews getById(Long id) {
        SourceNews sourceNews = sourceNewsRepository.findOne(id);
        if (null == sourceNews) {
            return null;
        }
        TBNews tbNews = new TBNews();
        tbNews.setTitle(sourceNews.getTitle());
        tbNews.setId(sourceNews.getId());
        tbNews.setPublishAt(sourceNews.getPublishAt());
        tbNews.setCommentCount(LeNumberUtils.toInt(sourceNews.getComments()));
        if (CollectionUtils.isNotEmpty(sourceNews.getParagraphs())) {
            List<TBParagraph> paragraphList = new ArrayList<>(sourceNews.getParagraphs().size());
            for (SourceNewsParagraph sourceNewsParagraph : sourceNews.getParagraphs()) {
                TBParagraph tbParagraph = new TBParagraph();
                tbParagraph.setContent(sourceNewsParagraph.getContent());
                tbParagraph.setOrder(sourceNewsParagraph.getOrder());
                paragraphList.add(tbParagraph);
            }
            tbNews.setParagraphs(paragraphList);
        }
        if (CollectionUtils.isNotEmpty(sourceNews.getImages())) {
            List<TBImage> imageList = new ArrayList<>(sourceNews.getImages().size());
            for (SourceNewsImage sourceNewsImage : sourceNews.getImages()) {
                TBImage tbImage = new TBImage();
                tbImage.setName(sourceNewsImage.getDesc());
                tbImage.setUrl(sourceNewsImage.getImageUrl());
                tbImage.setOrder(sourceNewsImage.getOrder());
                imageList.add(tbImage);
            }
            tbNews.setImages(imageList);
        }
        tbNews.setTags(sourceNews.getTags());
        return tbNews;
    }
}
