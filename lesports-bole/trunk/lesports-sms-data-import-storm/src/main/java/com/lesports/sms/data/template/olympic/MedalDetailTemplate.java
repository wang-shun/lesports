package com.lesports.sms.data.template.olympic;

import com.google.common.collect.Lists;
import com.lesports.sms.data.model.olympic.MedalDetail;
import com.lesports.sms.data.template.XmlTemplate;
import com.lesports.utils.xml.annotation.XPath;

import java.util.List;

/**
 * lesports-projects
 *
 * @author pangchuanxiao
 * @since 16-2-18
 */
public class MedalDetailTemplate extends XmlTemplate {
    @XPath("/OdfBody/Competition/Discipline/Gender")
    private List<GenderMedalDetail> genderMedalDetails;

    @Override
    public List<Object> getData() {
        List<Object> res = Lists.newArrayList();
        for (GenderMedalDetail genderMedalDetail : genderMedalDetails) {
            for (MedalDetail medalDetail : genderMedalDetail.medalDetails) {
                res.add(medalDetail);
            }
        }

        return res;
    }

    public static class GenderMedalDetail {
        @XPath("./Event")
        private List<MedalDetail> medalDetails;

        public List<MedalDetail> getMedalDetails() {
            return medalDetails;
        }

        public void setMedalDetails(List<MedalDetail> medalDetails) {
            this.medalDetails = medalDetails;
        }
    }
}
