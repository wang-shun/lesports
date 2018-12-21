package com.lesports.crawler.model.config;

/**
 * 控制接口输出数据
 * 
 * @author denghui
 *
 */
public enum OutputOption {
    /**
     * 全部
     */
    ALL(1),
    /**
     * 使用黑名单
     */
    BLACKLIST(2),
    /**
     * 使用白名单
     */
    WHITELIST(3),
    /**
     * 关闭
     */
    NONE(4);

    private int output;

    private OutputOption(int output) {
        this.output = output;
    }

    public int getOutput() {
        return output;
    }

    /**
     * 根据数值返回对应枚举
     * 
     * @param output
     * @return
     */
    public static OutputOption fromInt(int output) {
        for (OutputOption item : OutputOption.values()) {
            if (item.getOutput() == output) {
                return item;
            }
        }

        throw new IllegalArgumentException("unknown output option " + output);
    }
}
