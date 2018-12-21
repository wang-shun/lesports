package com.lesports.sms.data.template.olympic;

import com.google.common.collect.Lists;
import com.lesports.sms.data.model.olympic.PlayByPlay;
import com.lesports.sms.data.template.XmlTemplate;
import com.lesports.utils.xml.annotation.XPath;
import java.util.List;

/**
 * lesports-projects
 *
 * @author pangchuanxiao
 * @since 16-2-18
 */
public class PBPTemplate extends XmlTemplate {
    @XPath("/OdfBody/Competition/UnitActions/UnitAction")
    private List<PlayByPlay> pbpEvents;

    @Override
    public List<Object> getData() {
        List<Object> res= Lists.newArrayList();
        for (PlayByPlay pbpEvent : pbpEvents) {
            res.add(pbpEvent);
        }
        return res;
    }
}
