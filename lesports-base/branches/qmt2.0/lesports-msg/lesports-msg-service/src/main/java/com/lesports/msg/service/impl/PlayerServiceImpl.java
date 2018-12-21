package com.lesports.msg.service.impl;

import com.lesports.msg.service.AbstractService;
import com.lesports.msg.service.PlayerService;
import com.lesports.sms.model.Player;
import com.lesports.utils.LeProperties;
import com.lesports.utils.http.RestTemplateFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * lesports-projects.
 *
 * @author: pangchuanxiao
 * @since: 2015/7/10
 */
@Service
public class PlayerServiceImpl extends AbstractService implements PlayerService {
    private static final Logger LOG = LoggerFactory.getLogger(PlayerServiceImpl.class);
    private static final String INDEX_SMS_MATCH_URL = LeProperties.getString("search.index.host", "http://10.154.157.44:9380") + "/search/v1/i/sms/players/{0}?caller=1007";
    private static final RestTemplate TEMPLATE = RestTemplateFactory.getTemplate();

    @Override
    public boolean indexPlayer(Player player) {
        try {
            MultiValueMap<String, String> param = new LinkedMultiValueMap<>();
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("content-type", "application/x-www-form-urlencoded");
            HttpEntity httpEntity = new HttpEntity(param, httpHeaders);

            ResponseEntity<String> result = TEMPLATE.exchange(INDEX_SMS_MATCH_URL, HttpMethod.PUT, httpEntity, String.class, player.getId());
            LOG.info("indexing player {}, result {}", player.getId(), result.getBody());
            return true;
        } catch (Exception e) {
            LOG.error("{}", e.getMessage(), e);
        }
        return false;
    }

}
