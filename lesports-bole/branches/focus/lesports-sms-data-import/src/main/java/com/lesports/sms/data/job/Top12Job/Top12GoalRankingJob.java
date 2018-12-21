package com.lesports.sms.data.job.Top12Job;

import com.google.common.collect.Lists;
import com.lesports.query.InternalCriteria;
import com.lesports.query.InternalQuery;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.data.job.ThirdDataJob;
import com.lesports.sms.data.model.Constants;
import com.lesports.sms.data.service.soda.top12.PlayerPageContentParser;
import com.lesports.sms.data.service.soda.top12.Top12PlayerGoalParser;
import com.lesports.sms.data.util.FtpUtil;
import com.lesports.sms.model.Team;
import com.lesports.sms.model.TeamSeason;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service("top12GoalRankingJob")
public class Top12GoalRankingJob extends ThirdDataJob {
    @Resource
    private Top12PlayerGoalParser top12PlayerGoalParser;
    private static Logger LOG = LoggerFactory.getLogger(Top12GoalRankingJob.class);

    public void run() {
        FtpUtil ftpUtil = new FtpUtil(Constants.LocalFtpHost, Constants.LocalFtpPort, Constants.LocalFtpUserName, Constants.LocalFtpPassword);
        super.downloadAndParseData(ftpUtil, "playerGoalRankingJob", "s4-412-2018-player-goal.xml", Constants.SODA_PLAYER_RANKING_PATH, "//soda//412//top//", top12PlayerGoalParser);
    }



}
