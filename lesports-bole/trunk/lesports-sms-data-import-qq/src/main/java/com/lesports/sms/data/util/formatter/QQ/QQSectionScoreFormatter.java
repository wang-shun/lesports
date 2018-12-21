package com.lesports.sms.data.util.formatter.QQ;

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.model.DictEntry;
import com.lesports.sms.model.Match;
import net.minidev.json.JSONArray;
import org.apache.commons.collections.CollectionUtils;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

/**
 * Created by qiaohongxin on 2016/11/30.
 */
public class QQSectionScoreFormatter implements Function<Object, Map<Integer, Match.SectionResult>> {
    @Nullable
    @Override
    public Map<Integer, Match.SectionResult> apply(Object input) {

        Map<Integer, Match.SectionResult> sectionResults = Maps.newHashMap();
        if (net.minidev.json.JSONArray.class.isAssignableFrom(input.getClass())) {
            JSONArray arrays = (JSONArray) input;
            if (arrays != null && arrays.size() > 0) {
                for (int array = 1; array < arrays.size(); array++) {
                    Match.SectionResult result = new Match.SectionResult();
                    if (array < 5) {
                        if (!arrays.get(array).toString().equals("-")) {
                            List<DictEntry> entryList = SbdsInternalApis.getDictEntriesByName("^第" + array + "节$");
                            if (CollectionUtils.isNotEmpty(entryList)) {
                                result.setResult(arrays.get(array).toString());
                                result.setOrder(array);
                                result.setSection(entryList.get(0).getId());
                                sectionResults.put(array, result);
                            }
                        }
                    }
                    if (array < 9) {
                        if (!arrays.get(array).toString().equals("-")) {
                            List<DictEntry> entryList = SbdsInternalApis.getDictEntriesByName("^加时" + (array - 4));
                            if (CollectionUtils.isNotEmpty(entryList)) {
                                result.setResult(arrays.get(array).toString());
                                result.setOrder(array);
                                result.setSection(entryList.get(0).getId());
                                sectionResults.put(array, result);
                            }
                        }
                    }
                }
            }
        }
        return sectionResults;
    }
}