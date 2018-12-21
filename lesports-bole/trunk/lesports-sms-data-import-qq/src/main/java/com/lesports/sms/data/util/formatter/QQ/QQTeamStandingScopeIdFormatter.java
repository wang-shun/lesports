package com.lesports.sms.data.util.formatter.QQ;

import com.google.common.base.Function;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.data.model.QQConstants;
import com.lesports.sms.data.util.formatter.ElementFormatter;
import com.lesports.sms.model.Team;
import net.minidev.json.JSONArray;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * Created by qiaohongxin on 2016/11/30.
 */
public class QQTeamStandingScopeIdFormatter implements Function<Object, Long> {
    @Nullable
    @Override
    public Long apply(Object input) {
        if (net.minidev.json.JSONArray.class.isAssignableFrom(input.getClass())) {
            JSONArray arrays = (JSONArray) input;
            if (arrays != null && arrays.size() > 0) {
                Map<String, Object> currentObject = (Map<String, Object>) arrays.get(0);
                Object teamId = currentObject.get("teamId");
                Team team = null;
                if (teamId != null) {
                    team = SbdsInternalApis.getTeamByQQId(teamId.toString());
                }
                if (arrays.size() == 5 && team != null) return team.getRegion();
                else if (arrays.size() == 15 && team != null) return team.getConference();
            }
        }
        return 100009000L;
    }
}