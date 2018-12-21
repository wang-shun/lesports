package com.lesports.bole.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.lesports.bole.api.vo.TBCompetitor;
import com.lesports.bole.api.vo.TBoleStatus;
import com.lesports.bole.api.vo.TCompetitorType;
import com.lesports.bole.model.BoleCompetitor;
import com.lesports.bole.model.BoleCompetitorType;
import com.lesports.bole.model.BoleStatus;
import com.lesports.bole.repository.BoleCompetitorRepository;
import com.lesports.bole.service.BoleCompetitorService;
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
import com.lesports.sms.api.vo.TDictEntry;
import com.lesports.sms.api.vo.TPlayer;
import com.lesports.sms.api.vo.TTeam;
import com.lesports.utils.LeDateUtils;
import com.lesports.utils.math.LeNumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.google.common.base.Preconditions.*;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

/**
 * trunk.
 *
 * @author pangchuanxiao
 * @since 2015/12/9
 */
@Service
public class BoleCompetitorServiceImpl implements BoleCompetitorService {
    private static final Logger LOG = LoggerFactory.getLogger(BoleCompetitorServiceImpl.class);

    @Resource
    private BoleCompetitorRepository boleCompetitorRepository;
    @Resource
    private SourceBoleMappingRepository sourceBoleMappingRepository;
    @Resource
    private SourceMatchRepository sourceMatchRepository;

    @Override
    public TBCompetitor getTBCompetitorBySmsId(long id) {
        BoleCompetitor boleCompetitor = boleCompetitorRepository.findOneBySmsId(id);
        return constructTBCompetitor(boleCompetitor);
    }

    @Override
    public TBCompetitor getTBCompetitorById(long id) {
        BoleCompetitor boleCompetitor = boleCompetitorRepository.findOne(id);
        return constructTBCompetitor(boleCompetitor);
    }

    @Override
    public boolean saveSms(long id) {
        BoleCompetitor existing = boleCompetitorRepository.findOneBySmsId(id);
        if (null == existing) {
            existing = new BoleCompetitor();
            existing.setId(LeIdApis.nextId(IdType.BOLE_COMPETITOR));
            existing.setCreateAt(LeDateUtils.formatYYYYMMDDHHMMSS(new Date()));
        }
        IdType idType = LeIdApis.checkIdTye(id);
        if (IdType.TEAM == idType) {
//            TTeam tTeam = SmsApis.getTeamById(id);
//            existing.setName(tTeam.getName());
//            existing.setNickname(tTeam.getNickname());
//            existing.setType(BoleCompetitorType.TEAM);
//            existing.setGameName(tTeam.getGameFirstType());
        } else if (IdType.PLAYER == idType) {
//            TPlayer tPlayer = SmsApis.getPlayerById(id);
//            existing.setName(tPlayer.getName());
//            existing.setNickname(tPlayer.getNickname());
//            existing.setType(BoleCompetitorType.PLAYER);
//            existing.setGameName(tPlayer.getGameFType());
        }
        existing.setSmsId(id);
        existing.setUpdateAt(LeDateUtils.formatYYYYMMDDHHMMSS(new Date()));
        existing.setStatus(BoleStatus.IMPORTED);
        boolean result = boleCompetitorRepository.save(existing);
        LOG.info("save bole competition {} from sms {}, result : {}", existing.getId(), id, result);
        return result;
    }

    public TBCompetitor constructTBCompetitor(BoleCompetitor boleCompetitor) {
        if (null == boleCompetitor) {
            return null;
        }
        TBCompetitor tbCompetitor = new TBCompetitor();
        tbCompetitor.setName(boleCompetitor.getName());
        tbCompetitor.setId(LeNumberUtils.toLong(boleCompetitor.getId()));
        tbCompetitor.setNickname(boleCompetitor.getNickname());
        tbCompetitor.setSmsId(LeNumberUtils.toLong(boleCompetitor.getSmsId()));
        if (null != boleCompetitor.getType()) {
            tbCompetitor.setType(TCompetitorType.valueOf(boleCompetitor.getType().name()));
        }
        if (null != boleCompetitor.getStatus()) {
            tbCompetitor.setStatus(TBoleStatus.valueOf(boleCompetitor.getStatus().name()));
        }
        tbCompetitor.setGameName(boleCompetitor.getGameName());
        return tbCompetitor;
    }

    @Override
    public PageResult<BoleCompetitor> getCompetitors(BoleCompetitorType type, Pageable pageable) {
        pageable = PageUtils.getValidPageable(pageable);
        Query query = new Query();
        long count = boleCompetitorRepository.countByQuery(query);
        query.with(pageable);
        List<BoleCompetitor> competitiors = boleCompetitorRepository.findByQuery(query);

        PageResult<BoleCompetitor> result = new PageResult<>();
        result.setTotal(count);
        result.setRows(competitiors);
        return result;
    }

    @Override
    public BoleCompetitor getById(long id) {
        return boleCompetitorRepository.findOne(id);
    }

    @Override
    public BoleCompetitor updateStatus(long id, BoleStatus status, BoleCompetitorType boleCompetitorType) {
        BoleCompetitor competitor = boleCompetitorRepository.findOne(id);
        checkNotNull(competitor, "id %s not exists", id);
        checkArgument(competitor.getStatus().isSafeUpdated(status), "invalid status");
        checkNotNull(boleCompetitorType, "competitor type is null id %s", id);

        competitor.setStatus(status);
        competitor.setType(boleCompetitorType);
        competitor.setUpdateAt(LeDateUtils.formatYYYYMMDDHHMMSS(new Date()));
        boleCompetitorRepository.save(competitor);
        if (BoleStatus.CONFIRMED == competitor.getStatus()) {
            long smsId = saveToSms(competitor);
            if (smsId > 0) {
                competitor.setSmsId(smsId);
                competitor.setStatus(BoleStatus.EXPORTED);
                competitor.setUpdateAt(LeDateUtils.formatYYYYMMDDHHMMSS(new Date()));
                boleCompetitorRepository.save(competitor);
                LOG.info("success to save  competitor {} in sms, sms id : {}.", competitor.getId(), smsId);
            } else {
                LOG.warn("fail to save {} in sms.", JSONObject.toJSONString(competitor));
            }
        }
        sendMessage(competitor);
        return competitor;
    }

    @Override
    public boolean attachSms(long id, long attachTo) {
        checkArgument(id > 0 && attachTo > 0, "id must be positive");

        BoleCompetitor boleC = boleCompetitorRepository.findOne(id);
        checkNotNull(boleC, "id %s not exists", id);
        checkArgument(BoleStatus.canAttach(boleC.getStatus()), "can not attach on status %s", boleC.getStatus());

        BoleCompetitor smsC = boleCompetitorRepository.findOne(attachTo);
        checkNotNull(smsC, "id %s not exists: ", attachTo);
        checkArgument(BoleStatus.canBeAttached(smsC.getStatus()), "can not attach to status %s", smsC.getStatus());

        SourceBoleMapping mapping = sourceBoleMappingRepository.get(boleC.getId());
        checkState(mapping != null, "mapping not found");

        String now = LeDateUtils.formatYYYYMMDDHHMMSS(new Date());
        mapping.setBoleId(smsC.getId());
        mapping.setUpdateAt(now);
        sourceBoleMappingRepository.save(mapping);
        boleC.setSmsId(smsC.getSmsId());
        boleC.setStatus(BoleStatus.ATTACHED);
        boleC.setAttachId(smsC.getId());
        boleC.setUpdateAt(now);
        boleCompetitorRepository.save(boleC);
        sendMessage(boleC);
        LOG.info("attach competitor {} to {}", id, attachTo);
        return true;
    }

    private void sendMessage(BoleCompetitor boleC) {
        // send message
        LeMessage message = LeMessageBuilder.create().setEntityId(boleC.getId())
                .setIdType(IdType.BOLE_COMPETITOR).setActionType(ActionType.UPDATE)
                .setSource(MessageSource.BOLE).build();
        SwiftMessageApis.sendMsgAsync(message);
    }

    @Override
    public boolean cancelAttachSms(long id) {
        checkArgument(id > 0, "id must be positive");

        BoleCompetitor boleC = boleCompetitorRepository.findOne(id);
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
        boleCompetitorRepository.save(boleC);
        mapping.setBoleId(boleC.getId());
        mapping.setUpdateAt(now);
        sourceBoleMappingRepository.save(mapping);
        sendMessage(boleC);
        return true;
    }

    private long saveToSms(BoleCompetitor boleCompetitor) {
        checkArgument(BoleStatus.canExport(boleCompetitor.getStatus()), "invalid status");
        checkNotNull(boleCompetitor.getType(), "competitor type of competitor %s is null.", boleCompetitor.getId());

//        if (BoleCompetitorType.TEAM == boleCompetitor.getType()) {
//            TTeam tTeam = new TTeam();
//            tTeam.setName(boleCompetitor.getName());
//            tTeam.setBoleId(boleCompetitor.getId());
//            tTeam.setNickname(boleCompetitor.getNickname());
//            if (null != boleCompetitor.getGameFType()) {
//                TDictEntry tDictEntry = SmsApis.getDictEntryById(boleCompetitor.getGameFType());
//                if (null != tDictEntry) {
//                    tTeam.setGameFirstType(tDictEntry.getName());
//                }
//            }
//            return SmsInternalApis.saveTeam(tTeam);
//        } else if (BoleCompetitorType.PLAYER == boleCompetitor.getType()) {
//            TPlayer tPlayer = new TPlayer();
//            tPlayer.setName(boleCompetitor.getName());
//            tPlayer.setBoleId(tPlayer.getId());
//            tPlayer.setNickname(boleCompetitor.getNickname());
//            if (null != boleCompetitor.getGameFType()) {
//                TDictEntry tDictEntry = SmsApis.getDictEntryById(boleCompetitor.getGameFType());
//                if (null != tDictEntry) {
//                    tPlayer.setGameFType(tDictEntry.getName());
//                }
//            }
//            return SmsInternalApis.savePlayer(tPlayer);
//        }
        return 0;
    }

    @Override
    public List<BoleCompetitor> getExportable(String name) {
        Query query = query(where("name").is(name));
        query.addCriteria(where("status").in(BoleStatus.CREATED.toString(), BoleStatus.CONFIRMED.toString()));
        return boleCompetitorRepository.findByQuery(query);
    }
    
    @Override
    public List<BoleCompetitor> export(List<Long> ids, String type) {
        BoleCompetitorType competitorType = BoleCompetitorType.valueOf(type);
        List<BoleCompetitor> competitors = new ArrayList<>();
        for (Long id : ids) {
            BoleCompetitor competitor = boleCompetitorRepository.findOne(id);
            checkArgument(competitor!=null, "id %d not exists", id);
            checkArgument(BoleStatus.canExport(competitor.getStatus()), "invalid status");
            competitor.setType(competitorType);
            competitors.add(competitor);
        }
        if (competitors.isEmpty()) {
            return competitors;
        }
        
        long smsId = saveToSms(competitors.get(0));
        if (smsId > 0) {
            for (BoleCompetitor competitor : competitors) {
                competitor.setSmsId(smsId);
                competitor.setStatus(BoleStatus.EXPORTED);
                competitor.setUpdateAt(LeDateUtils.formatYYYYMMDDHHMMSS(new Date()));
                boleCompetitorRepository.save(competitor);
                sendMessage(competitor);
                LOG.info("success to save competitor {} in sms, sms id : {}.", competitor.getId(), smsId);
            }
        } else {
            String msg = "fail to save competitor in sms: " + JSONObject.toJSONString(competitors.get(0));
            throw new RuntimeException(msg);
        }
        
        return competitors;
    }
}
