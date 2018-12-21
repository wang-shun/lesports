//package com.lesports.sms.data.processor.sportrard;
//
//import com.lesports.sms.api.common.MatchStatus;
//import com.lesports.sms.client.SbdsInternalApis;
//import com.lesports.sms.data.model.CommonSchedule;
//import com.lesports.sms.data.model.sportrard.F1MatchSchedule;
//import com.lesports.sms.data.model.sportrard.MatchSchedule;
//import com.lesports.sms.data.model.sportrard.SportrardConstants;
//import com.lesports.sms.data.processor.BeanProcessor;
//import com.lesports.sms.data.processor.CommonScheduleProcessor;
//import com.lesports.sms.data.processor.Processor;
//import com.lesports.sms.data.processor.ProcessorFactory;
//import com.lesports.sms.data.processor.olympic.AbstractProcessor;
//import com.lesports.sms.data.utils.CommonUtil;
//import com.lesports.sms.model.*;
//import com.lesports.utils.LeDateUtils;
//import org.apache.commons.collections.CollectionUtils;
//import org.apache.commons.lang.StringUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//
///**
// * Created by qiaohongxin
// */
//
//public class F1ScheduleProcessor extends AbstractProcessor implements BeanProcessor<F1MatchSchedule> {
//
//    private static Logger LOG = LoggerFactory.getLogger(F1ScheduleProcessor.class);
//
//    public Boolean process(String fileType, F1MatchSchedule matchSchedule) {
//        Boolean result = false;
//        try {
//
//            CompetitionSeason competitionSeason = SbdsInternalApis.getLatestCompetitionSeasonByCid(SportrardConstants.f1NameMap.get(matchSchedule.getTourmamentId()));
//            if (competitionSeason == null) {
//                LOG.warn("can not find relative competionSeason,tournamentId is:{}", matchSchedule.getTourmamentId());
//                return result;
//            }
//
//            Match match = SbdsInternalApis.getMatchByPartnerIdAndType(matchSchedule.getPartnerId(), getPartnerType());
//            if (match != null) return true;
//
//            match = new Match();
//            createMatch(match, matchSchedule, competitionSeason.getId());
//
//            return SbdsInternalApis.saveMatch(match) > 0;
//        } catch (Exception e) {
//            return false;
//        }
//    }
//
//    private void createMatch(Match currentMatch, F1MatchSchedule matchSchedule, Long csid) {
//        currentMatch.setAllowCountries(getAllowCountries());
//        currentMatch.setCid(SportrardConstants.f1NameMap.get(matchSchedule.getTourmamentId()));
//        currentMatch.setCsid(csid);
//        currentMatch.setPartnerId(matchSchedule.getPartnerId());
//        currentMatch.setPartnerType(getPartnerType());
//        List<DictEntry> dictEntries = SbdsInternalApis.getDictEntriesByName(getGameFTypeName());
//        currentMatch.setGameFType(dictEntries.get(0).getId());
//        currentMatch.setStartTime(matchSchedule.getStartTime());
//        currentMatch.setStartDate(matchSchedule.getStartTime().substring(0, 8));
//        currentMatch.setStage(SportrardConstants.stageMap.get(matchSchedule.getSubStageType()));
//        Long substationId = getparentStageId(matchSchedule.getStageId());
//        DictEntry dictEntrie1 = SbdsInternalApis.getDictById(substationId);
//        currentMatch.setSubstation(dictEntrie1.getId());
//        String substationName = dictEntrie1.getName();
//        String stageName = SbdsInternalApis.getDictById(SportrardConstants.stageMap.get(matchSchedule.getSubStageType())).getName();
//        String name = substationName + " " + stageName;
//        currentMatch.setName(stageName);
//        Integer num = null;
//        if (matchSchedule.getSubStageType().contains("Practice")) {
//            String[] split = matchSchedule.getSubStageType().split("\\s+");
//            if (split.length>1) num=CommonUtil.parseInt(split[1],0);
//        }
//        currentMatch.setNumber(num);
//        currentMatch.setOnlineLanguages(getOnlineLang());
//        currentMatch.setMultiLangNames(getMultiLang(name));
//    }
//
//    public Integer getPartnerType() {
//        return 469;
//    }
//
//    public String getGameFTypeName() {
//        return "^赛车";
//    }
//
//    private Long getparentStageId(String id) {
//        for (Map.Entry<String, Long> entry : SportrardConstants.substationMap.entrySet()) {
//            if (entry.getKey().contains(id)) return entry.getValue();
//        }
//        return 0L;
//    }
//}
//
//
//
//
