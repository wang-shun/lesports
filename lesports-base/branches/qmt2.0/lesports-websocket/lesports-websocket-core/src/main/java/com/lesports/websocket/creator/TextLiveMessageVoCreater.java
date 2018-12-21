package com.lesports.websocket.creator;


import com.alibaba.fastjson.JSON;
import com.lesports.model.tlive.ImageContext;
import com.lesports.model.tlive.VoteContext;
import com.lesports.sms.api.vo.TSimpleAnchor;
import com.lesports.sms.api.vo.TTextLiveMessage;
import com.lesports.websocket.domain.TextLiveMessageVo;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by lufei1 on 2015/9/20.
 */
public class TextLiveMessageVoCreater {

    private static final Logger LOG = LoggerFactory.getLogger(TextLiveMessageVoCreater.class);

    public static TextLiveMessageVo createLiveMessageVo(TTextLiveMessage tLiveMessage) {
        TextLiveMessageVo liveMessageVo = new TextLiveMessageVo();
        liveMessageVo.setId(tLiveMessage.getId());
        TSimpleAnchor tSimpleAnchor = tLiveMessage.getAnchor();
        if (tSimpleAnchor != null) {
            TextLiveMessageVo.SimpleAnchor simpleAnchor = liveMessageVo.new SimpleAnchor();
            simpleAnchor.setName(tSimpleAnchor.getName());
            simpleAnchor.setLogo(tSimpleAnchor.getLogo());
            liveMessageVo.setAnchor(simpleAnchor);
        }
        liveMessageVo.setMatchResult(tLiveMessage.getMatchResult());
        liveMessageVo.setSectionId(tLiveMessage.getSectionId());
        liveMessageVo.setSection(tLiveMessage.getSection());
        liveMessageVo.setSendTime(tLiveMessage.getSendTime());
        liveMessageVo.setTextLiveId(tLiveMessage.getTextLiveId());
        liveMessageVo.setTextLiveType(tLiveMessage.getTextLiveType());
        liveMessageVo.setTime(tLiveMessage.getTime());
        liveMessageVo.setDeleted(tLiveMessage.isDeleted());
        buildContent(tLiveMessage, liveMessageVo);
        return liveMessageVo;
    }

    private static void buildContent(TTextLiveMessage tLiveMessage, TextLiveMessageVo liveMessageVo) {
        if (tLiveMessage.getType() == null) {
            return;
        }
        liveMessageVo.setType(tLiveMessage.getType());
        switch (tLiveMessage.getType()) {
            case TEXT:
                liveMessageVo.setContent(tLiveMessage.getContent());
                break;
            case IMAGE:
                liveMessageVo.setContent(JSON.parseObject(tLiveMessage.getContent(), ImageContext.class));
                break;
            case VOTE:
                VoteContext voteContext = JSON.parseObject(tLiveMessage.getContent(), VoteContext.class);
                List<VoteContext.Option> options = voteContext.getOptions();
                if (CollectionUtils.isNotEmpty(options)) {
                    try {
                        Collections.sort(options, new Comparator<VoteContext.Option>() {
                            @Override
                            public int compare(VoteContext.Option o1, VoteContext.Option o2) {
                                return o1.getOrder() - o2.getOrder();
                            }
                        });
                    } catch (Exception e) {
                        LOG.error("Sorting vote option error! voteId = {}", voteContext.getVoteId(), e);
                    }
                }
                liveMessageVo.setContent(voteContext);
                break;
            default:
                break;
        }

    }

}
