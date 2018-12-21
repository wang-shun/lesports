package com.lesports.bole.service.impl;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.lesports.bole.api.vo.TBLive;
import com.lesports.bole.api.vo.TBMatch;
import com.lesports.bole.api.vo.TBoleLiveType;
import com.lesports.bole.api.vo.TBoleSource;
import com.lesports.bole.api.vo.TBoleStatus;
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
import com.lesports.bole.service.BoleMatchService;
import com.lesports.bole.utils.BoleLiveFilter;
import com.lesports.bole.utils.PageUtils;
import com.lesports.crawler.model.config.OutputConfig;
import com.lesports.crawler.model.source.SourceBoleMapping;
import com.lesports.crawler.model.source.SourceMatch;
import com.lesports.crawler.repository.OutputConfigRepository;
import com.lesports.crawler.repository.SourceBoleMappingRepository;
import com.lesports.crawler.repository.SourceMatchRepository;
import com.lesports.id.api.IdType;
import com.lesports.id.client.LeIdApis;
import com.lesports.model.PageResult;
import com.lesports.msg.core.ActionType;
import com.lesports.msg.core.LeMessage;
import com.lesports.msg.core.LeMessageBuilder;
import com.lesports.msg.core.MessageSource;
import com.lesports.msg.producer.SwiftMessageApis;
import com.lesports.sms.api.common.CompetitorType;
import com.lesports.sms.api.vo.TCompetitor;
import com.lesports.sms.api.vo.TMatch;
import com.lesports.utils.LeDateUtils;
import com.lesports.utils.math.LeNumberUtils;

/**
 * trunk.
 *
 * @author pangchuanxiao
 * @since 2015/12/9
 */
@Service
public class BoleMatchServiceImpl implements BoleMatchService {
    private static final Logger LOG = LoggerFactory.getLogger(BoleMatchServiceImpl.class);

    @Resource
    private BoleMatchRepository boleMatchRepository;

    @Resource
    private BoleCompetitionSeasonRepository boleCompetitionSeasonRepository;

    @Resource
    private BoleCompetitionRepository boleCompetitionRepository;

    @Resource
    private BoleCompetitorRepository boleCompetitorRepository;
    @Resource
    private SourceBoleMappingRepository sourceBoleMappingRepository;
    @Resource
    private SourceMatchRepository sourceMatchRepository;
    @Resource
    private BoleLiveRepository boleLiveRepository;
    @Resource
    private OutputConfigRepository outputConfigRepository;
    @Resource
    private BoleLiveFilter boleLiveFilter;

    @Override
    public boolean saveSms(long id) {
        BoleMatch existing = boleMatchRepository.findOneBySmsId(id);
        if (null == existing) {
            existing = new BoleMatch();
            existing.setId(LeIdApis.nextId(IdType.BOLE_MATCH));
            existing.setCreateAt(LeDateUtils.formatYYYYMMDDHHMMSS(new Date()));
        }

        existing.setSmsId(id);
        existing.setUpdateAt(LeDateUtils.formatYYYYMMDDHHMMSS(new Date()));
//        TMatch tMatch = SmsApis.getMatchById(id);
//        // 查找伯乐赛程
//        long cid = tMatch.getCid();
//        BoleCompetition boleCompetition = boleCompetitionRepository.findOneBySmsId(cid);
//        if (null != boleCompetition) {
//            existing.setCid(boleCompetition.getId());
//        }
//        // 查找伯乐赛季
//        long csid = tMatch.getCsid();
//        BoleCompetitionSeason boleCompetitionSeason = boleCompetitionSeasonRepository.findOneBySmsId(csid);
//        if (null != boleCompetitionSeason) {
//            existing.setCsid(boleCompetitionSeason.getId());
//        }
//        // 查找伯乐对阵双方
//        List<TCompetitor> tCompetitors = tMatch.getCompetitors();
//        if (CollectionUtils.isNotEmpty(tCompetitors)) {
//            List<Long> compes = new ArrayList<>(tCompetitors.size());
//            for (TCompetitor tCompetitor : tCompetitors) {
//                BoleCompetitor boleCompetitor = boleCompetitorRepository.findOneBySmsId(tCompetitor.getId());
//                if (null != boleCompetitor) {
//                    compes.add(boleCompetitor.getId());
//                }
//            }
//            existing.setCompetitors(compes);
//        }
//        existing.setStatus(BoleStatus.IMPORTED);
//        existing.setStartDate(StringUtils.substring(tMatch.getStartTime(), 0, 8));
//        existing.setStartTime(tMatch.getStartTime());
//        existing.setName(tMatch.getName());
//        boolean result = boleMatchRepository.save(existing);
//        LOG.info("save bole competition {} from sms {}, result : {}", existing.getId(), id, result);
//        return result;
        return false;
    }

    @Override
    public TBMatch getTBMatchBySmsId(long id) {
        BoleMatch boleMatch = boleMatchRepository.findOneBySmsId(id);
        return constructTBMatch(boleMatch);
    }

    @Override
    public TBMatch getTBMatchById(long id) {
        BoleMatch boleMatch = boleMatchRepository.findOne(id);
        return constructTBMatch(boleMatch);
    }

    private TBMatch constructTBMatch(BoleMatch boleMatch) {
        if (null == boleMatch) {
            return null;
        }
        TBMatch tbMatch = new TBMatch();
        tbMatch.setId(LeNumberUtils.toLong(boleMatch.getId()));
        tbMatch.setName(boleMatch.getName());
        tbMatch.setSmsId(LeNumberUtils.toLong(boleMatch.getSmsId()));
        tbMatch.setCid(LeNumberUtils.toLong(boleMatch.getCid()));
        tbMatch.setCompetitorIds(boleMatch.getCompetitors());
        tbMatch.setCsid(LeNumberUtils.toLong(boleMatch.getCsid()));
        tbMatch.setStartTime(boleMatch.getStartTime());
        if (null != boleMatch.getStatus()) {
            tbMatch.setStatus(TBoleStatus.valueOf(boleMatch.getStatus().name()));
        }
        return tbMatch;
    }

    @Override
    public PageResult<BoleMatch> getMatches(Pageable pageable) {
        pageable = PageUtils.getValidPageable(pageable);
        Query query = new Query();
        long count = boleMatchRepository.countByQuery(query);
        query.with(pageable);
        List<BoleMatch> matches = boleMatchRepository.findByQuery(query);

        PageResult<BoleMatch> result = new PageResult<>();
        result.setTotal(count);
        result.setRows(matches);
        return result;
    }

    @Override
    public BoleMatch getById(long id) {
        return boleMatchRepository.findOne(id);
    }

    @Override
    public BoleMatch updateStatus(long id, BoleStatus status) {
        BoleMatch match = boleMatchRepository.findOne(id);
        checkNotNull(match, "id %s not exists", id);
        checkArgument(match.getStatus().isSafeUpdated(status), "invalid status");

        match.setStatus(status);
        match.setUpdateAt(LeDateUtils.formatYYYYMMDDHHMMSS(new Date()));
        boleMatchRepository.save(match);
        sendMessage(match);

        if (BoleStatus.CONFIRMED == match.getStatus()) {
            long smsId = saveToSms(match);
            if (smsId > 0) {
                match.setSmsId(smsId);
                match.setStatus(BoleStatus.EXPORTED);
                match.setUpdateAt(LeDateUtils.formatYYYYMMDDHHMMSS(new Date()));
                boleMatchRepository.save(match);
                LOG.info("success to save  match {} in sms, sms id : {}.", match.getId(), smsId);
            } else {
                LOG.warn("fail to save {} in sms.", JSONObject.toJSONString(match));
            }
        }
        sendMessage(match);
        return match;
    }

    @Override
    public boolean attachSms(long id, long attachTo) {
        checkArgument(id > 0 && attachTo > 0, "id must be positive");

        BoleMatch boleM = boleMatchRepository.findOne(id);
        checkNotNull(boleM, "id %s not exists", id);
        checkArgument(BoleStatus.canAttach(boleM.getStatus()), "can not attach on status %s", boleM.getStatus());

        BoleMatch smsM = boleMatchRepository.findOne(attachTo);
        checkNotNull(smsM, "id %s not exists: ", attachTo);
        checkArgument(BoleStatus.canBeAttached(smsM.getStatus()), "can not attach to status %s", smsM.getStatus());

        SourceMatch sourceMatch = sourceMatchRepository.findOne(boleM.getSourceMatchId());
        checkState(sourceMatch != null, "source match %s not found", boleM.getSourceMatchId());

        SourceBoleMapping mapping = sourceBoleMappingRepository.get(boleM.getId());
        checkState(mapping != null, "mapping not found");

        String now = LeDateUtils.formatYYYYMMDDHHMMSS(new Date());
        mapping.setBoleId(smsM.getId());
        mapping.setUpdateAt(now);
        sourceBoleMappingRepository.save(mapping);
        boleM.setSmsId(smsM.getSmsId());
        boleM.setStatus(BoleStatus.ATTACHED);
        boleM.setAttachId(smsM.getId());
        boleM.setUpdateAt(now);
        boleMatchRepository.save(boleM);
        sendMessage(boleM);

        //
        TMatch tMatch = new TMatch();
        tMatch.setId(smsM.getSmsId());
        tMatch.setBoleId(smsM.getId());
        long smsId = 0;//SmsInternalApis.saveMatch(tMatch);

        sendMessage(boleM);
        LOG.info("attach match {} to {}, save sms result : {}.", id, attachTo, tMatch.getId() == smsId);
        return true;
    }

    private void sendMessage(BoleMatch boleM) {
        // send message
        LeMessage message = LeMessageBuilder.create().setEntityId(boleM.getId())
                .setIdType(IdType.BOLE_MATCH).setActionType(ActionType.UPDATE)
                .setSource(MessageSource.BOLE).build();
        SwiftMessageApis.sendMsgAsync(message);
    }

    @Override
    public List<TBLive> getTLivesBySmsMatchId(long smsId) {
        BoleMatch boleMatch = boleMatchRepository.findOneBySmsId(smsId);
        long matchId = boleMatch.getId();
        Query query = new Query(Criteria.where("match_id").is(matchId));
        List<BoleLive> boleLives = boleLiveRepository.findByQuery(query);
        if (CollectionUtils.isEmpty(boleLives)) {
            return Collections.emptyList();
        }
        List<OutputConfig> configs = outputConfigRepository.getAllConfigs();
        boleLives = boleLiveFilter.filter(boleMatch, boleLives, configs);
        List<TBLive> tbLives = new ArrayList<>(boleLives.size());
        for (BoleLive boleLive : boleLives) {
            TBLive tbLive = new TBLive();
            tbLive.setId(boleLive.getId());
            tbLive.setMatchId(boleLive.getMatchId());
            tbLive.setSite(boleLive.getSite());
            tbLive.setSource(TBoleSource.valueOf(boleLive.getSource().name()));
            tbLive.setType(TBoleLiveType.valueOf(boleLive.getType().name()));
            tbLive.setUrl(boleLive.getUrl());
            tbLives.add(tbLive);
        }

        return tbLives;
    }

    @Override
    public boolean cancelAttachSms(long id) {
        checkArgument(id > 0, "id must be positive");

        BoleMatch boleM = boleMatchRepository.findOne(id);
        checkNotNull(boleM, "id %s not exists", id);
        checkState(boleM.getStatus() == BoleStatus.ATTACHED, "id %s not attached yet", id);

        SourceMatch sourceMatch = sourceMatchRepository.findOne(boleM.getSourceMatchId());
        checkState(sourceMatch != null, "source match %s not found", boleM.getSourceMatchId());

        SourceBoleMapping mapping = sourceBoleMappingRepository.get(boleM.getId());
        checkState(mapping != null, "mapping not found");

        // 置为初始状态
        String now = LeDateUtils.formatYYYYMMDDHHMMSS(new Date());
        boleM.setStatus(BoleStatus.CREATED);
        boleM.setAttachId(Long.valueOf(0));
        boleM.setSmsId(Long.valueOf(0));
        boleM.setUpdateAt(now);
        boleMatchRepository.save(boleM);
        mapping.setBoleId(boleM.getId());
        mapping.setUpdateAt(now);
        sourceBoleMappingRepository.save(mapping);
        sendMessage(boleM);
        return true;
    }

    private long saveToSms(BoleMatch boleMatch) {
        checkArgument(BoleStatus.canExport(boleMatch.getStatus()), "invalid status");
        TMatch tMatch = new TMatch();
        tMatch.setName(boleMatch.getName());
        tMatch.setBoleId(boleMatch.getId());
        tMatch.setStartTime(boleMatch.getStartTime());
        BoleCompetition boleCompetition = boleCompetitionRepository.findOne(boleMatch.getCid());
        checkNotNull(boleCompetition, "bole competition %s is null of match %s", boleMatch.getCid(), boleMatch.getId());
        checkArgument(null != boleCompetition.getSmsId() && boleCompetition.getSmsId() > 0,
                "bole competition %s has not attach to sms of match %s", boleMatch.getCid(), boleMatch.getId());
        tMatch.setCid(boleCompetition.getSmsId());

        BoleCompetitionSeason boleCompetitionSeason = boleCompetitionSeasonRepository.findOne(boleMatch.getCsid());
        checkNotNull(boleCompetitionSeason, "bole competition season %s is null of match %s", boleMatch.getCsid(), boleMatch.getId());
        checkArgument(null != boleCompetitionSeason.getSmsId() && boleCompetitionSeason.getSmsId() > 0,
                "bole competition %s has not attach to sms of match %s", boleMatch.getCsid(), boleMatch.getId());
        tMatch.setCsid(boleCompetitionSeason.getSmsId());

        if (Boolean.TRUE.equals(boleCompetition.getVs())) {
            checkArgument(CollectionUtils.isNotEmpty(boleMatch.getCompetitors()), "competitors is empty of match : %s", boleMatch.getId());
            List<TCompetitor> tCompetitors = new ArrayList<>(2);
            for (Long competitorId : boleMatch.getCompetitors()) {
                BoleCompetitor competitor = boleCompetitorRepository.findOne(competitorId);
                checkArgument(null != competitor.getSmsId() && competitor.getSmsId() > 0,
                        "bole competitor %s has not attach to sms of match %s", competitorId, competitor.getId());
                TCompetitor tCompetitor = new TCompetitor();
                tCompetitor.setId(competitor.getSmsId());
                tCompetitor.setCompetitorType(BoleCompetitorType.TEAM == competitor.getType()
                        ? CompetitorType.TEAM : CompetitorType.PLAYER);
                tCompetitors.add(tCompetitor);
            }

            tMatch.setCompetitors(tCompetitors);
        }
        long smsId = 0;//SmsInternalApis.saveMatch(tMatch);
        LOG.info("save bole match to sms. {}, sms id : {}", JSONObject.toJSONString(tMatch), smsId);
        return smsId;
    }

    @Override
    public List<BoleMatch> getExportable(String startTime, long com1, long com2) {
        Query query = query(where("start_time").is(startTime));
        query.addCriteria(where("competitors").all(com1, com2));
        query.addCriteria(where("status").in(BoleStatus.CREATED.toString(), BoleStatus.CONFIRMED.toString()));
        return boleMatchRepository.findByQuery(query);
    }

    @Override
    public List<BoleMatch> export(List<Long> ids) {
        List<BoleMatch> matches = new ArrayList<>();
        for (Long id : ids) {
            BoleMatch match = boleMatchRepository.findOne(id);
            checkArgument(match != null, "id %d not exists", id);
            checkArgument(BoleStatus.canExport(match.getStatus()), "invalid status");
            matches.add(match);
        }
        if (matches.isEmpty()) {
            return matches;
        }

        long smsId = saveToSms(matches.get(0));
        if (smsId > 0) {
            for (BoleMatch match : matches) {
                match.setSmsId(smsId);
                match.setStatus(BoleStatus.EXPORTED);
                match.setUpdateAt(LeDateUtils.formatYYYYMMDDHHMMSS(new Date()));
                boleMatchRepository.save(match);
                sendMessage(match);
                LOG.info("success to save match {} in sms, sms id : {}.", match.getId(), smsId);
            }
        } else {
            String msg = "fail to save match in sms: " + JSONObject.toJSONString(matches.get(0));
            throw new RuntimeException(msg);
        }

        return matches;
    }
}
