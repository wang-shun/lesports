package com.lesports.crawler.model.source;

/**
 * aa.
 *
 * @author pangchuanxiao
 * @since 2015/12/1
 */
public enum Source {
    ZHIBO8(1), QQ(2), SINA(3);

    private int source;

    private Source(int status) {
        this.source = status;
    }

    public int getSource() {
        return source;
    }

    /**
     * 根据数值返回源
     * 
     * @param source
     * @return
     */
    public static Source fromInt(int source) {
        for (Source item : Source.values()) {
            if (item.getSource() == source) {
                return item;
            }
        }

        throw new IllegalArgumentException("unknown source " + source);
    }
}
