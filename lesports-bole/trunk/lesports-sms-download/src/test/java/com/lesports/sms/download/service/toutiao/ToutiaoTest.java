package com.lesports.sms.download.service.toutiao;

import com.lesports.AbstractIntegrationTest;
import org.junit.Test;

import javax.annotation.Resource;

/**
 * Created by zhonglin on 2016/7/6.
 */
public class ToutiaoTest  extends AbstractIntegrationTest {

    @Resource
    private ToutiaoCslVideoService toutiaoCslVideoService;

    @Test
    public void testToutiaoCslVideo() {
        toutiaoCslVideoService.toutiaoCslVideos();
    }
}
