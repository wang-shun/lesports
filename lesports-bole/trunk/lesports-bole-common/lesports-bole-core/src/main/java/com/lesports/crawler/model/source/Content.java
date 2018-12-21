package com.lesports.crawler.model.source;

/**
 * 抓取内容
 * 
 * @author denghui
 *
 */
public enum Content {
    /**
     * 比赛
     */
    MATCH(1),

    /**
     * 新闻
     */
    NEWS(2);

    private int content;

    private Content(int content) {
        this.content = content;
    }

    public int getContent() {
        return content;
    }

    /**
     * 根据数值返回内容
     * 
     * @param content
     * @return
     */
    public static Content fromInt(int content) {
        for (Content item : Content.values()) {
            if (item.getContent() == content) {
                return item;
            }
        }

        throw new IllegalArgumentException("unknown content " + content);
    }
}
