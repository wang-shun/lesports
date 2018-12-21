package com.lesports.sms.cooperation;

import com.lesports.AbstractIntegrationTest;
import com.lesports.sms.cooperation.service.copaAmerican.BaiduPC_scheduleGenerator;
import com.lesports.sms.cooperation.service.copaAmerican.Baidu_player_rankingGenerator;
import com.lesports.sms.cooperation.service.copaAmerican.Three_mobile_Generatator;
import org.junit.Test;

import javax.annotation.Resource;

/**
 * Created by qiaohongxin on 2015/9/14.
 */
public class TestScript extends AbstractIntegrationTest {
    @Resource
    BaiduPC_scheduleGenerator baiduPCScheduleGenerator;
    @Resource
    Baidu_player_rankingGenerator player_rankingGenerator;
    @Resource
    Three_mobile_Generatator three_mobile_generatator;
    @Test
    public void testSchedule(){
      //  baiduPCScheduleGenerator.uploadXmlFile();
      //  player_rankingGenerator.uploadXmlFile();
        three_mobile_generatator.uploadXmlFile();
    }


}






