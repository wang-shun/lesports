package com.lesports.sms.data.Generator.impl;

import com.google.common.collect.Lists;
import com.hankcs.hanlp.corpus.util.StringUtils;
import com.lesports.query.InternalCriteria;
import com.lesports.query.InternalQuery;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.data.Generator.Generator;
import com.lesports.sms.data.model.Constants;
import com.lesports.sms.data.model.TransModel;
import com.lesports.sms.data.parser.FileParser;
import com.lesports.sms.model.Player;
import com.lesports.sms.model.Team;
import com.lesports.sms.model.TeamSeason;
import com.lesports.utils.LeDateUtils;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by qiaohongxin on 2016/11/17.
 */
@Service("defaultPlayerStatsGenerator")
public class DefaultPlayerStatsGenerator extends Generator {
    @Resource
    FileParser fileParser;
    private static Logger LOG = LoggerFactory.getLogger(DefaultPlayerStatsGenerator.class);

    public boolean nextProcessor() {
        List<TransModel> playerstats = getFileUrl(Constants.playerStats);
        if (CollectionUtils.isEmpty(playerstats)) {
            return false;
        }
        for (TransModel transModel : playerstats) {
            fileParser.nextProcessor("PLAYER-STATS", transModel);
        }

        return true;
    }

    @Override
    public List<TransModel> getFileUrl(MultiValueMap<Long, String> files) {
        List<TransModel> transModels = Lists.newArrayList();
        for (Map.Entry<Long, List<String>> entry : files.entrySet()) {
            Long csid = (Long) entry.getKey();
            entry.getValue();
            for (String valueContent : entry.getValue()) {
                String[] names = valueContent.split("\\---");
                String fileSupportor = names[0];
                if (fileSupportor.contains("479")) {
                    List<String> partnerCodes = getValidCodeByCsidAndPartner(csid);
                    if (CollectionUtils.isEmpty(partnerCodes)) continue;
                    for (String curentCode : partnerCodes) {
                        TransModel currentFiles = new TransModel();
                        currentFiles.setCsid(csid);
                        currentFiles.setPartnerType(null);
                        currentFiles.setFileUrl(names[2].replace("t1:?", "t1:" + curentCode));
                        currentFiles.setAnotationType(names[1]);
                        currentFiles.setPartnerId(curentCode);
                        transModels.add(currentFiles);
                    }
                }
            }
        }

        return transModels;
    }

    private List<String> getValidCodeByCsidAndPartner(Long csid) {
        InternalQuery query = new InternalQuery();
        query.addCriteria(new InternalCriteria("csid", "is", csid));
        List<TeamSeason> teamSeasons = SbdsInternalApis.getTeamSeasonsByQuery(query);
        if (CollectionUtils.isEmpty(teamSeasons)) return null;
        List<String> ids = Lists.newArrayList();
        for (TeamSeason currentTeamSeason : teamSeasons) {
            if (CollectionUtils.isEmpty(currentTeamSeason.getPlayers())) continue;
            for (TeamSeason.TeamPlayer player : currentTeamSeason.getPlayers()) {
                Player currentPlayer = SbdsInternalApis.getPlayerById(player.getPid());
                if (StringUtils.isBlankOrNull(currentPlayer.getQQId())) continue;
                ids.add(currentPlayer.getQQId());
            }
        }
        return ids;
    }

}
