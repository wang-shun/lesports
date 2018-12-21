package com.lesports.sms.data.parser;

import com.google.common.collect.Lists;
import com.lesports.api.common.MatchStatus;
import com.lesports.qmt.sbd.SbdMatchInternalApis;
import com.lesports.qmt.sbd.model.Match;
import com.lesports.qmt.sbd.model.Partner;
import com.lesports.qmt.sbd.model.PartnerType;
import com.lesports.query.InternalCriteria;
import com.lesports.query.InternalQuery;
import com.lesports.sms.data.model.TransModel;
import com.lesports.utils.LeDateUtils;
import org.apache.commons.collections.CollectionUtils;

import javax.sql.rowset.spi.TransactionalWriter;
import java.util.Date;
import java.util.List;

/**
 * Created by qiaohongxin on 2017/2/27.
 */
public class RegexFileUrlStrategy implements FileUrlStrategy {
    private String fileNameRegex;
    private Long csid;
    private PartnerType partnerType;
    private String annotationType;

    public RegexFileUrlStrategy(String annotationType, PartnerType partnerType, Long csid, String fileNameRegex) {
        this.annotationType = annotationType;
        this.partnerType = partnerType;
        this.csid = csid;
        this.fileNameRegex = fileNameRegex;
    }

    public List<TransModel> getFilesUrl() {
        List<TransModel> lists = getValidUrls(this.csid, this.partnerType, this.fileNameRegex);
        return lists;
    }

    private List<TransModel> getValidUrls(Long csid, PartnerType partnerType, String fileRegex) {
        List<String> finalUrls = Lists.newArrayList();
        List<String> validCodes = Lists.newArrayList();
        String regexPatten = "";
        if (fileRegex.contains("^CODE:$")) {
            regexPatten = "^CODE:$";
            validCodes = getValidMatchCode(csid, partnerType, regexPatten);
        } else if (fileRegex.contains("^ID:$")) {
            regexPatten = "^CODE:$";
            validCodes = getValidMatchCode(csid, partnerType, regexPatten);
        }
        if (CollectionUtils.isNotEmpty(validCodes)) {
            for (String code : validCodes) {
                String validFileName = fileRegex.replace(regexPatten, code);
                finalUrls.add(validFileName);
            }
        }
        if (CollectionUtils.isEmpty(finalUrls)) return Lists.newArrayList();
        List<TransModel> transModels = Lists.newArrayList();
        for (String url : finalUrls) {
            TransModel transModel = new TransModel();
            transModel.setAnotationType(annotationType);
            transModel.setFileUrl(url);
            transModel.setPartnerType(partnerType);
            transModel.setCsid(csid);
            transModels.add(transModel);
        }
        return transModels;
    }

    private List<String> getValidMatchCode(Long csid, PartnerType type, String codeType) {
        String currentDate = LeDateUtils.formatYYYYMMDD(new Date());
        InternalQuery query = new InternalQuery();
        query.addCriteria(new InternalCriteria("csid", "is", csid));
        query.addCriteria(new InternalCriteria("status", "eq", MatchStatus.MATCHING));
        query.addCriteria(new InternalCriteria("partners.type", "eq", type));
        InternalQuery query2 = new InternalQuery();
        query2.addCriteria(new InternalCriteria("csid", "is", csid));
        query2.addCriteria(new InternalCriteria("start_date", "eq", currentDate));
        query2.addCriteria(new InternalCriteria("partners.type", "eq", type));
        List<Match> matches1 = SbdMatchInternalApis.getMatchesByQuery(query2);
        List<Match> matches2 = SbdMatchInternalApis.getMatchesByQuery(query);
        List<Match> matches = Lists.newArrayList();
        if (CollectionUtils.isEmpty(matches1) && CollectionUtils.isEmpty(matches2)) return null;
        else if (CollectionUtils.isEmpty(matches1)) matches = matches1;
        else if (CollectionUtils.isEmpty(matches2)) matches = matches1;
        else {
            matches.addAll(matches1);
            matches.addAll(matches2);
        }
        if (CollectionUtils.isEmpty(matches)) return null;
        List<String> ids = Lists.newArrayList();
        List<String> codes = Lists.newArrayList();
        for (Match currentMatch : matches) {
            List<Partner> partners = currentMatch.getPartners();
            for (Partner partner : partners) {
                if (partner.getType().equals(type)) {
                    ids.add(partner.getId());
                    codes.add(partner.getCode());
                }
            }

        }
        if (codeType.contains("CODE")) return codes;
        return ids;
    }


}
