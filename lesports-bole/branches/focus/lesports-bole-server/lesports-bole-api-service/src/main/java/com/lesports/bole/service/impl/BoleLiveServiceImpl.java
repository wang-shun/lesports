package com.lesports.bole.service.impl;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.lesports.bole.model.BoleLive;
import com.lesports.bole.model.BoleLiveStatus;
import com.lesports.bole.repository.BoleLiveRepository;
import com.lesports.bole.service.BoleLiveService;
import com.lesports.id.api.IdType;
import com.lesports.msg.core.ActionType;
import com.lesports.msg.core.LeMessage;
import com.lesports.msg.core.LeMessageBuilder;
import com.lesports.msg.core.MessageSource;
import com.lesports.msg.producer.SwiftMessageApis;
import com.lesports.utils.LeDateUtils;

/**
 * BoleLiveServiceImpl
 * 
 * @author denghui
 *
 */
@Service
public class BoleLiveServiceImpl implements BoleLiveService {
    @Resource
    private BoleLiveRepository boleLiveRepository;

    @Override
    public List<BoleLive> getByMatchId(long matchId) {
        Query query = Query.query(Criteria.where("match_id").is(matchId));
        return boleLiveRepository.findByQuery(query);
    }

    @Override
    public BoleLive updateStatus(long id, BoleLiveStatus status) {
        BoleLive live = boleLiveRepository.findOne(id);
        checkArgument(live != null, "id %d not exists", id);
        live.setStatus(status);
        live.setUpdateAt(LeDateUtils.formatYYYYMMDDHHMMSS(new Date()));
        boleLiveRepository.save(live);
        sendMessage(id);
        return live;
    }

    private void sendMessage(long id) {
        // send message
        LeMessage message = LeMessageBuilder.create().setEntityId(id)
                .setIdType(IdType.BOLE_LIVE).setActionType(ActionType.UPDATE)
                .setSource(MessageSource.BOLE).build();
        SwiftMessageApis.sendMsgAsync(message);
    }
}
