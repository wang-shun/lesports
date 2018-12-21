package com.lesports.sms.data.adapter;

import com.google.common.collect.Lists;
import com.lesports.api.common.CountryCode;
import com.lesports.api.common.LangString;
import com.lesports.api.common.LanguageCode;
import com.lesports.sms.data.model.TransModel;

import java.util.List;

/**
 * Created by qiaohongxin on 2016/11/18.
 */
public abstract class DefualtAdaptor {
    public abstract Boolean nextProcessor(TransModel transModel, Object object);

    public List<LangString> getMultiLang(String value) {
        List<LangString> langStrings = Lists.newArrayList();
        if (null != value) {
            LangString cn = new LangString();
            cn.setLanguage(LanguageCode.ZH_CN);
            cn.setValue(value);
            langStrings.add(cn);
            LangString hk = new LangString();
            hk.setLanguage(LanguageCode.ZH_HK);
            //hk.setValue(HanLP.convertToTraditionalChinese(value));
            langStrings.add(hk);
        }
        return langStrings;
    }

    public List<CountryCode> getAllowCountries() {
        List<CountryCode> countryCodes = Lists.newArrayList();
        countryCodes.add(CountryCode.CN);
        return countryCodes;
    }
}
