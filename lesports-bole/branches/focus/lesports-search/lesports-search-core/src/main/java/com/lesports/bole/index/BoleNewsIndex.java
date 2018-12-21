package com.lesports.bole.index;

import com.lesports.bole.utils.ElasticSearchResult;
import com.lesports.utils.math.LeNumberUtils;
import org.apache.commons.beanutils.LeBeanUtils;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.List;

/**
 * trunk.
 *
 * @author pangchuanxiao
 * @since 2016/1/11
 */
@Document(indexName = "bole_news")
public class BoleNewsIndex extends SearchIndex<Long> {
    private String title;
    private String content;
    private Integer commentCount;
    private Long publishTime;
    private List<String> tags;

    public Integer getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(Integer commentCount) {
        this.commentCount = commentCount;
    }

    public Long getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(Long publishTime) {
        this.publishTime = publishTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    @Override
    public boolean newInstance(ElasticSearchResult.HitItem hitItem) {
        if (null == hitItem.getSource()) {
            setId(LeNumberUtils.toLong(hitItem.getId()));
        } else {
            LeBeanUtils.copyNotEmptyPropertiesQuietly(this, hitItem.getSource());
        }
        return true;
    }
}
