package com.lesports.sms.data.adapter;

import com.google.common.collect.Lists;
import com.hankcs.hanlp.HanLP;
import com.lesports.api.common.CountryCode;
import com.lesports.api.common.LangString;
import com.lesports.api.common.LanguageCode;
import com.lesports.qmt.sbd.model.Partner;
import com.lesports.qmt.sbd.model.PartnerType;

import java.util.List;

/**
 * Created by qiaohongxin on 2016/11/18.
 */
public abstract class DefualtAdaptor {
    public abstract Boolean nextProcessor(PartnerType partnerType, Long csid, Object object);

    public Partner getPartner(String partnerId, PartnerType type) {
        Partner partner = new Partner();
        partner.setId(partnerId);
        partner.setType(type);
        return partner;
    }

    public Partner getPartnerwithCode(String partnerId, String code, PartnerType type) {
        Partner partner = new Partner();
        partner.setId(partnerId);
        partner.setCode(code);
        partner.setType(type);
        return partner;
    }

    public List<LangString> getMultiLang(String value) {
        List<LangString> langStrings = Lists.newArrayList();
        if (null != value) {
            LangString cn = new LangString();
            cn.setLanguage(LanguageCode.ZH_CN);
            cn.setValue(value);
            langStrings.add(cn);
            LangString hk = new LangString();
            hk.setLanguage(LanguageCode.ZH_HK);
            hk.setValue(HanLP.convertToTraditionalChinese(value));
            langStrings.add(hk);
            LangString us = new LangString();
            hk.setLanguage(LanguageCode.EN_US);
            hk.setValue(value);
            langStrings.add(us);
            LangString ushk = new LangString();
            hk.setLanguage(LanguageCode.EN_HK);
            hk.setValue(value);
            langStrings.add(ushk);
        }
        return langStrings;
    }

    public List<CountryCode> getAllowCountries() {
        List<CountryCode> countryCodes = Lists.newArrayList();
        countryCodes.add(CountryCode.CN);
        return countryCodes;
    }
}
