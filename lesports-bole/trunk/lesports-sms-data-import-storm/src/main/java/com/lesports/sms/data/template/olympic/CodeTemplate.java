package com.lesports.sms.data.template.olympic;

import com.google.common.collect.Lists;
import com.lesports.sms.data.model.olympic.Code;
import com.lesports.sms.data.template.XmlTemplate;
import com.lesports.sms.data.utils.DateFormatter;
import com.lesports.utils.xml.annotation.Formatter;
import com.lesports.utils.xml.annotation.XPath;

import java.util.List;

/**
 * lesports-projects
 *
 * @author pangchuanxiao
 * @since 16-2-18
 */
public class CodeTemplate extends XmlTemplate {
    @XPath({"/OdfBody/Competition/CodeSsdqwwet","/OdfBody/Competition/CodeSet","/OdfBody/Competition/CodeSet"})
    private List<Code> codes;

    @Override
    public List<Object> getData() {
        List<Object> res= Lists.newArrayList();
        for (Code code : codes) {
            res.add(code);
        }
        return res;
    }
}
