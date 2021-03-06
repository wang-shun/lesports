package com.lesports.sms.data.util.formatter.QQ;

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import com.lesports.sms.data.model.QQConstants;
import com.lesports.sms.data.util.formatter.ElementFormatter;
import net.minidev.json.JSONArray;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * Created by qiaohongxin on 2016/11/30.
 */
public class QQLivePlayerStatsFormatter implements Function<Object, Map<String, Object>> {
    @Nullable
    @Override
    public Map<String, Object> apply(Object input) {
        Map<String, Object> stats = Maps.newHashMap();
        if (net.minidev.json.JSONArray.class.isAssignableFrom(input.getClass())) {
            JSONArray arrays = (JSONArray) input;
            if (arrays != null && arrays.size() > 0) {
                stats.put("minutes", getArrayValue(arrays,2));
                stats.put("points", getArrayValue(arrays,3));
                stats.put("total_rebounds", getArrayValue(arrays,4));
                stats.put("assists", getArrayValue(arrays,5));
                String[] fieldGoas = arrays.get(6).toString().split("\\/");
                stats.put("fieldgoals_made", fieldGoas[0]);
                stats.put("fieldgoals_attempt", fieldGoas[1]);
                String[] threePointGoas = arrays.get(7).toString().split("\\/");
                stats.put("threepoint_made", threePointGoas[0]);
                stats.put("threepoint_attempt", threePointGoas[1]);
                String[] freeThrowPointGoas = arrays.get(8).toString().split("\\/");
                stats.put("freethrows_made", freeThrowPointGoas[0]);
                stats.put("freethrows_attempted", freeThrowPointGoas[1]);
                stats.put("offensive_rebounds", arrays.get(9));
                stats.put("defensive_rebounds", arrays.get(10));
                stats.put("steals", arrays.get(11));
                stats.put("blockedshots", arrays.get(12));
                stats.put("turnovers", getArrayValue(arrays,13));
                stats.put("personalfouls",getArrayValue(arrays,14));
            }
        }
        return stats;
    }

    private Object getArrayValue(JSONArray array, Integer index) {
        try {
            return array.get(index);

        } catch (Exception e) {
            return "";
        }
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