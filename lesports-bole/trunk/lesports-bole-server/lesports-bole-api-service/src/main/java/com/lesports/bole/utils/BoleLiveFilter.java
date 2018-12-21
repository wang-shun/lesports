package com.lesports.bole.utils;

import com.lesports.bole.model.BoleLive;
import com.lesports.bole.model.BoleLiveStatus;
import com.lesports.bole.model.BoleMatch;
import com.lesports.crawler.model.config.OutputConfig;
import com.lesports.crawler.model.config.OutputOption;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * trunk.
 *
 * @author pangchuanxiao
 * @since 2015/12/30
 */
@Component
public class BoleLiveFilter {

    @Resource
    private BoleLiveDuplicateFilter boleLiveDuplicateFilter;
    @Resource
    private BoleDefaultLiveSorter boleDefaultLiveSorter;
    @Resource
    private BoleCompetitionLiveSorter boleCompetitionLiveSorter;

    /**
     * 过滤直播跳转链接
     * @param tbMatch
     * @param tbLives
     * @param configs
     * @return
     */
    public List<BoleLive> filter(BoleMatch tbMatch, List<BoleLive> tbLives, List<OutputConfig> configs) {
        if (CollectionUtils.isEmpty(tbLives)) {
            return Collections.emptyList();
        }
        List<BoleLive> boleLives = new ArrayList<>();
        Iterator<BoleLive> iterator = boleLiveDuplicateFilter.filter(tbLives).iterator();
        while (iterator.hasNext()) {
            BoleLive boleLive = iterator.next();
            if (valid(tbMatch, boleLive, configs)) {
                boleLives.add(boleLive);
            }
        }
        //根据默认数据来源排序
        boleLives = boleDefaultLiveSorter.sort(boleLives, configs);
        //根据某一赛事上的数据来源排序
        boleLives = boleCompetitionLiveSorter.sort(boleLives, configs);
        return boleLives;
    }

    public boolean valid(BoleMatch tbMatch, BoleLive tbLive, List<OutputConfig> configs) {
        if (CollectionUtils.isEmpty(configs)) {
            return true;
        }
        if (tbLive.getStatus() != BoleLiveStatus.ONLINE) {
            return false;
        }
        for (OutputConfig crawlerConfig : configs) {
            if (!valid(tbMatch, tbLive, crawlerConfig)) {
                return false;
            }
        }
        return true;
    }

    public boolean valid(BoleMatch tbMatch, BoleLive tbLive, OutputConfig config) {
        if (!config.getSite().equalsIgnoreCase(tbLive.getSite())) {
            return true;
        }
        if (config.getOutputOption() == OutputOption.ALL) {
            return true;
        }
        if (config.getOutputOption() == OutputOption.NONE) {
            return false;
        }
        if (config.getOutputOption() == OutputOption.WHITELIST) {
            if (CollectionUtils.isEmpty(config.getCids())) {
                return false;
            }
            return config.getCids().contains(tbMatch.getCid());
        }
        if (config.getOutputOption() == OutputOption.BLACKLIST) {
            if (CollectionUtils.isEmpty(config.getCids())) {
                return true;
            }
            return !config.getCids().contains(tbMatch.getCid());
        }
        return true;
    }
}
