package com.lesports.sms.data.util.formatter.QQ;

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import com.lesports.sms.data.model.QQConstants;
import com.lesports.sms.data.util.formatter.ElementFormatter;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * Created by qiaohongxin on 2016/11/30.
 */
public class QQPlayerStandingStatsFormatter extends ElementFormatter implements Function<Map<String, Object>, Map<String, Object>> {
    @Nullable
    @Override
    public Map<String, Object> apply(Map<String, Object> input) {
        Map<String, Object> stats = Maps.newHashMap();
        Object assistsPG = input.get("assistsPG");
        Object blocksPG = input.get("blocksPG");
        Object pointsPG = input.get("pointsPG");
        Object reboundsPG = input.get("reboundsPG");
        Object stealsPG = input.get("stealsPG");
        Object turnoversPG = input.get("turnoversPG");
        if (assistsPG != null) {
            stats.put("AssistsperGame", assistsPG);
            stats.put("Ast/G", assistsPG);
        } else if (blocksPG != null) {
            stats.put("BlockedShotsperGame", blocksPG);
            stats.put("Blk/G", blocksPG);
        } else if (pointsPG != null) {
            stats.put("PointsperGame", pointsPG);
            stats.put("Pts/G", pointsPG);
        } else if (reboundsPG != null) {
            stats.put("ReboundsperGame", reboundsPG);
            stats.put("Reb/G", reboundsPG);
        } else if (stealsPG != null) {
            stats.put("StealsperGame", stealsPG);
            stats.put("Stl/G", stealsPG);
        }
        return stats;

    }
}
//Top20rankingTypeId.put("ReboundsperGame", 104662000L);//篮板榜
//        Top20rankingTypeId.put("PointsperGame", 104661000L);//得分榜
//        Top20rankingTypeId.put("AssistsperGame", 104666000L);//助攻榜
//        Top20rankingTypeId.put("StealsperGame", 104663000L);//抢断榜
//        Top20rankingTypeId.put("BlockedShotsperGame", 104664000L);//盖帽榜
//        Top20rankingTypeId.put("ThreePointersMadeperGame", 116903000L);//三分榜
//
//        rankingTypeId.put("Reb/G", 116902000L);//篮板榜
//        rankingTypeId.put("Pts/G", 116805000L);//得分榜
//        rankingTypeId.put("Ast/G", 116703000L);//助攻榜
//        rankingTypeId.put("Stl/G", 116806000L);//抢断榜
//        rankingTypeId.put("Blk/G", 116807000L);//盖帽榜
//        rankingTypeId.put("FG%", 116808000L);//投篮命中率
//        rankingTypeId.put("3G%", 116704000L);//三分命中率

