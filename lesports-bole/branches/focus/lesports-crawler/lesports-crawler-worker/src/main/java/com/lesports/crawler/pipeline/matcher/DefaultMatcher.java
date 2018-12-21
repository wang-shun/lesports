package com.lesports.crawler.pipeline.matcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.lesports.bole.model.BoleCompetition;
import com.lesports.bole.model.BoleCompetitor;
import com.lesports.bole.model.BoleCompetitorType;
import com.lesports.bole.model.BoleMatch;
import com.lesports.bole.repository.BoleCompetitionRepository;
import com.lesports.bole.repository.BoleCompetitorRepository;
import com.lesports.bole.repository.BoleMatchRepository;
import com.lesports.crawler.model.source.SourceMatch;
import com.lesports.crawler.utils.SpringUtils;

/**
 * 通用赛事/对阵/比赛匹配器
 * 
 * @author denghui
 *
 */
@Component("DefaultMatcher")
public class DefaultMatcher {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private BoleCompetitionRepository bcRepo = SpringUtils.getBean(BoleCompetitionRepository.class);
    private BoleCompetitorRepository bcrRepo = SpringUtils.getBean(BoleCompetitorRepository.class);
    private BoleMatchRepository bmRepo = SpringUtils.getBean(BoleMatchRepository.class);

    protected String preprocessCompetition(String value) {
        return value;
    }

    /**
     * 用给定值与SMS中赛事匹配
     * 
     * @param value
     * @return 匹配到的赛事或者null
     */
    public BoleCompetition matchCompetition(String value) {
        String preprocessed = preprocessCompetition(value);
        BoleCompetition bc = bcRepo.matchByAbbreviation(preprocessed);
        return bc;
    }

    /**
     * 用给定值与SMS中球队匹配
     * 
     * @param value
     * @return 匹配到的球队ID或者0l
     */
    public BoleCompetitor matchTeam(String value, String gameFName) {
        BoleCompetitor team = bcrRepo.matchOne(BoleCompetitorType.TEAM, value, gameFName);
        return team;
    }

    /**
     * 用给定值与SMS中球员匹配
     * 
     * @param value
     * @return 匹配到的球员ID或者0l
     */
    public BoleCompetitor matchPlayer(String value, String gameFName) {
        BoleCompetitor player = bcrRepo.matchOne(BoleCompetitorType.PLAYER, value, gameFName);
        return player;
    }

    /**
     * 用给定值与SMS中比赛匹配
     * 
     * @param value
     * @return 匹配到的比赛ID或者0l
     */
    public BoleMatch matchMatch(SourceMatch value, Long competitor1, Long competitor2) {
        // 按开始时间与SMS中比赛匹配
        BoleMatch bm = bmRepo.matchByStartTimeAndCompetitors(value.getStartTime(), competitor1, competitor2);
        if (bm == null) {
            LOGGER.info("match match failed with id {}, competitor1 {}, competitor2 {}", value.getSourceId(), competitor1, competitor2);
            return null;
        }

        return bm;
    }
}
