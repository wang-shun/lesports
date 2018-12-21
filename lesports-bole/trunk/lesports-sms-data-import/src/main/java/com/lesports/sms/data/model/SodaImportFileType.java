package com.lesports.sms.data.model;

/**
 * Created by ruiyuansheng on 2016/3/17.
 */
public enum SodaImportFileType {

    SODAMATCH(0),//赛程
    SODARANKING(1),//积分榜
    SODAGOAL(2),//射手榜
    SODAASSIST(3),//助攻榜
    SODAMATCHSTAT(4),//历史数据
    SODAMATCHRESULT(5),//实时统计
    SODATIMELINE(6);//关键事件

    private final int value;

    private SodaImportFileType(int value) {
        this.value = value;
    }

    /**
     * Get the integer value of this enum value, as defined in the Thrift IDL.
     */
    public int getValue() {
        return value;
    }

    /**
     * Find a the enum type by its integer value, as defined in the Thrift IDL.
     * @return null if the value is not found.
     */
    public static SodaImportFileType findByValue(int value) {
        switch (value) {
            case 0:
                return SODAMATCH;
            case 1:
                return SODARANKING;
            case 2:
                return SODAGOAL;
            case 3:
                return SODAASSIST;
            case 4:
                return SODAMATCHSTAT;
            case 5:
                return SODAMATCHRESULT;
            case 6:
                return SODATIMELINE;
            default:
                return null;
        }
    }

}
