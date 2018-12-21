package com.lesports.sms.data.processor.olympic;

import com.lesports.sms.api.common.TextLiveMessageType;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.client.TextLiveInternalApis;
import com.lesports.sms.data.Constants;
import com.lesports.sms.data.model.olympic.PlayByPlay;
import com.lesports.sms.data.processor.BeanProcessor;
import com.lesports.sms.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * lesports-projects
 *
 * @author pangchuanxiao
 * @since 16-2-18
 */
@Component
public class PBPProcessor extends AbstractProcessor implements BeanProcessor<PlayByPlay> {
    private static Logger LOG = LoggerFactory.getLogger(PBPProcessor.class);

    @Override
    public Boolean process(String fileType, PlayByPlay obj) {
        boolean result = false;
        try {
            if (obj == null || obj.getMatchCode() == null) return result;
            Match currentMatch = SbdsInternalApis.getMatchByPartnerIdAndType(obj.getMatchCode(), Constants.partner_type);
            if (currentMatch == null) {
                LOG.warn("the current match : {} is not exist", obj.getMatchCode());
                return result;
            }
            TextLive currentTextLive = TextLiveInternalApis.getPBPTTextLiveByEid(currentMatch.getId() + 2);
            if (currentTextLive == null) {
                LOG.warn("text live is null, the eid is : {}", currentMatch.getId() + 2);
                return result;
            }
            if (null != TextLiveInternalApis.getLiveMessagesByLiveIdAndPartnerId(currentTextLive.getId(), obj.getEventNumber())) {
                LOG.warn("the message is already exist in dbs : the eid is : {}", currentMatch.getId() + 2);
                return result;
            }
            TextLiveMessage currentMessage = new TextLiveMessage();
            DictEntry sectionEntry = getCommonDictWithCache(Constants.DICT_NAME_PERIOD, obj.getPeriodCode() + "_" + obj.getMatchCode().substring(0, 2));
            if (sectionEntry != null) {
                currentMessage.setSection(sectionEntry.getId());
            }
            currentMessage.setTextLiveId(currentTextLive.getId());
            currentMessage.setTime(obj.getMatchTime());
            currentMessage.setType(TextLiveMessageType.TEXT);
            currentMessage.setPartnerId(String.valueOf(obj.getEventNumber()));
            currentMessage.setPartnerType(Constants.partner_type);
            currentMessage.setContent(getContent(obj));
            return TextLiveInternalApis.saveTextLiveMessage(currentMessage) > 0;
        } catch (Exception e) {
            LOG.warn("the current match : {} and play by play event : {} is process failed", obj.getMatchCode(), obj.getEventNumber());
            return result;
        }
    }

    private String getContent(PlayByPlay obj) {
        DictEntry eventEntry = getCommonDictWithCache(Constants.DICT_NAME_EVENT_TYPE, obj.getEventTypeCode() + "_" + obj.getMatchCode().substring(0, 2));
        if (eventEntry == null) return "";
        StringBuffer event = new StringBuffer(eventEntry.getName());
        DictEntry eventResultEntry = getCommonDictWithCache(Constants.DICT_NAME_EVENT_TYPE, obj.getEventResultCode() + "_" + obj.getMatchCode().substring(0, 2));
        if (eventResultEntry != null) {
            event.append(eventResultEntry.getName());
        }
        if (obj.getCompetitorType() == null) return event.toString();

        if (obj.getCompetitorType().equalsIgnoreCase("A")) {
            Player player = SbdsInternalApis.getPlayerByPartnerIdAndPartnerType(obj.getCompetitorCode(), Constants.partner_type);
            if (player != null) {
                StringBuffer playName = new StringBuffer(player.getName()).append(event);
                return playName.toString();
            }
        }
        Team currentCompetitor = SbdsInternalApis.getTeamByPartnerIdAndPartnerType(obj.getCompetitorCode(), Constants.partner_type);
        if (currentCompetitor != null) {
            StringBuffer teamName = new StringBuffer(currentCompetitor.getName());
            if (obj.getAthletes() == null && !obj.getAthletes().isEmpty()) {
                return teamName.append(event + "").toString();
            }

            for (PlayByPlay.EventAthlete athlete : obj.getAthletes()) {
                Player eventPlayer = SbdsInternalApis.getPlayerByPartnerIdAndPartnerType(athlete.getPlayerCode(), Constants.partner_type);
                if (eventPlayer == null) continue;
                teamName.append(eventPlayer.getName() + "");
            }
            return teamName.append(event).toString();
        }
        return "";

    }
}
