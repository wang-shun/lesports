package com.lesports.sms.data.util.formatter.sportrard;

import com.google.common.base.Function;
import com.lesports.qmt.config.client.QmtConfigDictInternalApis;
import com.lesports.qmt.config.model.DictEntry;
import com.lesports.qmt.sbd.model.Match;
import com.lesports.query.InternalCriteria;
import com.lesports.query.InternalQuery;
import com.lesports.sms.data.model.commonImpl.DefaultLiveScore;
import com.lesports.sms.data.util.CommonUtil;
import com.lesports.sms.data.util.formatter.ElementFormatter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Element;

import javax.annotation.Nullable;
import java.util.*;

/**
 * Created by qiaohongxin on 2016/2/23.
 */
public class SportrardHomeSectionResultFormatter extends ElementFormatter implements Function<Element, Map<Integer, com.lesports.qmt.sbd.model.Match.SectionResult>> {
    @Nullable
    @Override
    public Map<Integer, com.lesports.qmt.sbd.model.Match.SectionResult> apply(Element scores) {
        int hScore = 0;
        int ftScore = 0;
        int otScore = 0;
        int atScore = 0;
        Map<Integer, Match.SectionResult> sectionResults = new HashMap<>();
        try {
            if (scores != null) {
                Iterator scoreIterator = scores.elementIterator("Score");
                while (scoreIterator.hasNext()) {
                    Element score = (Element) scoreIterator.next();
                    if (score.attributeValue("type").equals("Period1")) {
                        hScore = CommonUtil.parseInt(score.elementText("Team1"), 0);
                        Match.SectionResult sectionResult = new Match.SectionResult();
                        sectionResult.setResult(hScore + "");
                        sectionResult.setOrder(1);
                        sectionResult.setSection(getDictIdByName("^上半场$"));
                        sectionResults.put(1, sectionResult);
                    }
                    if (score.attributeValue("type").equals("Normaltime")) {
                        ftScore = CommonUtil.parseInt(score.elementText("Team1"), 0);
                        Match.SectionResult sectionResult = new Match.SectionResult();
                        sectionResult.setResult((ftScore - hScore) + "");
                        sectionResult.setOrder(2);
                        sectionResult.setSection(getDictIdByName("^下半场$"));
                        sectionResults.put(2, sectionResult);
                    }
                    //加时赛结束
                    if (score.attributeValue("type").equals("Overtime")) {
                        otScore = CommonUtil.parseInt(score.elementText("Team1"), 0);
                        Match.SectionResult sectionResult = new Match.SectionResult();
                        sectionResult.setResult((otScore - ftScore) + "");
                        sectionResult.setOrder(3);
                        sectionResult.setSection(getDictIdByName("^加时$"));
                        sectionResults.put(3, sectionResult);
                    }
                    //点球大战结束
                    if (score.attributeValue("type").equals("Penalties")) {
                        atScore = CommonUtil.parseInt(score.elementText("Team1"), 0);
                        Match.SectionResult sectionResult = new Match.SectionResult();
                        sectionResult.setResult((atScore - otScore) + "");
                        sectionResult.setOrder(4);
                        sectionResult.setSection(getDictIdByName("^点球$"));
                        sectionResults.put(4, sectionResult);
                    }
                }
            }

        } catch (Exception e) {

        }
        return sectionResults;
    }
}
