package com.lesports.crawler.scheduler;

import com.lesports.bole.model.BoleMatch;
import com.lesports.bole.model.BoleStatus;
import com.lesports.bole.repository.BoleMatchRepository;
import com.lesports.crawler.utils.SpringUtils;
import com.lesports.utils.LeDateUtils;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * aa.
 *
 * @author pangchuanxiao
 * @since 2015/11/24
 */
public class BoleMatchAutoInvalidTask extends SchedulerTask {
    private static final Logger LOG = LoggerFactory.getLogger(BoleMatchAutoInvalidTask.class);
    private BoleMatchRepository boleMatchRepository = SpringUtils.getBean(BoleMatchRepository.class);

    @Override
    public void run() {
        List<BoleMatch> boleMatches = findExpiredMatch();

        expireMatches(boleMatches);
    }


    private List<BoleMatch> findExpiredMatch() {
        Date date = Calendar.getInstance().getTime();
        Query query = new Query(Criteria.where("start_time").lt(LeDateUtils.formatYYYYMMDDHHMMSS(date)));
        query.addCriteria(Criteria.where("status").is(BoleStatus.CREATED));
        return boleMatchRepository.findByQuery(query);
    }

    private void expireMatches(List<BoleMatch> boleMatches) {
        if (CollectionUtils.isEmpty(boleMatches)) {
            return;
        }
        for (BoleMatch boleMatch : boleMatches) {
            boleMatch.setStatus(BoleStatus.INVALID);
            boolean result = boleMatchRepository.save(boleMatch);
            LOG.info("set bole match : {} to invalid as start time expired, result : {}.", boleMatch.getId(), result);
        }
    }
}
