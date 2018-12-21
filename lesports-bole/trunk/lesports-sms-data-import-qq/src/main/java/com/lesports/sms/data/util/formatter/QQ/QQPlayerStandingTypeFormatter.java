package com.lesports.sms.data.util.formatter.QQ;

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.data.model.QQConstants;
import com.lesports.sms.data.util.formatter.ElementFormatter;
import com.lesports.sms.model.Team;
import net.minidev.json.JSONArray;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * Created by qiaohongxin on 2016/11/30.
 */
public class QQPlayerStandingTypeFormatter implements Function<Object, Long> {
    @Nullable
    @Override
    public Long apply(Object input) {
        Long typeId = 0L;
        if (net.minidev.json.JSONArray.class.isAssignableFrom(input.getClass())) {
            JSONArray arrays = (JSONArray) input;
            if (arrays != null && arrays.size() > 0) {
                Map<String, Object> currentObject = (Map<String, Object>) arrays.get(0);
                Object assistsPG = currentObject.get("assistsPG");
                Object blocksPG = currentObject.get("blocksPG");
                Object pointsPG = currentObject.get("pointsPG");
                Object reboundsPG = currentObject.get("reboundsPG");
                Object stealsPG = currentObject.get("stealsPG");
                Object turnoversPG = currentObject.get("turnoversPG");
                if (assistsPG != null) {
                    if (arrays.size() == 20) {
                        typeId = 104666000L;
                    } else {
                        typeId = 116703000L;
                    }
                } else if (blocksPG != null) {
                    if (arrays.size() == 20) {
                        typeId = 104664000L;
                    } else {
                        typeId = 116807000L;
                    }
                } else if (pointsPG != null) {
                    if (arrays.size() == 20) {
                        typeId = 104661000L;
                    } else {
                        typeId = 116805000L;
                    }
                } else if (reboundsPG != null) {
                    if (arrays.size() == 20) {
                        typeId = 104662000L;
                    } else {
                        typeId = 116902000L;
                    }
                } else if (stealsPG != null) {
                    if (arrays.size() == 20) {
                        typeId = 104663000L;
                    } else {
                        typeId = 116806000L;
                    }
                }


            }
        }
        return typeId;
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

