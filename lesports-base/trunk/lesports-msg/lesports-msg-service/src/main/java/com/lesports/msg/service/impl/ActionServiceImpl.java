package com.lesports.msg.service.impl;

import com.lesports.msg.core.LeMessage;
import com.lesports.msg.service.ActionService;
import org.springframework.stereotype.Service;

/**
 * trunk.
 *
 * @author pangchuanxiao
 * @since 2015/10/13
 */
@Service("actionService")
public class ActionServiceImpl implements ActionService {
    @Override
    public boolean saveFailAction(LeMessage leMessage) {
        return true;
    }
}
