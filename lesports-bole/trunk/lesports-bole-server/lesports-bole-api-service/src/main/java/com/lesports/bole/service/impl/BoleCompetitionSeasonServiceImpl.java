package com.lesports.bole.service.impl;

import com.lesports.bole.api.vo.TBCompetitionSeason;
import com.lesports.bole.model.BoleCompetition;
import com.lesports.bole.model.BoleCompetitionSeason;
import com.lesports.bole.model.BoleStatus;
import com.lesports.bole.repository.BoleCompetitionRepository;
import com.lesports.bole.repository.BoleCompetitionSeasonRepository;
import com.lesports.bole.service.BoleCompetitionSeasonService;
import com.lesports.bole.utils.PageUtils;
import com.lesports.id.api.IdType;
import com.lesports.id.client.LeIdApis;
import com.lesports.model.PageResult;
import com.lesports.sms.api.vo.TCompetitionSeason;
import com.lesports.utils.LeDateUtils;
import com.lesports.utils.math.LeNumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * trunk.
 *
 * @author pangchuanxiao
 * @since 2015/12/9
 */
@Service
public class BoleCompetitionSeasonServiceImpl implements BoleCompetitionSeasonService {
    private static final Logger LOG = LoggerFactory.getLogger(BoleCompetitionSeasonServiceImpl.class);

    @Resource
    private BoleCompetitionSeasonRepository boleCompetitionSeasonRepository;
    @Resource
    private BoleCompetitionRepository boleCompetitionRepository;

    @Override
    public boolean saveSms(long id) {
        BoleCompetitionSeason existing = boleCompetitionSeasonRepository.findOneBySmsId(id);
        if (null == existing) {
            existing = new BoleCompetitionSeason();
            existing.setId(LeIdApis.nextId(IdType.BOLE_COMPETITION_SEASON));
            existing.setCreateAt(LeDateUtils.formatYYYYMMDDHHMMSS(new Date()));
        }

//        TCompetitionSeason tCompetitionSeason = SmsApis.getCompetitionSeasonById(id);
//        existing.setName(tCompetitionSeason.getName());
//        existing.setSmsId(id);
//        existing.setUpdateAt(LeDateUtils.formatYYYYMMDDHHMMSS(new Date()));
//        existing.setSeason(tCompetitionSeason.getSeason());
//        existing.setStartTime(tCompetitionSeason.getStartTime());
//        BoleCompetition boleCompetition = boleCompetitionRepository.findOneBySmsId(tCompetitionSeason.getCid());
//        if (null != boleCompetition) {
//            existing.setCid(boleCompetition.getId());
//        }
//        existing.setStatus(BoleStatus.IMPORTED);
//        boolean result = boleCompetitionSeasonRepository.save(existing);
//        LOG.info("save bole competition {} from sms {}, result : {}", existing.getId(), id, result);
//        return result;
        return false;
    }

    @Override
    public TBCompetitionSeason getTBCompetitionSeasonBySmsId(long id) {
        BoleCompetitionSeason boleCompetitionSeason = boleCompetitionSeasonRepository.findOneBySmsId(id);

        return constructTBCompetitionSeason(boleCompetitionSeason);
    }

    @Override
    public TBCompetitionSeason getTBCompetitionSeasonById(long id) {
        BoleCompetitionSeason boleCompetitionSeason = boleCompetitionSeasonRepository.findOne(id);

        return constructTBCompetitionSeason(boleCompetitionSeason);
    }

    private TBCompetitionSeason constructTBCompetitionSeason(BoleCompetitionSeason boleCompetitionSeason) {
        if (null == boleCompetitionSeason) {
            return null;
        }
        TBCompetitionSeason tbCompetitionSeason = new TBCompetitionSeason();
        tbCompetitionSeason.setSmsId(LeNumberUtils.toLong(boleCompetitionSeason.getSmsId()));
        tbCompetitionSeason.setName(boleCompetitionSeason.getName());
        tbCompetitionSeason.setId(LeNumberUtils.toLong(boleCompetitionSeason.getId()));
        tbCompetitionSeason.setCompetitionId(LeNumberUtils.toLong(boleCompetitionSeason.getCid()));
        return tbCompetitionSeason;
    }

    @Override
    public PageResult<BoleCompetitionSeason> getCompetitionSeasons(Pageable pageable) {
        pageable = PageUtils.getValidPageable(pageable);
        Query query = new Query();
        long count = boleCompetitionRepository.countByQuery(query);
        query.with(pageable);
        List<BoleCompetitionSeason> seasons = boleCompetitionSeasonRepository.findByQuery(query);

        PageResult<BoleCompetitionSeason> result = new PageResult<>();
        result.setTotal(count);
        result.setRows(seasons);
        return result;
    }

    @Override
    public BoleCompetitionSeason getById(long id) {
        return boleCompetitionSeasonRepository.findOne(id);
    }
}
