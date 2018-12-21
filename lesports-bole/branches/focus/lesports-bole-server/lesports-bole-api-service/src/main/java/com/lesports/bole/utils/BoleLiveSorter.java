package com.lesports.bole.utils;

import com.lesports.bole.model.BoleLive;
import com.lesports.crawler.model.config.OutputConfig;

import java.util.List;

/**
 * trunk.
 *
 * @author pangchuanxiao
 * @since 2016/1/6
 */
public interface BoleLiveSorter {

    public List<BoleLive> sort(List<BoleLive> boleLives, List<OutputConfig> configs);
}
