package com.lesports.msg.context;

import com.alibaba.fastjson.JSONObject;
import com.lesports.msg.core.ActionType;
import com.lesports.msg.core.LeMessage;
import com.lesports.msg.core.MessageConstants;
import com.lesports.msg.handler.*;
import com.lesports.sms.model.CompetitorSeasonStat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * User: ellios
 * Time: 15-6-28 : 下午9:38
 */
@Component("messageProcessContext")
public class MessageProcessContext {
    private static final Logger LOG = LoggerFactory.getLogger(MessageProcessContext.class);

//    private Executor MESSAGE_SMS_EXECUTOR = Executors.newFixedThreadPool(100, new ThreadFactoryBuilder().setNameFormat("message-sms-executor-%d").build());
//
//    private Executor MESSAGE_USER_EXECUTOR = Executors.newFixedThreadPool(50, new ThreadFactoryBuilder().setNameFormat("message-user-executor-%d").build());

    @Resource
    private SmsRedisCacheHandler smsRedisCacheHandler;
    @Resource
    private SmsWebCbaseCacheHandler smsWebCbaseCacheHandler;
    @Resource
    private EpisodeMessageHandler episodeMessageHandler;

    @Resource
    private MatchMessageHandler matchMessageHandler;

    @Resource
    private LiveMessageHandler liveMessageHandler;

    @Resource
    private AlbumMessageHandler albumMessageHandler;

    @Resource
    private NewsMessageHandler newsMessageHandler;

    @Resource
    private VideoMessageHandler videoMessageHandler;

    @Resource
    private ActionMessageHandler actionMessageHandler;

    @Resource
    private TeamMessageHandler teamMessageHandler;

    @Resource
    private TLiveBusinessMessageHandler tLiveBusinessMessageHandler;

    @Resource
    private TextLiveMessageHandler textLiveMessageHandler;

    @Resource
    private TextLiveHandler textLiveHandler;

    @Resource
    private PlayerMessageHandler playerMessageHandler;

    @Resource
    private CompetitionSeasonHandler competitionSeasonHandler;

    @Resource
    private CompetitionMessageHandler competitionMessageHandler;

    @Resource
    private BoleMatchMessageHandler boleMatchMessageHandler;

    @Resource
    private BoleCompetitorMessageHandler boleCompetitorMessageHandler;

    @Resource
    private BoleCompetitionMessageHandler boleCompetitionMessageHandler;

    @Resource
    private BoleNewsMessageHandler boleNewsMessageHandler;

    @Resource
    private SmsAlbumMessageHandler smsAlbumMessageHandler;

    @Resource
    private TeamSeasonMessageHandler teamSeasonMessageHandler;
    @Resource
    private CompetitorSeasonStatHandler competitorSeasonStatHandler;

    @Resource
    private ScopeTopListHandler scopeTopListHandler;

    public void process(final LeMessage message) {
        if (null != message.getActionType() && ActionType.DELETE_CACHE == message.getActionType()) {
            String cacheType = message.getContent().getMsgBody().get(MessageConstants.CACHE_TYPE);
            if (MessageConstants.REDIS.equals(cacheType)) {
                smsRedisCacheHandler.handle(message);
                return;
            } else if (MessageConstants.CBASE.equals(cacheType)) {
                smsWebCbaseCacheHandler.handle(message);
                return;
            }
            return;
        }
        if (null == message.getIdType()) {
            smsRedisCacheHandler.handle(message);
            return;
        }

        switch (message.getIdType()) {
            case ACTION:
                processUserMessage(message);
                break;
            case TLIVE:
                processTLiveBusinessMessage(message);
                break;
            case USER_SUBSCRIBE:
                processUserSubscribeMessage(message);
                break;
            case BOLE_MATCH:
            case BOLE_COMPETITION:
            case BOLE_COMPETITION_SEASON:
            case BOLE_COMPETITOR:
            case BOLE_NEWS:
            case BOLE_LIVE:
                processBoleMessage(message);
                break;
            default:
                processSmsMessage(message);
                break;
        }
    }

    /**
     * 处理用户消息
     *
     * @param message
     */
    private void processUserSubscribeMessage(final LeMessage message) {
        try {
            LOG.info("handle message {}", JSONObject.toJSONString(message));
            actionMessageHandler.handle(message);
        } catch (Exception e) {
            LOG.error("{}", e.getMessage(), e);
        }
    }

    /**
     * 处理用户消息
     *
     * @param message
     */
    private void processUserMessage(final LeMessage message) {
        try {
            LOG.info("handle message {}", JSONObject.toJSONString(message));
            actionMessageHandler.handle(message);
        } catch (Exception e) {
            LOG.error("{}", e.getMessage(), e);
        }
    }

    /**
     * 处理图文直播分页消息
     *
     * @param message
     */
    private void processTLiveBusinessMessage(final LeMessage message) {
        try {
            LOG.info("handle message {}", JSONObject.toJSONString(message));
            tLiveBusinessMessageHandler.handle(message);
        } catch (Exception e) {
            LOG.error("{}", e.getMessage(), e);
        }
    }

    /**
     * 处理sms消息
     *
     * @param message
     */
    private void processSmsMessage(final LeMessage message) {
        LOG.info("handle message {}", JSONObject.toJSONString(message));
        try {
            switch (message.getIdType()) {
                case EPISODE:
                    episodeMessageHandler.handle(message);
                    break;
                case TEAM:
                    teamMessageHandler.handle(message);
                    break;
                case MATCH:
                    matchMessageHandler.handle(message);
                    break;
                case LETV_LIVE:
                    liveMessageHandler.handle(message);
                    break;
                case MMS_ALBUM:
                    albumMessageHandler.handle(message);
                    break;
                case MMS_VIDEO:
                    videoMessageHandler.handle(message);
                    break;
                case NEWS:
                    newsMessageHandler.handle(message);
                    break;
                case TEXT_LIVE:
                    textLiveHandler.handle(message);
                    break;
                case LIVE_MESSAGE:
                    textLiveMessageHandler.handle(message);
                    break;
                case PLAYER:
                    playerMessageHandler.handle(message);
                    break;
                case COMPETITION_SEASON:
                    competitionSeasonHandler.handle(message);
                    break;
                case COMPETITION:
                    competitionMessageHandler.handle(message);
                    break;
                case TEAM_SEASON:
                    teamSeasonMessageHandler.handle(message);
                    break;
                case ALBUM:
                    smsAlbumMessageHandler.handle(message);
                    break;
                case COMPETITOR_SEASON_STAT:
                    competitorSeasonStatHandler.handle(message);
                    break;
                case TOP_LIST:
                    scopeTopListHandler.handle(message);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            LOG.error("{}", e.getMessage(), e);
        }
    }

    /**
     * 处理bole消息
     *
     * @param message
     */
    private void processBoleMessage(final LeMessage message) {
        LOG.info("handle message {}", JSONObject.toJSONString(message));
        try {
            switch (message.getIdType()) {
                case BOLE_MATCH:
                    boleMatchMessageHandler.handle(message);
                    break;
                case BOLE_COMPETITOR:
                    boleCompetitorMessageHandler.handle(message);
                    break;
                case BOLE_COMPETITION:
                    boleCompetitionMessageHandler.handle(message);
                    break;
                case BOLE_NEWS:
                    boleNewsMessageHandler.handle(message);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            LOG.error("{}", e.getMessage(), e);
        }
    }
}
