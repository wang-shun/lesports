package com.lesports.sms.data.processor.olympic;

import com.google.common.collect.Sets;
import com.hankcs.hanlp.corpus.util.StringUtils;
import com.lesports.sms.api.common.Gender;
import com.lesports.sms.api.common.PlayerType;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.data.Constants;
import com.lesports.sms.data.model.olympic.Participant;
import com.lesports.sms.data.processor.BeanProcessor;
import com.lesports.sms.model.CompetitionSeason;
import com.lesports.sms.model.DictEntry;
import com.lesports.sms.model.Player;
import com.lesports.utils.math.LeNumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Created by qiaohongxin on 2016/2/22.
 */
@Component
public class ParticipantProcessor extends AbstractProcessor implements BeanProcessor<Participant> {

    private static Logger LOG = LoggerFactory.getLogger(ParticipantProcessor.class);

    @Override
    public Boolean process(String fileType, Participant obj) {
        try {
            if (obj == null || obj.getCode() == null || obj.getCompetitionCode() == null || obj.getSportType() == null)
                return false;
            CompetitionSeason competitionSeason = SbdsInternalApis.getLatestCompetitionSeasonByCid(Constants.OG_CID);
            if (competitionSeason == null) {
                LOG.warn(" the right season competition id : {} is not exits.");
                return false;
            }
            DictEntry discipline = getCommonDictWithCache(Constants.DICT_NAME_DISCIPLINE, obj.getSportType().substring(0, 2) + "$");
            if (discipline == null) {
                LOG.warn("will not handle this, can not get game first type {},  code : {}.", obj.getSportType(), obj.getCode());
                return false;
            }
            DictEntry gameFtype = getCommonDictWithCache(Constants.DICT_NAME_SPORT, discipline.getCode().substring(0, 2) + "$");
            if (gameFtype == null) {
                LOG.warn("will not handle this, game first type not exists. {}", obj.getCode());
                return false;
            }
            Player currentPlayer = SbdsInternalApis.getPlayerByPartnerIdAndPartnerType(obj.getCode(), Constants.partner_typeS);
            if (null != currentPlayer) {
                LOG.info("player is already saved and playerId is : {}.", currentPlayer.getId());
                return true;
            } else {
                currentPlayer = new Player();
                currentPlayer.setGameFType(gameFtype.getId());
                Set<Long> cids = Sets.newHashSet();
                cids.add(Constants.OG_CID);
                currentPlayer.setCids(cids);
                currentPlayer.setPartnerId(obj.getCode());
                currentPlayer.setPartnerType(Constants.partner_typeS);
                currentPlayer.setName(obj.getFamilyName());
                currentPlayer.setCountryId(getCommonDictWithCache(Constants.DICT_NAME_COUNTRY, obj.getOrganisation()).getId());
                currentPlayer.setAllowCountries(getAllowCountries());
                if (!StringUtils.isBlankOrNull(obj.getFunctionType())) {
                    if (obj.getFunctionType().equalsIgnoreCase("AA01")) {
                        currentPlayer.setType(PlayerType.PLAYER);
                    } else if (obj.getFunctionType().equals("COACH")) {
                        currentPlayer.setType(PlayerType.COACH.COACH);
                    }
                }
                currentPlayer.setOnlineLanguages(getOnlineLang());
                currentPlayer.setGender(obj.getGender().equals("M") ? Gender.MALE : Gender.FEMALE);
                currentPlayer.setMultiLangNames(getMultiLang(obj.getFamilyName()));
                currentPlayer.setWeight(LeNumberUtils.toInt(obj.getWight()));
                currentPlayer.setBirthdate(obj.getBirthDate());
                currentPlayer.setHeight(LeNumberUtils.toInt(obj.getHeight()));
                return SbdsInternalApis.savePlayer(currentPlayer) > 0;
            }
        } catch (Exception e) {
            LOG.warn("player id : {} is parsed fail.", obj.getCode(), e);
            return false;
        }

    }
}

