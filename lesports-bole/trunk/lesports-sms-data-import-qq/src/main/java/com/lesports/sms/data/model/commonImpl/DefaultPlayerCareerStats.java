package com.lesports.sms.data.model.commonImpl;

import com.google.common.collect.Lists;
import com.lesports.sms.data.model.DefaultModel;

import java.util.List;
import java.util.Map;

/**
 * Created by qiaohongxin on 2016/11/3.
 */
public class DefaultPlayerCareerStats extends DefaultModel {

    private List<PlayerCareerStats> playerStatsList;

    public static class PlayerCareerStats {

        private String partnerId;
        private String tid;

        private List<CareerStatT> clubStats;

        private List<CareerStatT> NationalStats;

        public String getPartnerId() {
            return partnerId;
        }

        public void setPartnerId(String partnerId) {
            this.partnerId = partnerId;
        }

        public static class CareerStatT {

            private Map<String, Object> elementStats;

            public Map<String, Object> getElementStats() {
                return elementStats;
            }

            public void setElementStats(Map<String, Object> elementStats) {
                this.elementStats = elementStats;
            }
        }

    }


    @Override
    public List<Object> getData() {
        List<Object> res = Lists.newArrayList();
        for (PlayerCareerStats model : playerStatsList) {
            res.add(model);
        }
        return res;
    }
}
