package com.lesports.sms.data.model;


import java.util.HashMap;
import java.util.Map;

/**
 * Created by qiaohongxin on 2016/11/05.
 */
public class CommonConstants {

    public static final Map<String, Long> rankingTypeId = new HashMap<String, Long>(); //球员榜单映射关系
    public static final Map<String, String> basketBallPositionMap = new HashMap<String, String>();
    public static final Map<String, String> soccerPositionMap = new HashMap<String, String>();

    static {
        //stats//
        rankingTypeId.put("ReboundsperGame", 104662000L);//Top20篮板榜
        rankingTypeId.put("PointsperGame", 104661000L);//Top20得分榜
        rankingTypeId.put("AssistsperGame", 104666000L);//Top20助攻榜
        rankingTypeId.put("StealsperGame", 104663000L);//Top20抢断榜
        rankingTypeId.put("BlockedShotsperGame", 104664000L);//Top20盖帽榜
        rankingTypeId.put("ThreePointersMadeperGame", 116903000L);//Top20三分榜

        rankingTypeId.put("Reb/G", 116902000L);//篮板榜
        rankingTypeId.put("Pts/G", 116805000L);//得分榜
        rankingTypeId.put("Ast/G", 116703000L);//助攻榜
        rankingTypeId.put("Stl/G", 116806000L);//抢断榜
        rankingTypeId.put("Blk/G", 116807000L);//盖帽榜
        rankingTypeId.put("FG%", 116808000L);//投篮命中率
        rankingTypeId.put("3G%", 116704000L);//三分命中率

        rankingTypeId.put("goalscores", 100160000L);//射手榜
        rankingTypeId.put("assists", 100161000L);//助攻榜

        soccerPositionMap.put("G", "100125000");//守门员
        soccerPositionMap.put("D", "100126000");//后卫
        soccerPositionMap.put("M", "100127000");//中场
        soccerPositionMap.put("F", "100128000");//前锋
        //基础的配置字典项
        basketBallPositionMap.put("G", "后卫(G)");
        basketBallPositionMap.put("Guard", "后卫(G)");
        basketBallPositionMap.put("C", "中锋(C)");
        basketBallPositionMap.put("Center", "中锋(C)");
        basketBallPositionMap.put("F", "前锋(F)");
        basketBallPositionMap.put("Forward", "前锋(F)");
        basketBallPositionMap.put("SG", "后卫(G)");
        basketBallPositionMap.put("PF", "大前锋");
        basketBallPositionMap.put("Power Forward", "大前锋");
        basketBallPositionMap.put("SF", "小前锋");
        basketBallPositionMap.put("Small Forward", "小前锋");
        basketBallPositionMap.put("SG", "得分后卫");
        basketBallPositionMap.put("Shooting Guard", "得分后卫");
        basketBallPositionMap.put("PG", "控球后卫");
        basketBallPositionMap.put("Point Guard", "控球后卫");
        basketBallPositionMap.put("F/C", "前锋/中锋(F-C)");
        basketBallPositionMap.put("F-C", "前锋/中锋(F-C)");
        basketBallPositionMap.put("Forward-Center", "前锋/中锋(F-C)");
        basketBallPositionMap.put("F-G", "前锋/后卫(F-G)");
        basketBallPositionMap.put("G/F", "前锋/后卫(F-G)");
        basketBallPositionMap.put("Guard-Forward", "前锋/后卫(F-G");
        basketBallPositionMap.put("C-F", "中锋/前锋(C-F)");
        basketBallPositionMap.put("C/F", "中锋/前锋(C-F)");
        basketBallPositionMap.put("Center-Forward", "中锋/前锋(C-F)");


    }
}
