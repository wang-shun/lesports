package com.lesports.bole.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.lesports.bole.api.ElasticApis;
import com.lesports.bole.api.vo.TBNews;
import com.lesports.bole.api.vo.TBParagraph;
import com.lesports.bole.index.BoleNewsIndex;
import com.lesports.bole.index.SearchIndex;
import com.lesports.bole.repository.BoleNewsRepository;
import com.lesports.bole.service.AbstractSearchService;
import com.lesports.bole.service.BoleNewsService;
import com.lesports.bole.utils.PageResult;
import com.lesports.sms.client.BoleApis;
import com.lesports.utils.LeDateUtils;
import com.lesports.utils.math.LeNumberUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Map;

/**
 * trunk.
 *
 * @author pangchuanxiao
 * @since 2016/1/11
 */
@Service
public class BoleNewsServiceImpl extends AbstractSearchService<BoleNewsIndex, Long> implements BoleNewsService {
    private static final String INDEX_NAME_BOLE_NEWS = "bole_news";
    private static final String TYPE_NAME_BOLE_NEWS = "image_text";
    private static final String TOP_PAGE = "{\"query\":{\"function_score\":{\"functions\":[{\"script_score\":{\"lang\":\"groovy\",\"script_file\":\"score_comment_publishtime\"}}]}}}";
    private static final String MATCH_PAGE = "{\"query\":{\"function_score\":{\"functions\":[{\"script_score\":{\"lang\":\"groovy\",\"script_file\":\"score_comment_publishtime\"}}], \"query\":{\"multi_match\":{\"query\":\"?2\",\"fields\":[\"tags^5\",\"title^4\",\"content^3\"]}}}}}";
    private static final String MATCH_PHRASE_PAGE = "{\"query\":{\"function_score\":{\"functions\":[{\"script_score\":{\"lang\":\"groovy\",\"script_file\":\"score_comment_publishtime\"}}], \"query\":{\"multi_match\":{\"type\":\"phrase\",\"query\":\"?2\",\"fields\":[\"tags^5\",\"title^4\",\"content^3\"]}}}}}";
    @Resource
    private BoleNewsRepository boleNewsRepository;

    @Override
    protected BoleNewsIndex getEntityFromRpc(Long id) {
        TBNews news = BoleApis.getNewsById(id);
        if (null == news) {
            return null;
        }
        BoleNewsIndex boleNewsIndex = new BoleNewsIndex();
        boleNewsIndex.setTitle(news.getTitle());
        if (CollectionUtils.isNotEmpty(news.getParagraphs())) {
            StringBuilder stringBuilder = new StringBuilder();
            for (TBParagraph para : news.getParagraphs()) {
                stringBuilder.append(para.getContent());
            }
            boleNewsIndex.setContent(stringBuilder.toString());
        }
        boleNewsIndex.setPublishTime(LeNumberUtils.toLong(news.getPublishAt()));
        boleNewsIndex.setCommentCount(news.getCommentCount());
        boleNewsIndex.setId(id);
        boleNewsIndex.setUpdateAt(LeDateUtils.formatYYYYMMDDHHMMSS(new Date()));
        boleNewsIndex.setTags(Lists.newArrayList(news.getTags()));
        return boleNewsIndex;
    }

    @Override
    public boolean save(BoleNewsIndex entity) {
        boleNewsRepository.save(entity);
        return true;
    }

    @Override
    protected BoleNewsIndex doFindOne(Long aLong) {
        return boleNewsRepository.findOne(aLong);
    }

    /**
     * 根据发布时间和评论数计算新闻列表页数据
     * {
     * "query": {
     * "function_score": {
     * "functions": [
     * {
     * "script_score": {
     * "lang": "groovy",
     * "script_file": "score_comment_publishtime"
     * }
     * }
     * ]
     * }
     * },
     * "from": 0,
     * "size": 20,
     * "_source": false
     * }
     *
     * @param page
     * @param count
     * @return
     */
    @Override
    public PageResult<SearchIndex> list(int page, int count, boolean source) {
        String queryValue = TOP_PAGE.replace("?0", String.valueOf(page)).replace("?1", String.valueOf(count));
        Map<String, Object> param = JSONObject.parseObject(queryValue, Map.class);
        return ElasticApis.search(INDEX_NAME_BOLE_NEWS, TYPE_NAME_BOLE_NEWS, param, page, count, source);
    }

    @Override
    public PageResult<SearchIndex> matchPhrase(String word, int page, int count, boolean source) {
        String queryValue = MATCH_PHRASE_PAGE.replace("?0", String.valueOf(page)).replace("?1", String.valueOf(count)).replace("?2", word);
        Map<String, Object> param = JSONObject.parseObject(queryValue, Map.class);
        return ElasticApis.search(INDEX_NAME_BOLE_NEWS, TYPE_NAME_BOLE_NEWS, param, page, count, source);
    }

    @Override
    public PageResult<SearchIndex> match(String word, int page, int count, boolean source) {
        String queryValue = MATCH_PAGE.replace("?0", String.valueOf(page)).replace("?1", String.valueOf(count)).replace("?2", word);
        Map<String, Object> param = JSONObject.parseObject(queryValue, Map.class);
        return ElasticApis.search(INDEX_NAME_BOLE_NEWS, TYPE_NAME_BOLE_NEWS, param, page, count, source);
    }
}
