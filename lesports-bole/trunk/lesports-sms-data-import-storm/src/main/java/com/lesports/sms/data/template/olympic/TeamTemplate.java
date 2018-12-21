package com.lesports.sms.data.template.olympic;

import com.google.common.collect.Lists;
import com.lesports.sms.data.model.olympic.ParticipantTeam;
import com.lesports.sms.data.template.XmlTemplate;
import com.lesports.utils.xml.annotation.XPath;

import java.util.List;

/**
 * lesports-projects
 *
 * @author pangchuanxiao
 * @since 16-2-18
 */
public class TeamTemplate extends XmlTemplate {
    @XPath("/OdfBody/Competition/Team")
   private List<ParticipantTeam> teams;

    @Override
    public List<Object> getData() {
        List<Object> res= Lists.newArrayList();
        for (ParticipantTeam team : teams) {
            res.add(team);
        }
        return res;
    }


}
