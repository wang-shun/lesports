package com.lesports.sms.data.adapter;

import com.google.common.base.Function;
import com.lesports.sms.data.service.Parser;
import com.lesports.sms.model.MatchReview;
import com.lesports.sms.model.Match;
import com.lesports.utils.LeConcurrentUtils;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by ruiyuansheng on 2016/2/25.
 */
@Component
public class MatchResultAdapter {

    private static final Function<Match,MatchReview.HistoryMatch> matchFunction = new Function<Match, MatchReview.HistoryMatch>() {
        @Nullable
        @Override
        public MatchReview.HistoryMatch apply(@Nullable Match input) {
            return adapt(input);
        }
    };


    public <T> List<MatchReview.HistoryMatch> adapt(List<T> list,Class<T> clazz){

        if(clazz == Match.class){
            return LeConcurrentUtils.parallelApply((List<Match>) list,matchFunction);
        }
        return Collections.emptyList();
    }

    public static MatchReview.HistoryMatch adapt(Match match){

        MatchReview.HistoryMatch conMatch = new MatchReview.HistoryMatch();

        conMatch.setName(match.getName());
        conMatch.setMultiLangNames(match.getMultiLangNames());
        conMatch.setCid(match.getCid());
        conMatch.setCsid(match.getCsid());
        conMatch.setMid(match.getId());
        conMatch.setRound(match.getRound());
        conMatch.setStartDate(match.getStartDate());
        conMatch.setStartTime(match.getStartTime());
        conMatch.setCompetitors(match.getCompetitors());

        return conMatch;


    }






}
