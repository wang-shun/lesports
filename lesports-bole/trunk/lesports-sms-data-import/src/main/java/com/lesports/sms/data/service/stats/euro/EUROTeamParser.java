package com.lesports.sms.data.service.stats.euro;

import com.lesports.query.InternalCriteria;
import com.lesports.query.InternalQuery;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.data.model.Constants;
import com.lesports.sms.data.service.IThirdDataParser;
import com.lesports.sms.model.Team;
import org.apache.commons.collections.CollectionUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Iterator;
import java.util.List;

/**
 * Created by zhonglin on 2016/5/18.
 */
@Service("EUROTeamParser")
public class EUROTeamParser  implements IThirdDataParser {

    private static Logger logger = LoggerFactory.getLogger(EUROTeamParser.class);

    private final Long GAME_F_TYPE = 100015000L;

    @Override
    public Boolean parseData(String file) {
        Boolean result = false;
        try {
            File xmlFile = new File(file);
            if (!xmlFile.exists()) {
                System.out.println("parsing the file:{} not exists");
                return result;
            }
            SAXReader reader = new SAXReader();
            Document document = reader.read(xmlFile);
            Element rootElmement = document.getRootElement();
            Element sportsRosters = rootElmement.element("sports-roster");

            // get sport-rosters
            Element nbaRosters = sportsRosters.element("ifb-soccer-roster");
            Iterator<Element> euroRosterList = nbaRosters.elementIterator("ifb-team-roster");


            //球队
            while (euroRosterList.hasNext()) {
                Element euroRoster = euroRosterList.next();
                String partnerId = euroRoster.element("team-info").attributeValue("global-id");

                String teamName = "";
                if (Constants.nationalityMap.get(euroRoster.element("team-info").attributeValue("display-name")) != null) {
                    teamName = Constants.nationalityMap.get(euroRoster.element("team-info").attributeValue("display-name"));
                }

                InternalQuery internalQuery = new InternalQuery();
                internalQuery.addCriteria(InternalCriteria.where("name").is(teamName));
                internalQuery.addCriteria(InternalCriteria.where("game_f_type").is(GAME_F_TYPE));
                List<Team> pageTeam =  SbdsInternalApis.getTeamsByQuery(internalQuery);

                if(CollectionUtils.isNotEmpty(pageTeam)){
                    for(Team team:pageTeam){
                        if(!team.getName().equals(teamName)){
                            continue;
                        }
                        team.setStatsId(partnerId);
                        SbdsInternalApis.saveTeam(team);
                        System.out.println("new team is added sucessfully " + team.getPartnerId());
                    }
                }

            }
            result = true;
        } catch (Exception e) {
            logger.error("team and player updated error: ", e);
        }
        return result;
    }
}
