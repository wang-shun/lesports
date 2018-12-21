package com.lesports.sms.data.service.soda;

import com.google.common.collect.Lists;
import com.lesports.AbstractIntegrationTest;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.data.util.MD5Util;
import com.lesports.sms.model.CompetitionSeason;
import com.lesports.sms.service.MatchService;
import org.junit.Test;

import javax.annotation.Resource;
import java.io.File;
import java.util.List;

/**
 * Created by zhonglin on 2016/3/8.
 */
public class SodaTeamSkillParserTest extends AbstractIntegrationTest {

    @Resource
    private SodaTeamSkillParser sodaTeamSkillParser;

    @Test
    public void testSodaTeamSkillParser() {
        String name = "5829|5505|5504|8994|5506|5508|3321|5837|11108|8733|7875|456|5773|11093|5791|5827";
        String[] fileNames = name.split("\\|");
        if(null != fileNames) {
            for (String fileName : fileNames) {
                String filePath = "E:\\soda\\2016\\" + "t315-team-skill-"+fileName+".xml";
                sodaTeamSkillParser.parseData(filePath);
            }
        }

//        CompetitionSeason competitionSeason = SbdsInternalApis.getLatestCompetitionSeasonByCid(47001);
//        System.out.println("csid: " + competitionSeason.getId());
    }
}
