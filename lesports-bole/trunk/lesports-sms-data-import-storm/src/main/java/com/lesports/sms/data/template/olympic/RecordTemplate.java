package com.lesports.sms.data.template.olympic;

import com.google.common.collect.Lists;
import com.lesports.sms.data.model.olympic.RecordDetail;
import com.lesports.sms.data.template.XmlTemplate;
import com.lesports.utils.xml.annotation.XPath;

import java.util.List;

/**
 * lesports-projects
 *
 * @author pangchuanxiao
 * @since 16-2-18
 */
public class RecordTemplate extends XmlTemplate {
    @XPath("/OdfBody/Competition/Record/RecordType")
    private List<RecordDetail> records;

    @Override
    public List<Object> getData() {
        List<Object> res= Lists.newArrayList();
        for (RecordDetail recode : records) {
            res.add(recode);
        }
        return res;
    }
}
