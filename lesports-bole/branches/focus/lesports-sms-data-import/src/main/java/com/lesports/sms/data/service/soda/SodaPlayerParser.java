package com.lesports.sms.data.service.soda;

import com.lesports.query.InternalCriteria;
import com.lesports.query.InternalQuery;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.data.service.IThirdDataParser;
import com.lesports.sms.data.service.Parser;
import com.lesports.sms.model.Player;
import com.lesports.sms.service.PlayerService;
import com.lesports.utils.math.LeNumberUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

/**
 * Created by ruiyuansheng on 2016/3/15.
 */
@Service("sodaPlayerParser")
public class SodaPlayerParser extends Parser implements IThirdDataParser {
    private static Logger logger = LoggerFactory.getLogger(SodaTeamParser.class);
    @Override
    public Boolean parseData(String file) {

        Boolean result = false;
        try {
            InternalQuery internalQuery = new InternalQuery();
            internalQuery.addCriteria(InternalCriteria.where("deleted").is(false));
            internalQuery.addCriteria(InternalCriteria.where("soda_id").exists(true));
            List<Player> playerList = SbdsInternalApis.getPlayersByQuery(internalQuery);

            if(CollectionUtils.isNotEmpty(playerList)) {
                for(Player player : playerList) {
                    int folder=folderNum(Integer.parseInt(player.getSodaId()));
                    file = "/letv/data/soda/basic/player/part"+folder+"/t101-player-"+player.getSodaId()+".xml";

                    File xmlFile = new File(file);
                    if (!xmlFile.exists()) {
                        logger.warn("parsing the file:{} not exists", file);
                       continue;
                    }
                    SAXReader reader = new SAXReader();
                    Document document = reader.read(xmlFile);
                    Element rootElmement = document.getRootElement();

                    // 赛事基本信息，赛事名称、当前赛季、当前轮次
                    Element playerElement = rootElmement.element("player");
                    String playerId = playerElement.attributeValue("id");

                    String careerValue = playerElement.elementText("careerValue");
                    String custumalLeg = playerElement.elementText("custumalLeg");
                    String birthplace = playerElement.elementText("birthplace");
                    String note = playerElement.elementText("note");


                    if (null != player) {
                        player.setCity(birthplace);
                        player.setMultiLangCities(getMultiLang(birthplace));
                        if("万欧元".equals(player.getCareerValue())){
                            player.setCareerValue(null);
                        }
                        if(StringUtils.isNotEmpty(careerValue)) {
                            player.setCareerValue(careerValue + "万欧元");
                        }
                        player.setHeavyFoot(custumalLeg);
                        player.setDesc(note);
                        SbdsInternalApis.savePlayer(player);
                    }
                }
            }



        } catch (Exception e) {
            logger.error("insert into match  error: ", e);
        }
        return result;


    }
    private  int folderNum(int id){
        return (id%2000>0)?(id/2000+1):(id/2000);

    }


}
