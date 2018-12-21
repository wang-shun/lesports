package com.lesports.bole.utils;

import com.lesports.bole.model.BoleCompetition;
import com.lesports.bole.model.BoleLive;
import com.lesports.bole.model.BoleMatch;
import com.lesports.bole.repository.BoleCompetitionRepository;
import com.lesports.bole.repository.BoleMatchRepository;
import com.lesports.crawler.model.config.OutputConfig;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * trunk.
 *
 * @author pangchuanxiao
 * @since 2016/1/6
 */
@Component
public class BoleCompetitionLiveSorter implements BoleLiveSorter {
    @Resource
    private BoleCompetitionRepository boleCompetitionRepository;
    @Resource
    private BoleMatchRepository boleMatchRepository;

    @Override
    public List<BoleLive> sort(List<BoleLive> boleLives, List<OutputConfig> configs) {
        if (CollectionUtils.isEmpty(boleLives) || CollectionUtils.isEmpty(configs)) {
            return boleLives;
        }

        Collections.sort(boleLives, new Comparator<BoleLive>() {
            @Override
            public int compare(BoleLive o1, BoleLive o2) {
                int priority1 = getPriority(o1);
                int priority2 = getPriority(o2);
                return priority1 - priority2;
            }
        });
        return boleLives;
    }

    private int getPriority(BoleLive boleLive) {
        BoleMatch boleMatch = boleMatchRepository.findOne(boleLive.getMatchId());
        if (null == boleMatch) {
            return Integer.MAX_VALUE;
        }
        BoleCompetition boleCompetition = boleCompetitionRepository.findOne(boleMatch.getCid());
        if (null == boleCompetition) {
            return Integer.MAX_VALUE;
        }
        List<String> siteOrder = boleCompetition.getSiteOrder();
        if (CollectionUtils.isEmpty(siteOrder)) {
            return Integer.MAX_VALUE;
        }
        for (int index = 0; index < siteOrder.size(); index++) {
            if (siteOrder.get(index).equals(boleLive.getSite())) {
                return index;
            }
        }
        return Integer.MAX_VALUE;
    }

}
