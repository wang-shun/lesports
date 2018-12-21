package com.lesports.bole.service.impl;

import com.lesports.bole.api.vo.TBCompetitor;
import com.lesports.bole.index.BoleCompetitorIndex;
import com.lesports.bole.repository.BoleCompetitorRepository;
import com.lesports.bole.service.AbstractSearchService;
import com.lesports.bole.service.BoleCompetitorService;
import com.lesports.sms.client.BoleApis;
import com.lesports.utils.LeDateUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 * trunk.
 *
 * @author pangchuanxiao
 * @since 2016/4/11
 */
@Service
public class BoleCompetitorServiceImpl extends AbstractSearchService<BoleCompetitorIndex, Long> implements BoleCompetitorService {
    @Resource
    private BoleCompetitorRepository boleCompetitorRepository;
    @Override
    protected BoleCompetitorIndex getEntityFromRpc(Long aLong) {
        TBCompetitor tbCompetitor = BoleApis.getCompetitorById(aLong);
        if (null == tbCompetitor) {
            return null;
        }
        BoleCompetitorIndex boleCompetitorIndex = new BoleCompetitorIndex();
        boleCompetitorIndex.setId(aLong);
        boleCompetitorIndex.setName(tbCompetitor.getName());
        boleCompetitorIndex.setStatus(tbCompetitor.getStatus().getValue());
        boleCompetitorIndex.setUpdateAt(LeDateUtils.formatYYYYMMDDHHMMSS(new Date()));
        boleCompetitorIndex.setGameName(tbCompetitor.getGameName());
        if (null != tbCompetitor.getType()) {
            boleCompetitorIndex.setType(tbCompetitor.getType().getValue());
        }
        return boleCompetitorIndex;
    }

    @Override
    public boolean save(BoleCompetitorIndex entity) {
        boleCompetitorRepository.save(entity);
        return true;
    }

    @Override
    protected BoleCompetitorIndex doFindOne(Long aLong) {
        return boleCompetitorRepository.findOne(aLong);
    }
}
