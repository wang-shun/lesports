package com.lesports.bole.service.impl;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.lesports.bole.api.vo.TBCompetition;
import com.lesports.bole.api.vo.TBoleStatus;
import com.lesports.bole.model.BoleCompetition;
import com.lesports.bole.model.BoleStatus;
import com.lesports.bole.repository.BoleCompetitionRepository;
import com.lesports.bole.service.BoleCompetitionService;
import com.lesports.bole.utils.PageUtils;
import com.lesports.crawler.model.source.SourceBoleMapping;
import com.lesports.crawler.model.source.SourceMatch;
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
import com.lesports.sms.api.vo.TCompetition;
import com.lesports.sms.api.vo.TCompetitionSeason;
import com.lesports.utils.LeDateUtils;
import com.lesports.utils.math.LeNumberUtils;

/**
 * trunk.
 *
 * @author pangchuanxiao
 * @since 2015/12/9
 */
@Service
public class BoleCompetitionServiceImpl implements BoleCompetitionService {
    private static final Logger LOG = LoggerFactory.getLogger(BoleCompetitionServiceImpl.class);

    @Resource
    private BoleCompetitionRepository boleCompetitionRepository;
    @Resource
    private SourceBoleMappingRepository sourceBoleMappingRepository;
    @Resource
    private SourceMatchRepository sourceMatchRepository;

    @Override
    public TBCompetition getTBCompetitionBySmsId(long id) {
        BoleCompetition boleCompetition = boleCompetitionRepository.findOneBySmsId(id);
        return constructTBCompetition(boleCompetition);
    }

    @Override
    public TBCompetition getTBCompetitionById(long id) {
        BoleCompetition boleCompetition = boleCompetitionRepository.findOne(id);
        return constructTBCompetition(boleCompetition);
    }
    @Override
    public BoleCompetition getBCompetitionBySmsId(long id) {
        BoleCompetition boleCompetition = boleCompetitionRepository.findOneBySmsId(id);
        return boleCompetition;
    }

    @Override
    public boolean saveSms(long id) {
        BoleCompetition existing = boleCompetitionRepository.findOneBySmsId(id);
        if (null == existing) {
            existing = new BoleCompetition();
            existing.setId(LeIdApis.nextId(IdType.BOLE_COMPETITION));
            existing.setCreateAt(LeDateUtils.formatYYYYMMDDHHMMSS(new Date()));
        }
//        TCompetition tCompetition = SmsApis.getCompetitionById(id);
//        existing.setName(tCompetition.getName());
//        existing.setSmsId(id);
//        existing.setUpdateAt(LeDateUtils.formatYYYYMMDDHHMMSS(new Date()));
//        existing.setStatus(BoleStatus.IMPORTED);
//        existing.setAbbreviation(tCompetition.getAbbreviation());
//        boolean result = boleCompetitionRepository.save(existing);
//        LOG.info("save bole competition {} from sms {}, result : {}", existing.getId(), id, result);
//        return result;
        return false;
    }

    private TBCompetition constructTBCompetition(BoleCompetition boleCompetition) {
        if (null == boleCompetition) {
            return null;
        }
        TBCompetition tbCompetition = new TBCompetition();
        tbCompetition.setId(LeNumberUtils.toLong(boleCompetition.getId()));
        tbCompetition.setName(boleCompetition.getName());
        tbCompetition.setAbbreviation(boleCompetition.getAbbreviation());
        if (null != boleCompetition.getStatus()) {
            tbCompetition.setStatus(TBoleStatus.valueOf(boleCompetition.getStatus().name()));
        }
        return tbCompetition;
    }

    @Override
    public PageResult<BoleCompetition> getCompetitions(Pageable pageable) {
        pageable = PageUtils.getValidPageable(pageable);
        Query query = new Query();
        long count = boleCompetitionRepository.countByQuery(query);
        query.with(pageable);
        List<BoleCompetition> competitions = boleCompetitionRepository.findByQuery(query);

        PageResult<BoleCompetition> result = new PageResult<>();
        result.setTotal(count);
        result.setRows(competitions);
        return result;
    }

    @Override
    public BoleCompetition getById(long id) {
        return boleCompetitionRepository.findOne(id);
    }

    @Override
    public BoleCompetition updateStatus(long id, BoleStatus status) {
        BoleCompetition competition = boleCompetitionRepository.findOne(id);
        checkNotNull(competition, "id %s not exists", id);
        checkArgument(competition.getStatus().isSafeUpdated(status), "invalid status");

        competition.setStatus(status);
        competition.setUpdateAt(LeDateUtils.formatYYYYMMDDHHMMSS(new Date()));
        boleCompetitionRepository.save(competition);
        if (BoleStatus.CONFIRMED == competition.getStatus()) {
            long smsId = saveToSms(competition);
            if (smsId > 0) {
                competition.setSmsId(smsId);
                competition.setStatus(BoleStatus.EXPORTED);
                competition.setUpdateAt(LeDateUtils.formatYYYYMMDDHHMMSS(new Date()));
                boleCompetitionRepository.save(competition);
                LOG.info("success to save  competition {} in sms, sms id : {}.", competition.getId(), smsId);
            } else {
                LOG.warn("fail to save {} in sms.", JSONObject.toJSONString(competition));
            }
        }
        sendMessage(competition);
        return competition;
    }

    @Override
    public boolean attachSms(long id, long attachTo) {
        checkArgument(id > 0 && attachTo > 0, "id must be positive");

        BoleCompetition boleC = boleCompetitionRepository.findOne(id);
        checkNotNull(boleC, "id %s not exists", id);
        checkArgument(BoleStatus.canAttach(boleC.getStatus()), "can not attach on status %s", boleC.getStatus());

        BoleCompetition smsC = boleCompetitionRepository.findOne(attachTo);
        checkNotNull(smsC, "id %s not exists: ", attachTo);
        checkArgument(BoleStatus.canBeAttached(smsC.getStatus()), "can not attach to status %s", smsC.getStatus());

        SourceMatch sourceMatch = sourceMatchRepository.findOne(boleC.getSourceMatchId());
        checkState(sourceMatch != null, "source match %s not found", boleC.getSourceMatchId());

        SourceBoleMapping mapping = sourceBoleMappingRepository.get(boleC.getId());
        checkState(mapping != null, "mapping not found");

        // 修改mapping
        String now = LeDateUtils.formatYYYYMMDDHHMMSS(new Date());
        mapping.setBoleId(smsC.getId());
        mapping.setUpdateAt(now);
        sourceBoleMappingRepository.save(mapping);
        // 修改完置状态
        boleC.setSmsId(smsC.getSmsId());
        boleC.setStatus(BoleStatus.ATTACHED);
        boleC.setAttachId(smsC.getId());
        boleC.setUpdateAt(now);
        boleCompetitionRepository.save(boleC);
        sendMessage(boleC);
        LOG.info("attach competition {} to {}", id, attachTo);
        return true;
    }

    private void sendMessage(BoleCompetition boleC) {
        // send message
        LeMessage message = LeMessageBuilder.create().setEntityId(boleC.getId())
                .setIdType(IdType.BOLE_COMPETITION).setActionType(ActionType.UPDATE)
                .setSource(MessageSource.BOLE).build();
        SwiftMessageApis.sendMsgAsync(message);
    }

    @Override
    public boolean cancelAttachSms(long id) {
        checkArgument(id > 0, "id must be positive");
        
        BoleCompetition boleC = boleCompetitionRepository.findOne(id);
        checkNotNull(boleC, "id %s not exists", id);
        checkState(boleC.getStatus() == BoleStatus.ATTACHED, "id %s not attached yet", id);
        
        SourceMatch sourceMatch = sourceMatchRepository.findOne(boleC.getSourceMatchId());
        checkState(sourceMatch != null, "source match %s not found", boleC.getSourceMatchId());

        SourceBoleMapping mapping = sourceBoleMappingRepository.get(boleC.getId());
        checkState(mapping != null, "mapping not found");
        
        // 置为初始状态
        String now = LeDateUtils.formatYYYYMMDDHHMMSS(new Date());
        boleC.setStatus(BoleStatus.CREATED);
        boleC.setAttachId(Long.valueOf(0));
        boleC.setSmsId(Long.valueOf(0));
        boleC.setUpdateAt(now);
        boleCompetitionRepository.save(boleC);
        mapping.setBoleId(boleC.getId());
        mapping.setUpdateAt(now);
        sourceBoleMappingRepository.save(mapping);
        sendMessage(boleC);
        return true;
    }

    private long saveToSms(BoleCompetition boleCompetition) {
//        checkArgument(BoleStatus.canExport(boleCompetition.getStatus()), "invalid status");
//        TCompetition tCompetition = new TCompetition();
//        tCompetition.setName(boleCompetition.getName());
//        tCompetition.setBoleId(boleCompetition.getId());
//        tCompetition.setAbbreviation(boleCompetition.getAbbreviation());
//        long smsCompetitionId = SmsInternalApis.saveCompetition(tCompetition);
//        //更新赛季信息
//        if (smsCompetitionId > 0) {
//            List<TCompetitionSeason> competitionSeasons = SmsApis.getCompetitionSeasonsByCid(smsCompetitionId);
//            if (CollectionUtils.isNotEmpty(competitionSeasons)) {
//                TCompetitionSeason max = Collections.max(competitionSeasons, new Comparator<TCompetitionSeason>() {
//                    @Override
//                    public int compare(TCompetitionSeason o1, TCompetitionSeason o2) {
//                        if (o1.getId() > o2.getId()) {
//                            return 1;
//                        }
//                        return -1;
//                    }
//                });
//                LeMessageBuilder builder = LeMessageBuilder.create().setEntityId(max.getId())
//                        .setIdType(IdType.COMPETITION_SEASON).setSource(MessageSource.BOLE);
//                SwiftMessageApis.sendMsgAsync(builder.build());
//            }
//
//        }
//
//        return smsCompetitionId;
        return 0;
    }

    @Override
    public List<BoleCompetition> getExportable(String name) {
        Query query = query(where("name").is(name));
        query.addCriteria(where("status").in(BoleStatus.CREATED.toString(), BoleStatus.CONFIRMED.toString()));
        return boleCompetitionRepository.findByQuery(query);
    }

    @Override
    public List<BoleCompetition> export(List<Long> ids) {
        List<BoleCompetition> competitions = new ArrayList<>();
        for (Long id : ids) {
            BoleCompetition competition = boleCompetitionRepository.findOne(id);
            checkArgument(competition!=null, "id %d not exists", id);
            checkArgument(BoleStatus.canExport(competition.getStatus()), "invalid status");
            competitions.add(competition);
        }
        if (competitions.isEmpty()) {
            return competitions;
        }
        
        long smsId = saveToSms(competitions.get(0));
        if (smsId > 0) {
            for (BoleCompetition competition : competitions) {
                competition.setSmsId(smsId);
                competition.setStatus(BoleStatus.EXPORTED);
                competition.setUpdateAt(LeDateUtils.formatYYYYMMDDHHMMSS(new Date()));
                boleCompetitionRepository.save(competition);
                sendMessage(competition);
                LOG.info("success to save competition {} in sms, sms id : {}.", competition.getId(), smsId);
            }
        } else {
            String msg = "fail to save competition in sms: " + JSONObject.toJSONString(competitions.get(0));
            throw new RuntimeException(msg);
        }
        
        return competitions;
    }
}
