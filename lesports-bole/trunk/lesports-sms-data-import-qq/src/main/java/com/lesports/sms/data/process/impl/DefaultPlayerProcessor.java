package com.lesports.sms.data.process.impl;

import com.google.common.collect.Lists;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.data.process.BeanProcessor;
import com.lesports.sms.model.Player;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by qiaohongxin on 2016/5/11.
 */
@Service("playerProcessor")
public class DefaultPlayerProcessor extends BeanProcessor {
    private static Logger LOG = LoggerFactory.getLogger(DefaultPlayerProcessor.class);

    public Boolean process(Object object) {
        Player newPlayer = (Player) object;
        try {
            Player player = SbdsInternalApis.getPlayerByQQId(newPlayer.getQQId());
            if (player == null) {
                SbdsInternalApis.savePlayer(newPlayer);
                return true;
            } else {
                List<String> configs = getUpdateProperty();
                if (CollectionUtils.isEmpty(configs)) {
                    LOG.warn("NO pemission to Update property");
                    return false;
                }
                player = (Player) updateObject(configs, player, newPlayer);
                if (SbdsInternalApis.savePlayer(player) > 0) {
                    LOG.warn("The player:{} is updated", player.getId());
                }
            }
            return true;
        } catch (Exception e) {
            LOG.error("Schedule Exception error ,e:{}", e);
            return false;
        }
    }

    private List<String> getUpdateProperty() {
        List<String> properties = Lists.newArrayList();
        return properties;
    }
}
