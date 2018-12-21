package com.lesports.websocket.config;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import com.lesports.sms.api.vo.TTextLive;
import com.lesports.sms.api.vo.TTextLiveMessage;
import com.lesports.sms.client.TextLiveApis;
import com.lesports.websocket.creator.TextLiveMessageVoCreater;
import com.lesports.websocket.domain.TextLiveIndexVo;
import com.lesports.websocket.domain.TextLiveMessageVo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;


/**
 * Created by zhangdeqiang on 2016/9/12.
 */
@Component
public class TextLiveHandler {
    private static final Logger LOG = LoggerFactory.getLogger(TextLiveHandler.class);
    private static final int PAGE_COUNT = 20;
    private static final LoadingCache<TextLiveParam, Integer> LATEST_INDEX_CACHE = CacheBuilder.newBuilder()
            .maximumSize(500)
            .expireAfterWrite(1, TimeUnit.SECONDS)
            .build(new CacheLoader<TextLiveParam, Integer>() {
                @Override
                public Integer load(TextLiveParam key) throws Exception {
                    long textLiveId = key.tliveId;
                    long section = key.section;
                    return TextLiveApis.getLiveMessageLatestIndex(textLiveId, section);
                }
            });

    private static final LoadingCache<TextLiveParam, List<TextLiveMessageVo>> GET_BY_PAGE_CACHE = CacheBuilder.newBuilder()
            .maximumSize(500)
            .expireAfterWrite(2, TimeUnit.SECONDS)
            .build(new CacheLoader<TextLiveParam, List<TextLiveMessageVo>>() {
                @Override
                public List<TextLiveMessageVo> load(TextLiveParam key) throws Exception {
                    long textLiveId = key.tliveId;
                    long section = key.section;
                    int page = key.page;
                    List<TTextLiveMessage> tTextLiveMessages = TextLiveApis.getLiveMessageByPage(textLiveId, section, page);
                    if (CollectionUtils.isEmpty(tTextLiveMessages)) {
                        return Collections.emptyList();
                    }
                    List<TextLiveMessageVo> textLiveMessageVos = new ArrayList<>(tTextLiveMessages.size());
                    for (TTextLiveMessage tTextLiveMessage : tTextLiveMessages) {
                        TextLiveMessageVo textLiveMessageVo = TextLiveMessageVoCreater.createLiveMessageVo(tTextLiveMessage);
                        if (null != textLiveMessageVo) {
                            textLiveMessageVos.add(textLiveMessageVo);
                        }
                    }
                    return textLiveMessageVos;
                }
            });

    /**
     * 根据图文直播id和分节获取最新的索引
     *
     * @param textLiveId
     * @param section
     * @return
     */
    public TextLiveIndexVo getLatestIndex(long textLiveId, long section) {
        try {
            int index = LATEST_INDEX_CACHE.get(new TextLiveParam(textLiveId, section, 0));
            return new TextLiveIndexVo(index);
        } catch (ExecutionException e) {
            LOG.error("{}", e.getMessage(), e);
        }
        return null;
    }

    /**
     * 根据图文直播消息id获取一条消息
     *
     * @return
     */
    public TextLiveMessageVo getMessageById(long liveMessageId) {
        List<TTextLiveMessage> tTextLiveMessages = TextLiveApis.getLiveMessageByIds(Lists.newArrayList(liveMessageId));
        if (CollectionUtils.isNotEmpty(tTextLiveMessages)) {
            return TextLiveMessageVoCreater.createLiveMessageVo(tTextLiveMessages.get(0));
        }
        return null;
    }

    /**
     * 根据图文直播id和分节获取最新的一条消息
     *
     * @param textLiveId
     * @param section
     * @return
     */
    public TextLiveMessageVo getLatestMessage(long textLiveId, long section) {
        try {
            int index = LATEST_INDEX_CACHE.get(new TextLiveParam(textLiveId, section, 0));
            TextLiveParam textLiveParam = new TextLiveParam(textLiveId, section, getPage(index));
            List<TextLiveMessageVo> tTextLiveMessages = GET_BY_PAGE_CACHE.get(textLiveParam);
            if (CollectionUtils.isEmpty(tTextLiveMessages)) {
                return null;
            }
            return tTextLiveMessages.get(0);
        } catch (ExecutionException e) {
            LOG.error("{}", e.getMessage(), e);
        }
        return null;
    }

    /**
     * 根据图文直播id和分节获取最新一页消息
     *
     * @param textLiveId
     * @param section
     * @return
     */
    public List<TextLiveMessageVo> getLatestPage(long textLiveId, long section) {
        try {
            int index = LATEST_INDEX_CACHE.get(new TextLiveParam(textLiveId, section, 0));
            TextLiveParam textLiveParam = new TextLiveParam(textLiveId, section, getPage(index));
            return GET_BY_PAGE_CACHE.get(textLiveParam);
        } catch (ExecutionException e) {
            LOG.error("{}", e.getMessage(), e);
        }
        return Collections.emptyList();
    }

    /**
     * 根据分页获取图文直播数据
     *
     * @param textLiveId
     * @param section
     * @param page
     * @return
     */
    public List<TextLiveMessageVo> getPage(long textLiveId, long section, int page) {
        try {
            return GET_BY_PAGE_CACHE.get(new TextLiveParam(textLiveId, section, page));
        } catch (ExecutionException e) {
            LOG.error("{}", e.getMessage(), e);
        }
        return Collections.emptyList();
    }

    private class TextLiveParam {
        private long tliveId;
        private long section;
        private int page;

        public TextLiveParam(long tliveId, long section, int page) {
            this.tliveId = tliveId;
            this.section = section;
            this.page = page;
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder().append(tliveId).append(section).append(page).build();
        }

        @Override
        public boolean equals(Object obj) {
            if (null == obj) return false;
            if (!(obj instanceof TextLiveParam)) return false;
            TextLiveParam textLiveParam = (TextLiveParam) obj;
            return new EqualsBuilder().append(tliveId, textLiveParam.tliveId)
                    .append(section, textLiveParam.section)
                    .append(page, textLiveParam.page).build();
        }
    }
    private int getPage(int index) {
        if (index <= 0) {
            return 0;
        }
        return ((index - 1) / PAGE_COUNT) + 1;
    }
}
