package com.lesports.sms.data.service.soda;

import com.google.common.collect.Lists;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.data.service.Parser;
import com.lesports.sms.data.util.StatsConstants;
import com.lesports.sms.model.*;
import com.lesports.sms.service.*;
import com.lesports.utils.LeDateUtils;
import com.lesports.utils.LeProperties;
import com.lesports.utils.math.LeNumberUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.*;

/**
 * Created by zhonglin on 2016/4/25.
 */
@Service("sodaFormationParser")
public class SodaFormationParser  extends Parser {
    private static Logger logger = LoggerFactory.getLogger(SodaFormationParser.class);
    private final String SODA_MATCH_RESULT_FILE_PATH = LeProperties.getString("soda.csl.matchresult.file.path");
//    private final String SODA_MATCH_RESULT_FILE_PATH = "E:\\soda\\";


    public List<Match.Squad> parseSquads(Match match,Match.Squad squad1,Match.Squad squad2) {

        List<Match.Squad> squads = Lists.newArrayList();

        String name = "s9-282-2017-"+match.getSodaId()+"-matchresult.xml";
        String filePath = SODA_MATCH_RESULT_FILE_PATH + name;
        try {
            File xmlFile = new File(filePath);
            if (!xmlFile.exists()) {
                logger.warn("parsing the file:{} not exists", filePath);
                return squads;
            }

            SAXReader reader = new SAXReader();
            Document document = reader.read(xmlFile);
            Element rootElement = document.getRootElement();

            Element matchElement = rootElement.element("Match");
            Iterator<Element> teamEventIterator = matchElement.elementIterator("Team");

            //按队处理阵型
            while(teamEventIterator.hasNext()){
                Element teamEvent = teamEventIterator.next();
                String tSodaId = teamEvent.attributeValue("id");
                Team team = SbdsInternalApis.getTeamBySodaId(tSodaId);
                if(team==null){
                    continue;
                }

                String formation = teamEvent.elementText("Formation_used");
                String newFormation = "";
                if(StringUtils.isNotBlank(formation)){
                    for(int i=0;i<formation.length();i++){
                        newFormation += formation.charAt(i) + "-";
                    }
                    newFormation = newFormation.substring(0,newFormation.length()-1);
                }
                Element PlayerLineUpElement = teamEvent.element("PlayerLineUp");
                Match.Squad squad = new Match.Squad();
                squad.setTid(team.getId());
                squad.setPlayers(generateSimplePlayer(PlayerLineUpElement,team.getId(),match.getCsid(),match.getCid(),formation,newFormation));
                squad.setFormation(newFormation);
                squads.add(squad);
            }
            logger.info("soda formation  parser end");
        } catch (Exception e) {
            logger.error("parse soda formation  file {}  error", filePath,e);
        }
        return squads;
    }

    //球队阵容
    private List<Match.SimplePlayer> generateSimplePlayer(Element playerLineUp, Long tid, Long csid, Long cid,String formation,String newFormation) {
        List<Match.SimplePlayer> simplePlayers = new ArrayList<Match.SimplePlayer>();
        try {

            if (null != playerLineUp) {
                Iterator playerIterator = playerLineUp.elementIterator("Player");

                while (playerIterator.hasNext()) {
                    Match.SimplePlayer simplePlayer = new Match.SimplePlayer();

                    Element teamPlayer = (Element) playerIterator.next();
                    String pSodaId = teamPlayer.attributeValue("id");
                    String pName = teamPlayer.attributeValue("name");
                    String playerNumber =teamPlayer.attributeValue("shirtNumber");
                    String position = teamPlayer.attributeValue("position");
                    if (StringUtils.isNotEmpty(playerNumber)){
                        simplePlayer.setNumber(Integer.parseInt(playerNumber));
                    }

                    Player player = SbdsInternalApis.getPlayerBySodaId(pSodaId);
                    if (null != player) {
                        simplePlayer.setPid(player.getId());
                    } else {
                        logger.warn("the player not found,pSodaId:{}", pSodaId);
                        player = new Player();
                        player.setName(pName);
                        player.setCreateAt(LeDateUtils.formatYYYYMMDDHHMMSS(new Date()));
                        player.setDeleted(false);

                        List<DictEntry> dictEntries = SbdsInternalApis.getDictEntriesByName("^足球$");
                        if (CollectionUtils.isNotEmpty(dictEntries)) {
                            player.setGameFType(dictEntries.get(0).getId());
                        }
                        Set<Long> cids = player.getCids();
                        cids.add(cid);
                        player.setCids(cids);
                        player.setSodaId(pSodaId);
                        player.setAllowCountries(getAllowCountries());
                        player.setOnlineLanguages(getOnlineLanguage());
                        Long pid = SbdsInternalApis.savePlayer(player);
                        if(pid <=0 ){
                            continue;
                        }
                        simplePlayer.setPid(player.getId());

                        //赛季里增加球员
                        TeamSeason teamSeason = new TeamSeason();
                        List<TeamSeason> teamSeasons = SbdsInternalApis.getTeamSeasonsByTeamIdAndCSId(tid, csid);
                        if (CollectionUtils.isNotEmpty(teamSeasons)) {
                            SbdsInternalApis.addTeamPlayer(teamSeasons.get(0).getId(), player.getId(), Long.valueOf(playerNumber));
                        } else {
                            teamSeason.setCsid(csid);
                            teamSeason.setTid(tid);
                            SbdsInternalApis.saveTeamSeason(teamSeason);
                            SbdsInternalApis.addTeamPlayer(teamSeason.getId(), player.getId(), Long.valueOf(playerNumber));
                        }
                    }
                    String[] positions = newFormation.split("-");


                    long pos = 0;
                    Iterator<Element> statIterator = teamPlayer.elementIterator("Stat");
                    while(statIterator.hasNext()){
                        Element statElement = statIterator.next();
                        if("formation_place".equals(statElement.attributeValue("type"))){
                            pos = LeNumberUtils.toLong(statElement.getText());
                        }
                    }
                    if(StatsConstants.squadOrderMap.get(formation+"_"+pos)!=null){
                        pos = LeNumberUtils.toLong(StatsConstants.squadOrderMap.get(formation + "_" + pos));
                    }

                    Long id = 0L;


//                    if(pos==1){
//                        id = SbdsInternalApis.getDictEntriesByName("^守门员$").get(0).getId();
//                    }else if(pos>1 && pos<=(1+ LeNumberUtils.toLong(positions[0]))){
//                        id = SbdsInternalApis.getDictEntriesByName("^后卫$").get(0).getId();
//                    }
//                    else if(pos> (11- LeNumberUtils.toLong(positions[positions.length - 1]))){
//                        id = SbdsInternalApis.getDictEntriesByName("^前锋$").get(0).getId();
//                    }
//                    else{
//                        id = SbdsInternalApis.getDictEntriesByName("^中场$").get(0).getId();
//                    }


                    if ("门将".equals(position)) {
                        id = SbdsInternalApis.getDictEntriesByName("^守门员$").get(0).getId();
                    }
                    if ("后卫".equals(position) || "中后卫".equals(position)) {
                        id = SbdsInternalApis.getDictEntriesByName("^后卫$").get(0).getId();
                    }
                    if ("前卫".equals(position) || "后腰".equals(position)) {
                        id = SbdsInternalApis.getDictEntriesByName("^中场$").get(0).getId();
                    }
                    if ("前锋".equals(position) || "中前卫".equals(position)) {
                        id = SbdsInternalApis.getDictEntriesByName("^前锋$").get(0).getId();
                    }

                    simplePlayer.setSquadOrder(pos+"");

                    String substitute = teamPlayer.attributeValue("status");

                    //是否首发
                    if ("首发".equals(substitute)) {
                        simplePlayer.setStarting(true);
                        simplePlayer.setPosition(id);
                    } else {
                        simplePlayer.setStarting(false);
                    }
                    simplePlayers.add(simplePlayer);
                }
            }
        } catch (Exception e) {
            logger.error("generateSimplePlayer  error", e);
        }
        return simplePlayers;
    }
}