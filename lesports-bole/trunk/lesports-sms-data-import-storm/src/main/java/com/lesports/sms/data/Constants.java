package com.lesports.sms.data;

import java.util.HashMap;
import java.util.Map;

/**
 * lesports-projects
 *
 * @author pangchuanxiao
 * @since 16-2-18
 */
public abstract class Constants {

    public static final String TAG_GENERATOR_SCHEDULE = "TAG_GENERATOR_SCHEDULE";
    public static final String TAG_TEMPLATE_SCHEDULE = "TAG_TEMPLATE_SCHEDULE";
    public static final String TAG_PROCESSOR_SCHEDULE = "TAG_PROCESSOR_SCHEDULE";
    //filelist-job code
    public static final String CODE_SPORT = "CODE_SPORT";
    public static final String CODE_COUNTRY = "CODE_COUNTRY";
    public static final String CODE_PHASE = "CODE_PHASE";
    public static final String CODE_EVENT = "CODE_EVENT";
    public static final String CODE_DISCIPLINE = "CODE_DISCIPLINE";
    public static final String CODE_COMMON = "CODE_COMMON";
    public static final String CODE_SPORT_CODE = "CODE_SPORT_CODE";
    public static final String CODE_PLAY_BY_PLAY = "PLAY_BY_PLAY";
    public static final String CODE_MEDAL = "MEDAL";
    public static final String CODE_RECORD = "RECORD";
    public static final String CODE_LIVE_RESULT = "LIVE_RESULT";
    public static final String CODE_PLAYER = "PLAYER";
    public static final String CODE_TEAM = "TEAM";
    public static final String CODE_SCHEDULE = "SCHEDULE";
    public static final String CODE_EVENT_TYPE = "@Action";
    public static final String CODE_EVENT_RESULT = "@ResAction";
    public static final String CODE_PERIOD = "@Period";
    public static final String CODE_FILE_LIST = "FILE_LIST";
    public static final String CODE_OLD_LIST = "OLD_LIST";

    public static final String DICT_NAME_COUNTRY = "奥运国籍";
    public static final String DICT_NAME_SPORT = "奥运大项";
    public static final String DICT_NAME_DISCIPLINE = "奥运分项";
    public static final String DICT_NAME_EVENT = "奥运小项";
    public static final String DICT_NAME_PHASE = "奥运阶段";
    public static final String DICT_NAME_PERIOD = "比赛分段";
    public static final String DICT_NAME_EVENT_TYPE = "奥运事件类型";
    public static final String DICT_NAME_EVENT_RESULT = "奥运事件结果";

    public static final String BOLT_FIELD_FILE_URI = "file_uri";
    public static final String BOLT_FIELD_PARSER_RESULT = "parser_result";
    public static final String BOLT_FIELD_JOB_NAME = "jobName";
    public static final String BOLT_FIELD_JOB = "job";
    public static final String BOLT_FIELD_CODE = "CODE";
    // Match_live_property_path
    public static final String PATH_MATCH_CODE = "/OdfBody/@DocumentCode";//节点位置
    public static final String PATH_MATCH_STATUS = "/OdfBody/@ResultStatus";//节点位置
    public static final String PATH_COMPETITOR_CODE = "./Competitor/@Code";//节点位置
    public static final String PATH_COMPETITOR_COUNTRY = "./Competitor/@Organisation";//节点位置
    public static final String PATH_PLAYER_COUNTRY = "./Competitor/Composition/Athlete/Description/@Organisation";//节点位置
    public static final String PATH_COMPETITOR_Name = "./Competitor/Description/@TeamName";//节点位置
    public static final String PATH_COMPETITOR_Type = "./Competitor/@Type";//节点位置
    public static final String PATH_COMPETITOR_ORDER = "./@SortOrder";//节点位置
    public static final String PATH_ATHLETE = "./Competitor/Composition/Athlete";
    public static final String PATH_COACH = "./Competitor/Coaches/Coach";
    public static final String PATH_PERIODS_HOME_CODE = "/OdfBody/Competition/Periods/@Home";
    public static final String PATH_PERIODS_AWAY_CODE = "/OdfBody/Competition/Periods/@Away";
    public static final String PATH_PERIODS = "/OdfBody/Competition/Periods/Period";
    public static final String PATH_PERIOD_CODE = "./@Code";
    public static final String PATH_PERIOD_HOMESCORE = "./@HomePeriodScore";
    public static final String PATH_PERIOD_AWAYSCORE = "./@AwayPeriodScore";

    public static final String matchType = "MATCH";//节点位置
    public static final String competitorType = "COMPETITOR";//节点位置
    public static final String competitorStatsType = "COMPETITOR_STATS";//节点位置
    public static final String playerType = "PLAYER";//节点位置
    public static final String playerStatsType = "PLAYER_STATS";//节点位置

    public static final String stringType = "String";//字符串
    public static final String integerType = "Integer";//整形
    public static final String floatType = "Float";//单精度浮点型
    public static final String doubleType = "Double";//双精度浮点型
    public static final String FILE_TYPE_XML = "xml";
    public static final String FILE_TYPE_EXCEL = "xlsx";

    public static final Long OG_CID = 100507001L;
    public static final Integer partner_type = 498;
    public static final Integer partner_typeS = 498;
    public static final Integer MAX_TRY_COUNT = 3;

    private static final String Date_YYYYMMDDHHMMSS = "Date_YYYYMMDDHHMMSS";
    private static final String Date_YYYYMMDD = "Date_YYYYMMDD";
    public static final Map<String, Long> periodMap = new HashMap<>();

    static {
        periodMap.put("Q1", 0L);
    }


}
