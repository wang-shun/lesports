package com.lesports.bole.utils;

import com.lesports.bole.model.BoleLive;
import com.lesports.crawler.model.config.OutputConfig;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * trunk.
 *
 * @author pangchuanxiao
 * @since 2016/1/6
 */
@Component
public class BoleDefaultLiveSorter implements BoleLiveSorter {
    @Override
    public List<BoleLive> sort(List<BoleLive> boleLives, List<OutputConfig> configs) {
        if (CollectionUtils.isEmpty(boleLives) || CollectionUtils.isEmpty(configs)) {
            return boleLives;
        }
        final Map<String, OutputConfig> configMap = new HashMap<>();
        for (OutputConfig outputConfig : configs) {
            configMap.put(outputConfig.getSite(), outputConfig);
        }
        Collections.sort(boleLives, new Comparator<BoleLive>() {
            @Override
            public int compare(BoleLive o1, BoleLive o2) {
                OutputConfig outputConfig1 = configMap.get(o1.getSite());
                OutputConfig outputConfig2 = configMap.get(o2.getSite());
                int priority1 = getPriority(outputConfig1);
                int priority2 = getPriority(outputConfig2);
                return priority1 - priority2;
            }
        });
        return boleLives;
    }

    private int getPriority(OutputConfig outputConfig) {
        if (null == outputConfig) {
            return Integer.MAX_VALUE;
        }
        if (null == outputConfig.getPriority()) {
            return Integer.MAX_VALUE;
        }
        return outputConfig.getPriority();
    }

}
