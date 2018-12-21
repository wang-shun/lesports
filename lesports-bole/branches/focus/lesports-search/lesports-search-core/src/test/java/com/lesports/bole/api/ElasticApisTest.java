package com.lesports.bole.api;

import com.google.common.collect.Maps;
import com.lesports.bole.index.EpisodeIndex;
import com.lesports.bole.index.SearchIndex;
import com.lesports.bole.utils.PageResult;
import junit.framework.TestCase;
import org.junit.Test;
import org.springframework.util.Assert;

import java.util.Map;

/**
 * aa.
 *
 * @author pangchuanxiao
 * @since 2015/10/28
 */
public class ElasticApisTest extends TestCase {

    @Test
    public void testSearch() throws Exception {
        Map<String, Object> param = Maps.newHashMap();
        Map<String, Object> query = Maps.newHashMap();
        Map<String, Object> match = Maps.newHashMap();
        match.put("name", "汽车");
        query.put("match", match);
        param.put("query", query);

        PageResult<SearchIndex> ids = ElasticApis.search("sms", "matches", param, 0, 10, true);
        Assert.notEmpty(ids.getRows());
    }

    @Test
    public void testIndex() throws Exception {
        EpisodeIndex episodeIndex = new EpisodeIndex();
        episodeIndex.setId(6l);
        episodeIndex.setName("霍华德");
        episodeIndex.setAid(999l);
        boolean result = ElasticApis.index("sms", "episodes", episodeIndex);
        Assert.isTrue(result);
    }
}