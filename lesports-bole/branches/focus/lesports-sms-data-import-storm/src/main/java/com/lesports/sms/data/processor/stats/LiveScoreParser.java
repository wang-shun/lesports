package com.lesports.sms.data.processor.stats;

import com.google.common.collect.ImmutableMap;
import com.lesports.sms.api.common.MatchStatus;
import com.lesports.sms.data.processor.BeanProcessor;
import com.lesports.sms.data.processor.CommonLiveScoreProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by qiaohongxin on 2015/9/18.
 */

//@Service("NBALiveParser")
//public class LiveScoreParser extends CommonLiveScoreProcessor implements BeanProcessor<MatchLiveScore> {
//    private static Logger logger = LoggerFactory.getLogger(LiveScoreParser.class);
//    private Map<String, MatchStatus> stateMap = ImmutableMap.of("1", MatchStatus.MATCH_NOT_START, "2", MatchStatus.MATCHING, "4", MatchStatus.MATCH_END);
//
//    public Boolean process(String fileType, MatchLiveScore matchLiveScore) {
//
//        return processLiveScore(matchLiveScore);
//    }
//
//    public Integer getPartnerType() {
//        return 499;
//    }
//
//    //TODO
//    public String getGameFTypeName(String sportId) {
//        return "篮球";
//    }
//
//    public MatchStatus getMatchStatus(String statusId) {
//        //TODO
//        return null;
//    }
//}
