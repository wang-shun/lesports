package com.lesports.sms.data.job.Top12Job;

import com.google.common.collect.Lists;
import com.hankcs.hanlp.corpus.util.StringUtils;
import com.lesports.query.InternalCriteria;
import com.lesports.query.InternalQuery;
import com.lesports.sms.api.common.MatchStatus;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.data.job.ThirdDataJob;
import com.lesports.sms.data.model.Constants;
import com.lesports.sms.data.service.soda.top12.PlayerPageContentParser;
import com.lesports.sms.data.util.FtpUtil;
import com.lesports.sms.model.Match;
import com.lesports.sms.model.Team;
import com.lesports.sms.model.TeamSeason;
import com.lesports.utils.LeDateUtils;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service("Top12LiveScoreJob")
public class Top12SquadJob extends ThirdDataJob {
    private static Logger LOG = LoggerFactory.getLogger(Top12SquadJob.class);

    public void run() {
        List<String> fileNames = getValidMatchSOdaId(Constants.TOP12_COMPETITION_SEASON_ID);
        if (CollectionUtils.isEmpty(fileNames)) return;
        FtpUtil ftpUtil = new FtpUtil(Constants.LocalFtpHost, Constants.LocalFtpPort, Constants.LocalFtpUserName, Constants.LocalFtpPassword);
        for (String sodaId : fileNames) {
            super.downloadAndParseData(ftpUtil, "liveScoreJob", "s9-412-2018-" + sodaId + "-matchresult.xml", Constants.SODA_LIVE_PATH, "//soda//412//matchresult//", null);
            super.downloadAndParseData(ftpUtil, "liveEventJob", "s8-412-2018-" + sodaId + "-timeline.xml", Constants.SODA_LIVE_PATH, "//soda//412//timeline//", null);

        }
    }

    private List<String> getValidMatchSOdaId(Long csid) {
        String currentDate = LeDateUtils.formatYYYYMMDD(new Date());
        InternalQuery query = new InternalQuery();
        query.addCriteria(new InternalCriteria("csid", "is", csid));
        query.addCriteria(new InternalCriteria("status", "eq", MatchStatus.MATCHING));
        InternalQuery query2 = new InternalQuery();
        query2.addCriteria(new InternalCriteria("csid", "is", csid));
        query2.addCriteria(new InternalCriteria("start_date", "eq", currentDate));
        //  query.addCriteria(c1);
        List<Match> matches1 = SbdsInternalApis.getMatchsByQuery(query2);
        List<Match> matches2 = SbdsInternalApis.getMatchsByQuery(query);
        List<Match> matches = Lists.newArrayList();
        if (CollectionUtils.isEmpty(matches1) && CollectionUtils.isEmpty(matches2)) return null;
        else if (CollectionUtils.isEmpty(matches1)) matches = matches1;
        else if (CollectionUtils.isEmpty(matches2)) matches = matches1;
        else {
            matches.addAll(matches1);
            matches.addAll(matches2);
        }

        if (CollectionUtils.isEmpty(matches)) return null;
        List<String> ids = Lists.newArrayList();
        for (Match currentMatch : matches) {
            if (StringUtils.isBlankOrNull(currentMatch.getSodaId())) continue;
            ids.add(currentMatch.getSodaId());
        }
        return ids;
    }

}
