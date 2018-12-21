package com.lesports.crawler.utils;

/**
 * 工具类
 * 
 * @author denghui
 *
 */
public class CrawlerUtils {

    /**
     * 从URL中获取网站名称
     * 
     * @param url
     * @return
     */
    public static String getSite(String url) {
        if (url == null || !url.startsWith("http://")) {
            return null;
        }

        String part = url.substring(7, 7 + url.substring(7).indexOf('/'));
        String[] items = part.split("\\.");
        return items.length > 2 ? items[1] : items[0];
    }

    /**
     * 从http://sports.qq.com/a/20151130/035822.htm提取035822
     * 
     * @param url
     * @return
     */
    public static String getIdFromUrl(String url) {
        int idx1 = url.lastIndexOf('.');
        int idx2 = url.lastIndexOf('/');
        return url.substring(idx2 + 1, idx1);
    }

    /**
     * 从字符串中提取json串
     * 
     * @param text
     * @return
     */
    public static String getJsonFromText(String text) {
        int jsonStartIndex = text.indexOf('{');
        int jsonEndIndex = text.lastIndexOf('}');
        String json = text.substring(jsonStartIndex, jsonEndIndex + 1);
        return json;
    }
}
