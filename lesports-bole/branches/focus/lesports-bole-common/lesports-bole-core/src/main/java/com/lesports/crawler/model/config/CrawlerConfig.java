package com.lesports.crawler.model.config;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import com.lesports.crawler.model.source.Content;
import com.lesports.crawler.model.source.Source;

/**
 * 爬虫配置
 * 
 * @author denghui
 *
 */
@Document(collection = "crawler_config")
@CompoundIndexes({
        @CompoundIndex(name = "source_1_content_1", def = "{'source': 1, 'content':1}", unique = true)
})
public class CrawlerConfig implements Serializable {
    private static final long serialVersionUID = -52070793191549641L;
    @Id
    private String id;
    // 源
    private Source source;
    // 内容
    private Content content;
    // 是否开启
    private boolean enabled = true;
    // TODO:线程数
    private int thread = 5;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    public Content getContent() {
        return content;
    }

    public void setContent(Content content) {
        this.content = content;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getThread() {
        return thread;
    }

    public void setThread(int thread) {
        this.thread = thread;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((content == null) ? 0 : content.hashCode());
        result = prime * result + (enabled ? 1231 : 1237);
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((source == null) ? 0 : source.hashCode());
        result = prime * result + thread;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        CrawlerConfig other = (CrawlerConfig) obj;
        if (content != other.content)
            return false;
        if (enabled != other.enabled)
            return false;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (source != other.source)
            return false;
        if (thread != other.thread)
            return false;
        return true;
    }

}
