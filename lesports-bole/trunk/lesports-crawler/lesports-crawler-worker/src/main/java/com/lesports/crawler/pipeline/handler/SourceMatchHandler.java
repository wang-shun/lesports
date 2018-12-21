package com.lesports.crawler.pipeline.handler;

import java.util.Date;

import org.apache.commons.beanutils.LeBeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.dao.DuplicateKeyException;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Strings;
import com.lesports.bole.model.BoleCompetition;
import com.lesports.bole.model.BoleCompetitionSeason;
import com.lesports.bole.model.BoleCompetitor;
import com.lesports.bole.model.BoleCompetitorType;
import com.lesports.bole.model.BoleLive;
import com.lesports.bole.model.BoleMatch;
import com.lesports.bole.model.BoleStatus;
import com.lesports.bole.repository.BoleCompetitionRepository;
import com.lesports.bole.repository.BoleCompetitionSeasonRepository;
import com.lesports.bole.repository.BoleCompetitorRepository;
import com.lesports.bole.repository.BoleLiveRepository;
import com.lesports.bole.repository.BoleMatchRepository;
import com.lesports.crawler.model.config.OutputConfig;
import com.lesports.crawler.model.source.Content;
import com.lesports.crawler.model.source.Source;
import com.lesports.crawler.model.source.SourceBoleMapping;
import com.lesports.crawler.model.source.SourceLive;
import com.lesports.crawler.model.source.SourceMatch;
import com.lesports.crawler.model.source.SourceResult;
import com.lesports.crawler.model.source.SourceValueType;
import com.lesports.crawler.pipeline.DataAttachHandler;
import com.lesports.crawler.pipeline.Model;
import com.lesports.crawler.pipeline.matcher.DefaultMatcher;
import com.lesports.crawler.repository.OutputConfigRepository;
import com.lesports.crawler.repository.SourceBoleMappingRepository;
import com.lesports.crawler.repository.SourceMatchRepository;
import com.lesports.crawler.utils.SpringUtils;
import com.lesports.id.api.IdType;
import com.lesports.id.client.LeIdApis;
import com.lesports.msg.core.ActionType;
import com.lesports.msg.core.LeMessage;
import com.lesports.msg.core.LeMessageBuilder;
import com.lesports.msg.core.MessageSource;
import com.lesports.msg.producer.SwiftMessageApis;
import com.lesports.utils.LeDateUtils;

import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;

/**
 * 处理抓取到的SouceMatch
 *
 * @author denghui
 */
@Model(clazz = SourceMatch[].class)
public class SourceMatchHandler implements DataAttachHandler<SourceMatch[]> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SourceMatchHandler.class);
    private SourceMatchRepository smRepo = SpringUtils.getBean(SourceMatchRepository.class);
    private SourceBoleMappingRepository sbmRepo = SpringUtils.getBean(SourceBoleMappingRepository.class);
    private BoleCompetitionRepository bcRepo = SpringUtils.getBean(BoleCompetitionRepository.class);
    private BoleCompetitionSeasonRepository boleCompetitionSeasonRepository = SpringUtils.getBean(BoleCompetitionSeasonRepository.class);
    private BoleCompetitorRepository bcrRepo = SpringUtils.getBean(BoleCompetitorRepository.class);
    private BoleMatchRepository bmRepo = SpringUtils.getBean(BoleMatchRepository.class);
    private BoleLiveRepository blRepo = SpringUtils.getBean(BoleLiveRepository.class);
    private OutputConfigRepository configRepo = SpringUtils.getBean(OutputConfigRepository.class);

    private static final Object BCSYNCOBJ = new Object();
    private static final Object BCRSYNCOBJ = new Object();
    private static final Object BMSYNCOBJ = new Object();

    @Override
    public boolean handle(ResultItems resultItems, Task task, SourceMatch[] sourceMatchs) {
        if (sourceMatchs == null || sourceMatchs.length <= 0) {
            return false;
        }

        for (SourceMatch item : sourceMatchs) {
            if (!check(item)) {
                LOGGER.info("invalid source match: {}", JSONObject.toJSONString(item));
                continue;
            }

            SourceMatch sm = item;
            // 查询之前是否抓取过
            SourceMatch oldSM = smRepo.getBySourceAndSourceId(item.getSource(), item.getSourceId());
            if (oldSM != null) {
                LeBeanUtils.copyNotEmptyPropertiesQuietly(oldSM, item);
                sm = oldSM;
            }
            saveSourceMatch(sm);

            // 合并之前抓取的信息后再检查
            if (!fullcheck(sm)) {
                LOGGER.info("incomplete source match: {}", JSONObject.toJSONString(sm));
                continue;
            }

            // 开始处理SourceMath
            DefaultMatcher matcher = getMatcher(sm.getSource());
            // 处理赛事
            SourceResult r1 = processCompetition(sm, matcher);
            if (!r1.getMatched()) {
                sm.setResult(r1);
                saveSourceMatch(sm);
                LOGGER.info("competition for source match {} is not handled, will drop this source match.", JSONObject.toJSONString(sm));
                continue;
            }

            // 处理赛季
            long boleCompetitionId = r1.getBoleId();
            SourceResult competitionSeasonResult = processCompetitionSeason(boleCompetitionId);
            if (!competitionSeasonResult.getMatched()) {
                sm.setResult(r1);
                saveSourceMatch(sm);
                LOGGER.info("competition season for source match {} is not handled, will drop this source match.", JSONObject.toJSONString(sm));
                continue;
            }

            if (Boolean.FALSE.equals(item.getVs())) {
                // 非对阵比赛不处理对阵
                SourceResult r4 = processMatch(sm, r1.getBoleId(), competitionSeasonResult.getBoleId(), null, null, matcher);
                sm.setResult(r4);
                saveSourceMatch(sm);
                continue;
            }

            // 处理对阵
            String competitor1 = sm.getCompetitors().get(0);
            SourceResult sourceResult1 = processCompetitor(sm, competitor1, matcher);
            if (!sourceResult1.getMatched()) {
                sm.setResult(sourceResult1);
                saveSourceMatch(sm);
                LOGGER.info("competitor {} for source match {} is not handled, will drop this source match.",
                        competitor1, JSONObject.toJSONString(sm));
                continue;
            }
            String competitor2 = sm.getCompetitors().get(1);
            SourceResult sourceResult2 = processCompetitor(sm, competitor2, matcher);
            if (!sourceResult2.getMatched()) {
                sm.setResult(sourceResult2);
                saveSourceMatch(sm);
                LOGGER.info("competitor {} for source match {} is not handled, will drop this source match.",
                        competitor2, JSONObject.toJSONString(sm));
                continue;
            }

            // 处理比赛
            SourceResult r4 = processMatch(sm, r1.getBoleId(), competitionSeasonResult.getBoleId(), sourceResult1.getBoleId(), sourceResult2.getBoleId(), matcher);
            sm.setResult(r4);
            saveSourceMatch(sm);
        }

        return true;
    }

    void saveSourceMatch(SourceMatch sm) {
        String now = LeDateUtils.formatYYYYMMDDHHMMSS(new Date());
        if (Strings.isNullOrEmpty(sm.getCreateAt())) {
            sm.setCreateAt(now);
        }
        sm.setUpdateAt(now);
        smRepo.save(sm);
    }

    DefaultMatcher getMatcher(Source source) {
        DefaultMatcher matcher = null;
        try {
            matcher = (DefaultMatcher) SpringUtils.getBean(source.toString().toUpperCase() + "Matcher");
        } catch (NoSuchBeanDefinitionException e) {
            matcher = (DefaultMatcher) SpringUtils.getBean("DefaultMatcher");
        }
        return matcher;
    }

    SourceResult processCompetition(SourceMatch match, DefaultMatcher matcher) {
        Source source = match.getSource();
        String value = match.getCompetition();
        boolean matched = false;
        // 查询Mapping
        SourceBoleMapping sbm = getMapping(source, SourceValueType.COMPETITION, value);
        if (sbm != null) {
            // 检查boleId状态
            BoleCompetition bc = bcRepo.findOne(sbm.getBoleId());
            if (bc == null) {
                String msg = String.format("bole competition with id %d not exists, but there is mapping for it.", sbm.getBoleId());
                throw new RuntimeException(msg);
            }

            // CREATED状态表示尚未匹配
            matched = BoleStatus.isMatched(bc.getStatus());
            LOGGER.info("competition {} from {} is created already, mapping to : {}.", value, source, bc.getId());
            return new SourceResult(SourceResult.STAGE_COMPETITION, matched, bc.getId());
        }

        BoleCompetition result = matcher.matchCompetition(value);
        Long boleId = 0l;
        if (result == null) {
            boleId = saveCompetition(match);
        } else {
            matched = BoleStatus.isMatched(result.getStatus());
            boleId = result.getId();
            saveMapping(source, SourceValueType.COMPETITION, value, boleId);
        }

        return new SourceResult(SourceResult.STAGE_COMPETITION, matched, boleId);
    }

    /**
     * 处理赛季
     *
     * @param boleCompetitionId
     * @return
     */
    SourceResult processCompetitionSeason(long boleCompetitionId) {

        BoleCompetitionSeason result = boleCompetitionSeasonRepository.getLatestByCid(boleCompetitionId);
        if (null != result) {
            return new SourceResult(SourceResult.STAGE_COMPETITION_SEASON, true, result.getId());
        }
        return new SourceResult(SourceResult.STAGE_COMPETITION_SEASON, false, boleCompetitionId);
    }

    private SourceBoleMapping getMapping(Source source, SourceValueType type, String value) {
        SourceBoleMapping sbm = sbmRepo.get(source, type, value, null);
        return sbm;
    }

    private SourceBoleMapping getCompetitorMapping(Source source, SourceValueType type, String value, String gameFName) {
        SourceBoleMapping sbm = sbmRepo.get(source, type, value, gameFName);
        return sbm;
    }
    
    SourceResult processCompetitor(SourceMatch match, String value, DefaultMatcher matcher) {
        Source source = match.getSource();
        boolean matched = false;
        // 查询Mapping
        SourceBoleMapping sbm = getCompetitorMapping(source, SourceValueType.COMPETITOR, value, match.getGameFName());
        if (sbm != null) {
            // 检查boleId状态
            BoleCompetitor bcr = bcrRepo.findOne(sbm.getBoleId());
            if (bcr == null) {
                String msg = String.format("bole competitor with id %d not exists, but there is mapping for it.", sbm.getBoleId());
                throw new RuntimeException(msg);
            }

            // CREATED状态表示尚未匹配
            matched = BoleStatus.isMatched(bcr.getStatus());
            LOGGER.info("competitor {} from {} is created already, mapping to : {}.", value, source, bcr.getId());
            return new SourceResult(SourceResult.STAGE_COMPETITOR, matched, bcr.getId());
        }

        BoleCompetitorType type = null;
        // 匹配球队
        BoleCompetitor bcr = matcher.matchTeam(value, match.getGameFName());
        if (bcr != null) {
            type = BoleCompetitorType.TEAM;
        } else {
            // 匹配球员
            bcr = matcher.matchPlayer(value, match.getGameFName());
            if (bcr != null) {
                type = BoleCompetitorType.PLAYER;
            }
        }

        Long boleId = 0l;
        if (type == null) {
            // 对阵类型为null
            boleId = saveCompetitor(match, type, value);
        } else {
            matched = BoleStatus.isMatched(bcr.getStatus());
            boleId = bcr.getId();
            saveMapping(source, SourceValueType.COMPETITOR, value, boleId);
        }

        return new SourceResult(SourceResult.STAGE_COMPETITOR, matched, boleId);
    }

    SourceResult processMatch(SourceMatch value, Long cid, Long csid, Long crid1, Long crid2, DefaultMatcher matcher) {
        boolean matched = false;
        // 查询Mapping
        SourceBoleMapping sbm = getMapping(value.getSource(), SourceValueType.MATCH, value.getSourceId());
        if (sbm != null) {
            // 检查boleId状态
            final BoleMatch bm = bmRepo.findOne(sbm.getBoleId());
            if (bm == null) {
                String msg = String.format("bole match with id %d not exists, but there is mapping for it.", sbm.getBoleId());
                throw new RuntimeException(msg);
            }

            // 更新BoleLive:删除之前的,创建新的
            saveLives(value, cid, bm.getId());
            matched = BoleStatus.isMatched(bm.getStatus());
            return new SourceResult(SourceResult.STAGE_MATCH, matched, bm.getId());
        }

        Long boleId = 0l;
        if (crid1 == null && crid2 == null) {
            // 非对阵比赛直接保存
            boleId = saveMatch(value, cid, csid, crid1, crid2);
        } else {
            // 尝试匹配
            BoleMatch bm = matcher.matchMatch(value, crid1, crid2);
            if (bm == null) {
                boleId = saveMatch(value, cid, csid, crid1, crid2);
            } else {
                matched = BoleStatus.isMatched(bm.getStatus());
                boleId = bm.getId();
                saveMapping(value.getSource(), SourceValueType.MATCH, value.getSourceId(), boleId);
            }
        }

        saveLives(value, cid, boleId);
        return new SourceResult(SourceResult.STAGE_MATCH, matched, boleId);
    }

    private Long saveMatch(SourceMatch value, Long cid, Long csid, Long crid1, Long crid2) {
        SourceBoleMapping mapping = getMapping(value.getSource(), SourceValueType.MATCH, value.getSourceId());
        if (mapping == null) {
            synchronized (BMSYNCOBJ) {
                mapping = getMapping(value.getSource(), SourceValueType.MATCH, value.getSourceId());
                if (mapping == null) {
                    // 创建比赛,暂不处理赛季
                    String now = LeDateUtils.formatYYYYMMDDHHMMSS(new Date());
                    BoleMatch bm = new BoleMatch();
                    bm.setId(LeIdApis.nextId(IdType.BOLE_MATCH));
                    bm.setName(value.getName());
                    bm.setCid(cid);
                    bm.setCsid(csid);
                    if (crid1 != null && crid2 != null) {
                        bm.addCompetitor(crid1);
                        bm.addCompetitor(crid2);
                    }
                    bm.setRound(value.getRound());
                    bm.setStartTime(value.getStartTime());
                    bm.setStatus(BoleStatus.CREATED);
                    bm.setSource(value.getSource());
                    bm.setSourceMatchId(value.getId());
                    bm.setCreateAt(now);
                    bm.setUpdateAt(now);
                    LOGGER.info("match {} will be created for source match {}.",
                            JSONObject.toJSONString(bm), JSONObject.toJSONString(value));
                    boolean success = bmRepo.save(bm);
                    saveMapping(value.getSource(), SourceValueType.MATCH, value.getSourceId(), bm.getId());

                    if (success) {
                        // send message
                        LeMessage message = LeMessageBuilder.create().setEntityId(bm.getId())
                                .setIdType(IdType.BOLE_MATCH).setActionType(ActionType.ADD)
                                .setSource(MessageSource.BOLE).build();
                        SwiftMessageApis.sendMsgAsync(message);
                    }
                    return bm.getId();
                }
            }
        }
        return mapping.getBoleId();
    }

    private void saveLives(final SourceMatch value, final Long cid, final Long matchId) {
        for (SourceLive sourceLive : value.getLives()) {
            BoleLive boleLive = blRepo.getByMatchIdAndUrl(matchId, sourceLive.getUrl());
            ActionType actionType = null;
            if (boleLive == null) {
                // Create
                boleLive = transLive(sourceLive, value.getSource(), cid, matchId);
                blRepo.save(boleLive);
                createConfig(boleLive);
                actionType = ActionType.ADD;
            } else {
                // Update
                String now = LeDateUtils.formatYYYYMMDDHHMMSS(new Date());
                boleLive.setSource(value.getSource());
                boleLive.setSite(sourceLive.getSite());
                boleLive.setName(sourceLive.getName());
                boleLive.setType(sourceLive.getType());
                boleLive.setUpdateAt(now);
                blRepo.save(boleLive);
                actionType = ActionType.UPDATE;
            }
            // send message
            LeMessage message = LeMessageBuilder.create().setEntityId(boleLive.getId())
                    .setIdType(IdType.BOLE_LIVE).setActionType(actionType)
                    .setSource(MessageSource.BOLE).build();
            SwiftMessageApis.sendMsgAsync(message);
        }
    }

    private void createConfig(BoleLive live) {
        OutputConfig config = configRepo.getConfig(live.getSite(), Content.MATCH);
        if (config == null) {
            config = new OutputConfig();
            config.setSite(live.getSite());
            config.setContent(Content.MATCH);
            try {
                configRepo.save(config);
            } catch (DuplicateKeyException e) {
                // ignore
            }
        }
    }

    private void saveMapping(Source source, SourceValueType type, String value, Long boleId) {
        try {
            String now = LeDateUtils.formatYYYYMMDDHHMMSS(new Date());
            SourceBoleMapping sbm = new SourceBoleMapping();
            sbm.setBoleId(boleId);
            sbm.setSource(source);
            sbm.setSourceValueType(type);
            sbm.setSourceValue(value);
            sbm.setCreateAt(now);
            sbm.setUpdateAt(now);
            LOGGER.info("{} {} from {} will mapping to : {}.", type, value, source, boleId);
            sbmRepo.save(sbm);
        } catch (DuplicateKeyException e) {
            // ignore
        }
    }

    private Long saveCompetition(SourceMatch match) {
        Source source = match.getSource();
        SourceBoleMapping mapping = getMapping(source, SourceValueType.COMPETITION, match.getCompetition());
        if (mapping == null) {
            synchronized (BCSYNCOBJ) {
                mapping = getMapping(source, SourceValueType.COMPETITION, match.getCompetition());
                if (mapping == null) {
                    String now = LeDateUtils.formatYYYYMMDDHHMMSS(new Date());
                    BoleCompetition bc = new BoleCompetition();
                    bc.setId(LeIdApis.nextId(IdType.BOLE_COMPETITION));
                    bc.setStatus(BoleStatus.CREATED);
                    bc.setVs(match.getVs());
                    bc.setSource(source);
                    bc.setSourceMatchId(match.getId());
                    bc.setName(match.getCompetition());
                    bc.setAbbreviation(match.getCompetition());
                    bc.setGameFType(match.getGameFType());
                    bc.setCreateAt(now);
                    bc.setUpdateAt(now);
                    LOGGER.info("competition {} will be created for source match {}.",
                            JSONObject.toJSONString(bc), JSONObject.toJSONString(match));
                    boolean success = bcRepo.save(bc);
                    saveMapping(match.getSource(), SourceValueType.COMPETITION, match.getCompetition(), bc.getId());

                    if (success) {
                        // send message
                        LeMessage message = LeMessageBuilder.create().setEntityId(bc.getId())
                                .setIdType(IdType.BOLE_COMPETITION).setActionType(ActionType.ADD)
                                .setSource(MessageSource.BOLE).build();
                        SwiftMessageApis.sendMsgAsync(message);
                    }
                    return bc.getId();
                }
            }
        }
        return mapping.getBoleId();
    }

    private Long saveCompetitor(SourceMatch sourceMatch, BoleCompetitorType type, String value) {
        Source source = sourceMatch.getSource();
        SourceBoleMapping mapping = getCompetitorMapping(source, SourceValueType.COMPETITOR, value, sourceMatch.getGameFName());
        if (mapping == null) {
            synchronized (BCRSYNCOBJ) {
                mapping = getCompetitorMapping(source, SourceValueType.COMPETITOR, value, sourceMatch.getGameFName());
                if (mapping == null) {
                    String now = LeDateUtils.formatYYYYMMDDHHMMSS(new Date());
                    BoleCompetitor bcr = new BoleCompetitor();
                    bcr.setId(LeIdApis.nextId(IdType.BOLE_COMPETITOR));
                    bcr.setStatus(BoleStatus.CREATED);
                    bcr.setSource(source);
                    bcr.setSourceMatchId(sourceMatch.getId());
                    bcr.setName(value);
                    bcr.setNickname(value);
                    bcr.setGameFType(sourceMatch.getGameFType());
                    bcr.setGameName(sourceMatch.getGameFName());
                    bcr.setType(type);
                    bcr.setCreateAt(now);
                    bcr.setUpdateAt(now);
                    LOGGER.info("competitor {} will be created for source match {}.",
                            JSONObject.toJSONString(bcr), JSONObject.toJSONString(sourceMatch));
                    boolean success = bcrRepo.save(bcr);
                    saveMapping(source, SourceValueType.COMPETITOR, value, bcr.getId());

                    if (success) {
                        // send message
                        LeMessage message = LeMessageBuilder.create().setEntityId(bcr.getId())
                                .setIdType(IdType.BOLE_COMPETITOR).setActionType(ActionType.ADD)
                                .setSource(MessageSource.BOLE).build();
                        SwiftMessageApis.sendMsgAsync(message);
                    }
                    return bcr.getId();
                }
            }
        }
        return mapping.getBoleId();
    }

    private boolean check(SourceMatch item) {
        return item != null && item.getSource() != null && !Strings.isNullOrEmpty(item.getSourceId());
    }

    private boolean fullcheck(SourceMatch item) {
        return !Strings.isNullOrEmpty(item.getStartTime())
                && !Strings.isNullOrEmpty(item.getCompetition())
                && !Strings.isNullOrEmpty(item.getSourceUrl());
    }

    private BoleLive transLive(SourceLive sourceLive, Source source, Long cid, Long matchId) {
        String now = LeDateUtils.formatYYYYMMDDHHMMSS(new Date());
        BoleLive bl = new BoleLive();
        bl.setId(LeIdApis.nextId(IdType.BOLE_LIVE));
        bl.setSource(source);
        bl.setDeleted(Boolean.FALSE);
        bl.setSite(sourceLive.getSite());
        bl.setName(sourceLive.getName());
        bl.setUrl(sourceLive.getUrl());
        bl.setType(sourceLive.getType());
        bl.setCid(cid);
        bl.setMatchId(matchId);
        bl.setCreateAt(now);
        bl.setUpdateAt(now);
        return bl;
    }
}
