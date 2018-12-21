package com.lesports.sms.data.util.xml;

import org.dom4j.Element;

import java.io.InputStream;

/**
 * lesports-projects
 *
 * @param <T> Class to be parsed from XML
 * @author pangchuanxiao
 * @since 16-2-16
 */
public interface Parser<T> {
    /**
     * Parses a XML document
     *
     * @param xml XML document
     * @return
     * @throws com.lesports.sms.data.util.xml.exception.ParserException
     */
    public T parse(String xml);

    /**
     * Parses a XML document
     *
     * @param stream XML document as InputStream
     * @return
     * @throws  com.lesports.sms.data.util.xml.exception.ParserException
     */
    public T parse(String annotationClass, InputStream stream);

    /**
     * Parses a XML document
     *
     * @param stream XML document as InputStream
     * @return
     * @throws  com.lesports.sms.data.util.xml.exception.ParserException
     */
    public T parse(InputStream stream);

    /**
     * Parses a XML document
     *
     * @param element Element in a XML document
     * @return
     * @throws  com.lesports.sms.data.util.xml.exception.ParserException
     */
    public T parse(String annotationClass, Element element);
}