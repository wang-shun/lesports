package com.lesports.sms.data.util.formatter.QQ;

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import net.minidev.json.JSONArray;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * Created by qiaohongxin on 2016/11/30.
 */
public class QQLiveAwayTeamStatsFormatter implements Function<Object, Map<String, Object>> {
    @Nullable
    @Override
    public Map<String, Object> apply(Object input) {
        Map<String, Object> stats = Maps.newHashMap();
        if (JSONArray.class.isAssignableFrom(input.getClass())) {
            JSONArray arrays = (JSONArray) input;
            if (arrays != null && arrays.size() > 0) {
                Map<String, Object> reboundMap =  (Map<String, Object>)arrays.get(0);
                Map<String, Object> assistMap =  (Map<String, Object>)arrays.get(1);
                Map<String, Object> stealsMap =  (Map<String, Object>)arrays.get(2);
                Map<String, Object> blocksMap =  (Map<String, Object>)arrays.get(3);
                Map<String, Object> turnOverMap = (Map<String, Object>) arrays.get(4);
                Map<String, Object> ftMap =  (Map<String, Object>)arrays.get(5);
                Map<String, Object> threeMap =  (Map<String, Object>)arrays.get(6);
                Map<String, Object> foulMap =  (Map<String, Object>)arrays.get(7);
                stats.put("total_rebounds", reboundMap.get("rightVal"));
                stats.put("assists",assistMap.get("rightVal") );
                stats.put("steals",stealsMap.get("rightVal") );
                stats.put("blockedshots",blocksMap.get("rightVal") );
                stats.put("turnovers", turnOverMap.get("rightVal"));
                stats.put("freethrows_made",ftMap.get("rightVal") );
                stats.put("threepoint_made",threeMap.get("rightVal") );
                stats.put("personalfouls",foulMap.get("rightVal") );
            }
        }
        return stats;
    }
}
//NBALiveStatPath.put("minutes", "./minutes/@minutes");
//        NBALiveStatPath.put("points", "./points/@points");  //总得分
//        NBALiveStatPath.put("fieldgoals_made", "./field-goals/@made");//投篮命中次数
//        NBALiveStatPath.put("fieldgoals_attempted", "./field-goals/@attempted"); //投篮尝试次数
//        NBALiveStatPath.put("fieldgoal_percentage", "./field-goals/@percentage");//命中率
//        NBALiveStatPath.put("threepoint_made", "./three-point-field-goals/@made"); //三分球命中次数
//        NBALiveStatPath.put("threepoint_attempted", "./three-point-field-goals/@attempted");//三分球投篮次数
//        NBALiveStatPath.put("threepoint_percentage", "./three-point-field-goals/@percentage");//三分球命中率
//        NBALiveStatPath.put("freethrows_made", "./free-throws/@made");  //罚球命中次数
//        NBALiveStatPath.put("freethrows_attempted", "./free-throws/@attempted"); //罚球次数
//        NBALiveStatPath.put("freethrow_percentage", "./free-throws/@percentage");  //罚球命中率
//        NBALiveStatPath.put("offensive_rebounds", "./rebounds/@offensive"); //前场篮板
//        NBALiveStatPath.put("defensive_rebounds", "./rebounds/@defensive");//后场篮板
//        NBALiveStatPath.put("total_rebounds", "./rebounds/@total");//总篮板球次数
//        NBALiveStatPath.put("assists", "./assists/@assists");//助攻次数
//        NBALiveStatPath.put("personalfouls", "./personal-fouls/@fouls");// 犯规次数
//        NBALiveStatPath.put("disqualification", "./personal-fouls/@disqualifications");//被罚下场次数
//        NBALiveStatPath.put("steals", "./steals/@steals");//抢断次数
//        NBALiveStatPath.put("turnovers", "./turnovers/@turnovers");//失误次数
//        NBALiveStatPath.put("blockedshots", "./blocked-shots/@blocked-shots ");//盖帽次数
//        NBALiveStatPath.put("jumpshots", "./jumpshots/@jumpshots");//跳投次数
//        NBALiveStatPath.put("dunks", "./dunks/@dunks");//扣篮次数
//        NBALiveStatPath.put("layups", "./layups/@layups");//上篮次数