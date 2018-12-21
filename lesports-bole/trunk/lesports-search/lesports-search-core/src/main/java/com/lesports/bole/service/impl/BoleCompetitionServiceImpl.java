package com.lesports.bole.service.impl;

import com.lesports.bole.api.vo.TBCompetition;
import com.lesports.bole.index.BoleCompetitionIndex;
import com.lesports.bole.repository.BoleCompetitionRepository;
import com.lesports.bole.service.AbstractSearchService;
import com.lesports.bole.service.BoleCompetitionService;
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
public class BoleCompetitionServiceImpl extends AbstractSearchService<BoleCompetitionIndex, Long> implements BoleCompetitionService {
    @Resource
    private BoleCompetitionRepository boleCompetitionRepository;

    @Override
    protected BoleCompetitionIndex getEntityFromRpc(Long idLong) {
        TBCompetition tbCompetition = BoleApis.getCompetitionById(idLong);
        if (null == tbCompetition) {
            return null;
        }
        BoleCompetitionIndex boleCompetitionIndex = new BoleCompetitionIndex();
        boleCompetitionIndex.setId(idLong);
        boleCompetitionIndex.setName(tbCompetition.getName());
        boleCompetitionIndex.setStatus(tbCompetition.getStatus().getValue());
        boleCompetitionIndex.setUpdateAt(LeDateUtils.formatYYYYMMDDHHMMSS(new Date()));
        return boleCompetitionIndex;
    }

    @Override
    public boolean save(BoleCompetitionIndex entity) {
        boleCompetitionRepository.save(entity);
        return true;
    }

    @Override
    protected BoleCompetitionIndex doFindOne(Long aLong) {
        return boleCompetitionRepository.findOne(aLong);
    }
}
