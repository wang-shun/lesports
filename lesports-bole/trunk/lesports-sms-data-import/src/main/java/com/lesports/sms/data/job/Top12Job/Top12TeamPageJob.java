package com.lesports.sms.data.job.Top12Job;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import com.lesports.query.InternalCriteria;
import com.lesports.query.InternalQuery;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.data.job.ThirdDataJob;
import com.lesports.sms.data.model.Constants;
import com.lesports.sms.data.service.soda.top12.PlayerPageContentParser;
import com.lesports.sms.data.service.soda.top12.Top12TeamParser;
import com.lesports.sms.data.util.FtpUtil;
import com.lesports.sms.model.CompetitionSeason;
import com.lesports.sms.model.Team;
import com.lesports.sms.model.TeamSeason;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service("teamPageJob")
public class Top12TeamPageJob extends ThirdDataJob {
    @Resource
    private Top12TeamParser top12TeamParser;
    private static Logger LOG = LoggerFactory.getLogger(Top12TeamPageJob.class);

    public void run() {
        List<String> files = getTeamFileNameByCsid(Constants.TOP12_COMPETITION_SEASON_ID);
        if (CollectionUtils.isEmpty(files)) return;
        FtpUtil ftpUtil = new FtpUtil(Constants.LocalFtpHost, Constants.LocalFtpPort, Constants.LocalFtpUserName, Constants.LocalFtpPassword);
        for (String filename : files) {
            super.downloadAndParseData(ftpUtil, "teamPageJob", filename, Constants.SODA_TEAM_PATH, "//soda//412//team//", top12TeamParser);
        }
    }


    private List<String> getTeamFileNameByCsid(Long csid) {
        InternalQuery query = new InternalQuery();
        query.addCriteria(new InternalCriteria("csid", "is", csid));
        List<TeamSeason> currentSeasons = SbdsInternalApis.getTeamSeasonsByQuery(query);
        if (CollectionUtils.isEmpty(currentSeasons)) return null;
        List<String> teams = Lists.newArrayList();
        for (TeamSeason teamSeason : currentSeasons) {
            if (teamSeason.getTid() == null) continue;
            Team curentTeam = SbdsInternalApis.getTeamById(teamSeason.getTid());
            if (curentTeam == null || curentTeam.getSodaId() == null) continue;
            teams.add("s0-team-" + curentTeam.getSodaId() + ".xml");
        }
        return teams;
    }
}
