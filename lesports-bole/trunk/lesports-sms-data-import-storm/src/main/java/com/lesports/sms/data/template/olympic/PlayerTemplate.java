package com.lesports.sms.data.template.olympic;

import com.google.common.collect.Lists;
import com.lesports.sms.data.model.olympic.Participant;
import com.lesports.sms.data.template.XmlTemplate;
import com.lesports.utils.xml.annotation.XPath;

import java.util.List;

/**
 * lesports-projects
 *
 * @author pangchuanxiao
 * @since 16-2-18
 */
public class PlayerTemplate extends XmlTemplate {
    @XPath("/OdfBody/Competition/Participant")
    private List<Participant> players;

    @Override
    public List<Object> getData() {
        List<Object> res= Lists.newArrayList();
        for (Participant player : players) {
            res.add(player);
        }
        return res;
    }
}
