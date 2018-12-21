package com.lesports.crawler.model.source;

import java.io.Serializable;

/**
 * 新闻正文段落
 * 
 * @author denghui
 *
 */
public class SourceNewsParagraph implements Serializable {

    private static final long serialVersionUID = 6924551878475526278L;
    // 序号
    private Short order;
    // 内容
    private String content;

    public SourceNewsParagraph() {
        super();
    }

    public SourceNewsParagraph(Short order, String content) {
        super();
        this.order = order;
        this.content = content;
    }

    public Short getOrder() {
        return order;
    }

    public void setOrder(Short order) {
        this.order = order;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
