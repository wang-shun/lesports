package com.lesports.sms.data.service.soda;

import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.data.service.IThirdDataParser;
import com.lesports.sms.data.service.Parser;
import com.lesports.sms.model.*;
import com.lesports.sms.service.*;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;

/**
 * Created by ruiyuansheng on 2016/2/23.
 */
@Service("sodaTeamParser")
public class SodaTeamParser extends Parser implements IThirdDataParser {

    private static Logger logger = LoggerFactory.getLogger(SodaTeamParser.class);


    @Override
    public Boolean parseData(String file) {


        Boolean result = false;
        try {
            File xmlFile = new File(file);
            if (!xmlFile.exists()) {
                logger.warn("parsing the file:{} not exists", file);
                return result;
            }
            SAXReader reader = new SAXReader();
            Document document = reader.read(xmlFile);
            Element rootElmement = document.getRootElement();

            // 赛事基本信息，赛事名称、当前赛季、当前轮次
            Element teamElement = rootElmement.element("team");
            String competitionName = teamElement.element("competition").attributeValue("name");
            String desc = teamElement.elementText("history");
            String stadium = teamElement.elementText("stadium");

            String teamId = teamElement.attributeValue("id");

            Team team = SbdsInternalApis.getTeamBySodaId(teamId);

            if (null != team) {
                team.setDesc(desc);
                team.setMultiLangDesc(getMultiLang(desc));
                team.setHomeGround(stadium);
                team.setMultiLangHomeGrounds(getMultiLang(stadium));
                SbdsInternalApis.saveTeam(team);
            }


        } catch (Exception e) {
            logger.error("insert into match  error: ", e);
        }
        return result;


    }


}
