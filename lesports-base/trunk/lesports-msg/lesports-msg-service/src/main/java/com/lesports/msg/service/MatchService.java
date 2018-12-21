package com.lesports.msg.service;

import com.lesports.sms.api.vo.TMatch;
import com.lesports.sms.model.Match;

/**
 * lesports-projects.
 *
 * @author: pangchuanxiao
 * @since: 2015/7/10
 */
public interface MatchService {
    public boolean deleteMatchApiCache(long matchId);

    public boolean deleteSmsRealtimeWebMatch(long matchId);

    public boolean deleteSisMatch(long matchId);

    public boolean deleteMatchPage(long matchId);

    public boolean indexMatch(Match match);
}
