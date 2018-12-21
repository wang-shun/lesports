package com.lesports.sms.data.util;


import jxl.*;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zhonglin on 2016/3/10.
 */
public class SodaContant {

    // 搜达赛事对应关系表
    public static final Map<String, Long> cidMap = new HashMap<String, Long>();
    // 赛事和年份定位赛季
    public static final Map<String, Long> csidMap = new HashMap<String, Long>();
    // 12强球队
    public static final Map<String, Long> top12Map = new HashMap<String, Long>();
    // 12强已存在球队
    public static final Map<String, Long> top12ExistMap = new HashMap<String, Long>();

    // 12强 搜达赛程
    public static final Map<String,String> top12MatchSodaIdMap = new HashMap<String,String>();

    //需要解析的赛事
    public static final Map<String,Long> parserCidMap = new HashMap<String,Long>();



    public static String localDownloadPath = "/letv/third_part_files/soda/";
    public static String localDownloadPathTest = "E:\\soda\\";

    static {

        cidMap.put("282",47001L);//中超
        cidMap.put("26",100234001L);//亚冠
        cidMap.put("412-2",309001L);//世界杯预选赛40强
        cidMap.put("412",101425001L);//世界杯预选赛12强
//        cidMap.put("262",100322001L);//友谊赛
        cidMap.put("427",100352001L);//奥运会预选赛
        cidMap.put("5",42001L);//亚洲杯


        parserCidMap.put("412",101425001L); //12强
        parserCidMap.put("133",20001L);//英超
        parserCidMap.put("372",29001L);//意甲
        parserCidMap.put("45",26001L);//西甲
        parserCidMap.put("115",32001L);//德甲
        parserCidMap.put("122",35001L);//法甲


        csidMap.put("47001-2016",100538002L);
        csidMap.put("47001-2015",47002L);
        csidMap.put("47001-2014",100670002L);
        csidMap.put("47001-2013",100671002L);
        csidMap.put("47001-2013",100672002L);

        csidMap.put("100234001-2016",100629002l);
        csidMap.put("100234001-2015",100431002l);

        top12Map.put("伊朗",1648L);
        top12Map.put("卡塔尔",1626L);
        top12Map.put("韩国",1623L);
        top12Map.put("中国",1653L);
        top12Map.put("乌兹别克",1644L);
        top12Map.put("叙利亚",5737L);
        top12Map.put("澳大利亚",1656L);
        top12Map.put("伊拉克",1647L);
        top12Map.put("日本",1637L);
        top12Map.put("阿联酋",1613L);
        top12Map.put("沙特阿拉伯",1638L);
        top12Map.put("泰国",1641L);

        top12ExistMap.put("伊朗",112084006L);
        top12ExistMap.put("卡塔尔",1645006L);
        top12ExistMap.put("韩国",2042006L);
        top12ExistMap.put("中国",1440006L);
        top12ExistMap.put("乌兹别克",880006L);
        top12ExistMap.put("叙利亚",111389006L);
        top12ExistMap.put("澳大利亚",868006L);
        top12ExistMap.put("伊拉克",907006L);
        top12ExistMap.put("日本",901006L);
        top12ExistMap.put("阿联酋",893006L);
        top12ExistMap.put("沙特阿拉伯",883006L);
        top12ExistMap.put("泰国",115413006L);


        top12MatchSodaIdMap.put("112084006,1645006","1070826");
        top12MatchSodaIdMap.put("2042006,1440006","1070827");
        top12MatchSodaIdMap.put("880006,111389006","1070828");
        top12MatchSodaIdMap.put("868006,907006","1070856");
        top12MatchSodaIdMap.put("901006,893006","1070857");
        top12MatchSodaIdMap.put("883006,115413006","1070858");
        top12MatchSodaIdMap.put("907006,883006","1070859");
        top12MatchSodaIdMap.put("115413006,901006","1070860");
        top12MatchSodaIdMap.put("893006,868006","1070861");
        top12MatchSodaIdMap.put("1440006,112084006","1070829");
        top12MatchSodaIdMap.put("1645006,880006","1070830");
        top12MatchSodaIdMap.put("111389006,2042006","1070831");
        top12MatchSodaIdMap.put("1440006,111389006","1070832");
        top12MatchSodaIdMap.put("2042006,1645006","1070833");
        top12MatchSodaIdMap.put("880006,112084006","1070834");
        top12MatchSodaIdMap.put("901006,907006","1070862");
        top12MatchSodaIdMap.put("883006,868006","1070863");
        top12MatchSodaIdMap.put("893006,115413006","1070864");
        top12MatchSodaIdMap.put("868006,901006","1070865");
        top12MatchSodaIdMap.put("907006,115413006","1070866");
        top12MatchSodaIdMap.put("883006,893006","1070867");
        top12MatchSodaIdMap.put("112084006,2042006","1070835");
        top12MatchSodaIdMap.put("1645006,111389006","1070836");
        top12MatchSodaIdMap.put("880006,1440006","1070837");
        top12MatchSodaIdMap.put("1440006,1645006","1070838");
        top12MatchSodaIdMap.put("2042006,880006","1070839");
        top12MatchSodaIdMap.put("111389006,112084006","1070840");
        top12MatchSodaIdMap.put("901006,883006","1070868");
        top12MatchSodaIdMap.put("115413006,868006","1070869");
        top12MatchSodaIdMap.put("893006,907006","1070870");
        top12MatchSodaIdMap.put("907006,868006","1070871");
        top12MatchSodaIdMap.put("115413006,883006","1070872");
        top12MatchSodaIdMap.put("893006,901006","1070873");
        top12MatchSodaIdMap.put("1440006,2042006","1070841");
        top12MatchSodaIdMap.put("1645006,112084006","1070842");
        top12MatchSodaIdMap.put("111389006,880006","1070843");
        top12MatchSodaIdMap.put("112084006,1440006","1070844");
        top12MatchSodaIdMap.put("2042006,111389006","1070845");
        top12MatchSodaIdMap.put("880006,1645006","1070846");
        top12MatchSodaIdMap.put("868006,893006","1070874");
        top12MatchSodaIdMap.put("901006,115413006","1070875");
        top12MatchSodaIdMap.put("883006,907006","1070876");
        top12MatchSodaIdMap.put("868006,883006","1070877");
        top12MatchSodaIdMap.put("907006,901006","1070878");
        top12MatchSodaIdMap.put("115413006,893006","1070879");
        top12MatchSodaIdMap.put("112084006,880006","1070847");
        top12MatchSodaIdMap.put("1645006,2042006","1070848");
        top12MatchSodaIdMap.put("111389006,1440006","1070849");
        top12MatchSodaIdMap.put("1440006,880006","1070850");
        top12MatchSodaIdMap.put("2042006,112084006","1070851");
        top12MatchSodaIdMap.put("111389006,1645006","1070852");
        top12MatchSodaIdMap.put("901006,868006","1070880");
        top12MatchSodaIdMap.put("115413006,907006","1070881");
        top12MatchSodaIdMap.put("893006,883006","1070882");
        top12MatchSodaIdMap.put("868006,115413006","1070883");
        top12MatchSodaIdMap.put("907006,893006","1070884");
        top12MatchSodaIdMap.put("883006,901006","1070885");
        top12MatchSodaIdMap.put("112084006,111389006","1070853");
        top12MatchSodaIdMap.put("1645006,1440006","1070854");
        top12MatchSodaIdMap.put("880006,2042006","1070855");

    }

    public static final long getCidBySodaId(String sodaId) {
        if(cidMap.get(sodaId)!=null)return cidMap.get(sodaId);
        return 0;
    }

    public static final long getCsidByYearAndCid(String key) {
        if(csidMap.get(key)!=null)return csidMap.get(key);
        return  0;
    }

    public static void main(String[] args){
//        int i;
//        Sheet sheet;
//        Workbook book;
//        Cell cell1,cell2,cell3;
//        try {
//            //t.xls为要读取的excel文件名
//            book= Workbook.getWorkbook(new File("E:\\epl.xls"));
//            sheet=book.getSheet(0);
//            if(sheet == null){
//                System.out.println("sheet is null");
//            }
//
//            i=1;
//            while(true)
//            {
//                //获取每一行的单元格
//                cell1=sheet.getCell(0,i);//（列，行）
//                cell2=sheet.getCell(3,i);
//                cell3=sheet.getCell(4,i);
//
//                i++;
//                //如果读取的数据为空
//                if(StringUtils.isBlank(cell1.getContents())){
//                    break;
//                }
//
//            }
//            book.close();
//        }
//        catch(Exception e)  {
//            e.printStackTrace();
//        }


        String content ="/letv/third_part_files/soda/302/team/s0-team-356.xml";
        Pattern pattern = Pattern.compile("/soda/(.+?)/team/");
        Matcher matcher = pattern.matcher(content);
        if(matcher.find()){
            System.out.println(matcher.group(1));
        }
    }

}
