package com.lesports.sms.data.processor.olympic;

import com.lesports.sms.api.common.CompetitorType;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.data.Constants;
import com.lesports.sms.data.model.olympic.RecordDetail;
import com.lesports.sms.data.processor.BeanProcessor;
import com.lesports.sms.model.CompetitionSeason;
import com.lesports.sms.model.DictEntry;
import com.lesports.sms.model.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * lesports-projects
 *
 * @author qiaohongxin
 * @since 16-3-16
 */
@Component
public class RecordProcessor extends AbstractProcessor implements BeanProcessor<RecordDetail> {
    private static Logger LOG = LoggerFactory.getLogger(RecordProcessor.class);

    @Override
    public Boolean process(String fileType, RecordDetail obj) {
        if (obj == null || obj.getRecordType() == null || obj.getRecordCode() == null || obj.getDatas() == null || inValidType(obj.getRecordType()))
            return true;
        try {
            CompetitionSeason competitionSeason = SbdsInternalApis.getLatestCompetitionSeasonByCid(Constants.OG_CID);
            if (null == competitionSeason) {
                LOG.warn("can not find relative event, uniqueTournamentId : {}, name : {}, season : {}.", Constants.OG_CID, "2016");
                return false;
            }
            DictEntry dictEntry = getCommonDictWithCache(Constants.DICT_NAME_EVENT, obj.getRecordCode().substring(0, 6) + "000"+"$");
            if (null == dictEntry){
                LOG.warn("the current gameStype is null,id{}",obj.getRecordCode());
                return false;
            }
            Record recordSet = SbdsInternalApis.getRecordByPartnerIdAndType(obj.getRecordCode(), Constants.partner_type);
            if (recordSet == null) {
                recordSet = new Record();
            }
            List<Record.RecordData> recordDataList = changeRecordToTRecord(obj.getDatas());
            recordSet.setGameSType(dictEntry.getId());
            recordSet.setCid(Constants.OG_CID);
            recordSet.setCsid(competitionSeason.getId());
            recordSet.setPartnerId(obj.getRecordCode());
            recordSet.setPartnerType(Constants.partner_type);
            if (obj.getRecordType().contains("o")) {
                recordSet.setOlympicRecords(recordDataList);
            } else {
                recordSet.setWordRecords(recordDataList);
            }
            if (SbdsInternalApis.saveRecord(recordSet) > 0) {
                LOG.warn("code is saved sucess, code : {}", obj.getRecordCode());
                return true;
            }
            else{
                LOG.warn("code is saved fail, code : {}", obj.getRecordCode());
                return false;
            }
        } catch (Exception e) {
            LOG.warn("code is saved fail, beacause exception : {}", obj.getRecordCode(), e);
            return false;
        }
    }

    private List<Record.RecordData> changeRecordToTRecord(List<RecordDetail.RecordData> list) {
        if (list.isEmpty()) return null;
        List<Record.RecordData> tdataList = new ArrayList<>();
        for (RecordDetail.RecordData data : list) {
            Record.RecordData tRecordData = new Record.RecordData();
            tRecordData.setCompetitorType(data.getCompetitorType().equals("A") ? CompetitorType.PLAYER : CompetitorType.TEAM);
            if (getCompetitorId(data.getCompetitorCode(), data.getCompetitorType()) <= 0L) {
                LOG.debug("competitor is not exist,code:{}", data.getCompetitorCode());
                continue;
            }
            tRecordData.setCompetitorId(getCompetitorId(data.getCompetitorCode(), data.getCompetitorType()));
            tRecordData.setCity(data.getGenCity());
            DictEntry country = getCommonDictWithCache(Constants.DICT_NAME_COUNTRY, data.getGenConntry());
            tRecordData.setCountryId(country == null ? 0L : country.getId());
            tRecordData.setGenDate(data.getGenDate());
            tRecordData.setResult(data.getResult());
            tRecordData.setResultType(data.getResultType());
            tRecordData.setIsCurrentRecord(data.getIsCurrent()!=null&&data.getIsCurrent().equals("Y") ? true : false);
            tdataList.add(tRecordData);
        }
        return tdataList;
    }

    private boolean inValidType(String recordType) {
        if (recordType.equalsIgnoreCase("WR") || recordType.equalsIgnoreCase("WB") || recordType.equalsIgnoreCase("OR") || recordType.equalsIgnoreCase("0B")) {
            return false;
        } else return true;
    }
}

