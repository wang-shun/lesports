package com.lesports.sms.data.util.JsonPath;

import org.dom4j.Element;

import java.io.InputStream;

/**
 * lesports-projects
 *
 * @param <T> Class to be parsed from XML
 * @author qiaohongxin
 * @since 23-12-16
 */
public interface JsonParser<T> {
    /**
     * Parses a Json String
     *
     * @param json XML
     * @return
     * @throws com.lesports.sms.data.util.xml.exception.ParserException
     */
    public T parse(String json);

    /**
     * Parses a XML document
     *
     * @param jsonString
     * @return
     * @throws com.lesports.sms.data.util.xml.exception.ParserException
     */
    public T parse(String annotationClass, String jsonString);
}