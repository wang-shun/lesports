//package com.lesports.sms.data.processor.sportrard.InnerProcessor;
//
//import com.alibaba.fastjson.JSON;
//import com.google.common.collect.Lists;
//import com.google.common.collect.Maps;
//import com.google.common.collect.Sets;
//import com.hankcs.hanlp.corpus.util.StringUtils;
//import com.lesports.sms.client.SbdsInternalApis;
//import com.lesports.sms.data.Constants;
//import com.lesports.sms.data.model.CommonLiveScore;
//import com.lesports.sms.data.model.sportrard.MatchLiveScore;
//import com.lesports.sms.data.model.sportrard.SportrardConstants;
//import com.lesports.sms.data.model.sportrard.SportrardPlayerStanding;
//import com.lesports.sms.data.model.sportrard.SportrardTeamStanding;
//import com.lesports.sms.data.processor.Processor;
//import com.lesports.sms.data.utils.CommonUtil;
//import com.lesports.sms.model.*;
//import com.lesports.utils.LeDateUtils;
//import org.apache.commons.collections.CollectionUtils;
//import org.dom4j.Element;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.util.*;
//
///**
// * Created by qiaohongxin on 2016/4/1.
// */
//public class F1Processor extends Processor {
//    private static Logger logger = LoggerFactory.getLogger(F1Processor.class);
//
//    public Map<String, Object> getCompetitorStatsMap(Element statistics, Map<String, String> paths) {
//        Map<String, Object> stats = Maps.newHashMap();
//        Iterator iter = paths.entrySet().iterator();
//        while (iter.hasNext()) {
//            Map.Entry entry = (Map.Entry) iter.next();
//            String key = String.valueOf(entry.getKey());
//            String path = String.valueOf(entry.getValue());
//            stats.put(key, CommonUtil.getStringValue(statistics.selectObject(path)));
//        }
//        return stats;
//    }
//
//
//}
//
//
//
//
//
//
//
//
//
