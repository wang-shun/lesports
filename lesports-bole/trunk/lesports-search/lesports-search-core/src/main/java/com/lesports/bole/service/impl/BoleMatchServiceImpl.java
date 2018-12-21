package com.lesports.bole.service.impl;

import com.lesports.bole.api.vo.TBCompetition;
import com.lesports.bole.api.vo.TBCompetitionSeason;
import com.lesports.bole.api.vo.TBCompetitor;
import com.lesports.bole.api.vo.TBMatch;
import com.lesports.bole.index.BoleMatchIndex;
import com.lesports.bole.repository.BoleMatchRepository;
import com.lesports.bole.service.AbstractSearchService;
import com.lesports.bole.service.BoleMatchService;
import com.lesports.sms.client.BoleApis;
import com.lesports.utils.LeDateUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * trunk.
 *
 * @author pangchuanxiao
 * @since 2016/4/11
 */
@Service
public class BoleMatchServiceImpl extends AbstractSearchService<BoleMatchIndex, Long> implements BoleMatchService {
    @Resource
    private BoleMatchRepository boleMatchRepository;

    @Override
    protected BoleMatchIndex getEntityFromRpc(Long id) {
        TBMatch match = BoleApis.getMatchById(id);
        if (null == match) {
            return null;
        }
        BoleMatchIndex boleMatchIndex = new BoleMatchIndex();
        boleMatchIndex.setStartTime(match.getStartTime());
        boleMatchIndex.setCompetitionId(match.getCid());
        boleMatchIndex.setCompetitionSeasonId(match.getCsid());
        boleMatchIndex.setCompetitorIds(match.getCompetitorIds());
        boleMatchIndex.setName(match.getName());
        boleMatchIndex.setSmsId(match.getSmsId());
        boleMatchIndex.setStatus(match.getStatus().getValue());
        boleMatchIndex.setUpdateAt(LeDateUtils.formatYYYYMMDDHHMMSS(new Date()));
        boleMatchIndex.setId(match.getId());
        if (match.getCid() > 0) {
            TBCompetition tbCompetition = BoleApis.getCompetitionById(match.getCid());
            if (null != tbCompetition) {
                boleMatchIndex.setCompetitionName(tbCompetition.getName());
            }
        }
        if (match.getCsid() > 0) {
            TBCompetitionSeason tbCompetitionSeason = BoleApis.getCompetitionSeasonById(match.getCsid());
            if (null != tbCompetitionSeason) {
                boleMatchIndex.setCompetitionSeasonName(tbCompetitionSeason.getName());
            }
        }
        if (CollectionUtils.isNotEmpty(match.getCompetitorIds())) {
            List<String> competitorNames = new ArrayList<>(2);
            for (Long competitorId : match.getCompetitorIds()) {
                TBCompetitor tbCompetitor = BoleApis.getCompetitorById(competitorId);
                if (null != tbCompetitor) {
                    competitorNames.add(tbCompetitor.getName());
                }
            }
            boleMatchIndex.setCompetitorNames(competitorNames);
        }
        return boleMatchIndex;
    }

    @Override
    public boolean save(BoleMatchIndex entity) {
        boleMatchRepository.save(entity);
        return true;
    }

    @Override
    protected BoleMatchIndex doFindOne(Long aLong) {
        return boleMatchRepository.findOne(aLong);
    }
}
