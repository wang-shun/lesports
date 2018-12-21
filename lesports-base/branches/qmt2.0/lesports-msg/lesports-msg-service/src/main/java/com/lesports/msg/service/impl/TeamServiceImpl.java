package com.lesports.msg.service.impl;

import com.lesports.msg.service.AbstractService;
import com.lesports.msg.service.TeamService;
import org.springframework.stereotype.Service;

/**
 * lesports-projects.
 *
 * @author pangchuanxiao
 * @since 2015/8/21
 */
@Service("teamService")
public class TeamServiceImpl extends AbstractService implements TeamService {
//    private static final String TEAM_WEB_URI = LeProperties.getString("sms.web.team.uri", "/sms/v1/teams/{0}");
//    private static final String TEAM_APP_URI = LeProperties.getString("sms.app.team.uri", "/sms/app/v1/teams/{0}");
    @Override
    public boolean deleteTeamApiCache(long teamId) {
        return deleteNgxApiCache(teamId);
    }
}
