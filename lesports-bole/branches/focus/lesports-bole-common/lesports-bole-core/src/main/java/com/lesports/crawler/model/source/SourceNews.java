package com.lesports.crawler.model.source;

import com.google.common.base.Strings;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.*;

/**
 * 源新闻
 * 
 * @author denghui
 *
 */
@Document(collection = "source_news")
@CompoundIndexes({ @CompoundIndex(name = "source_1_source_id_1", def = "{'source': 1, 'source_id': 1}", unique = true) })
public class SourceNews implements Serializable {

    private static final long serialVersionUID = 604811578976776037L;
    @Id
    private Long id;
    // 来源
    private Source source;
    // 来源Id
    @Field("source_id")
    private String sourceId;
    // 标题
    private String title;
    // 发布时间
    @Field("publish_at")
    private String publishAt;
    // 引用来源
    @Field("reference_name")
    private String referenceName;
    // 引用地址
    @Field("reference_url")
    private String referenceUrl;
    // 评论数
    private Integer comments;
    // 参与数
    private Integer participants;
    // 正文
    private List<SourceNewsParagraph> paragraphs;
    // 配图
    private List<SourceNewsImage> images;
    // 新闻所属赛事
    private String competitioin;
    // 标签
    private Set<String> tags;
    // 来源地址
    @Field("source_url")
    private String sourceUrl;
    // 创建时间
    @Field("create_at")
    private String createAt;
    // 更新时间
    @Field("update_at")
    private String updateAt;
    // 附加信息
    private Map<String, Object> extras;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPublishAt() {
        return publishAt;
    }

    public void setPublishAt(String publishAt) {
        this.publishAt = publishAt;
    }

    public String getReferenceName() {
        return referenceName;
    }

    public void setReferenceName(String referenceName) {
        this.referenceName = referenceName;
    }

    public String getReferenceUrl() {
        return referenceUrl;
    }

    public void setReferenceUrl(String referenceUrl) {
        this.referenceUrl = referenceUrl;
    }

    public Integer getComments() {
        return comments;
    }

    public void setComments(Integer comments) {
        this.comments = comments;
    }

    public List<SourceNewsParagraph> getParagraphs() {
        return paragraphs;
    }

    public void setParagraphs(List<SourceNewsParagraph> paragraphs) {
        this.paragraphs = paragraphs;
    }

    public List<SourceNewsImage> getImages() {
        return images;
    }

    public void setImages(List<SourceNewsImage> images) {
        this.images = images;
    }

    public String getCompetitioin() {
        return competitioin;
    }

    public void setCompetitioin(String competitioin) {
        this.competitioin = competitioin;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }

    public String getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(String updateAt) {
        this.updateAt = updateAt;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public void addParagraph(SourceNewsParagraph paragraph) {
        if (paragraph != null) {
            if (this.paragraphs == null) {
                this.paragraphs = new ArrayList<>();
            }
            this.paragraphs.add(paragraph);
        }
    }
    
    public void addImage(SourceNewsImage image) {
        if (image != null) {
            if (this.images == null) {
                this.images = new ArrayList<>();
            }
            this.images.add(image);
        }
    }

    public void addTag(String tag) {
        if (!Strings.isNullOrEmpty(tag)) {
            if (this.tags == null) {
                this.tags = new HashSet<>();
            }
            this.tags.add(tag);
        }
    }

    public void addTags(String... tags) {
        for (String tag : tags) {
            addTag(tag);
        }
    }

    public Integer getParticipants() {
        return participants;
    }

    public void setParticipants(Integer participants) {
        this.participants = participants;
    }

    public Map<String, Object> getExtras() {
        return extras;
    }

    public void setExtras(Map<String, Object> extras) {
        this.extras = extras;
    }

    public void putExtra(String key, Object value) {
        if (this.extras == null) {
            this.extras = new HashMap<>();
        }
        this.extras.put(key, value);
    }
    
    public Object getExtra(String key) {
        if (this.extras == null) {
            return null;
        }
        return this.extras.get(key);
    }
}
