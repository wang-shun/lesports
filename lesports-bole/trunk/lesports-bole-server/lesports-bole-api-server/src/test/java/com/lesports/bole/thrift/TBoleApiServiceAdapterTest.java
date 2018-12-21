package com.lesports.bole.thrift;

import com.lesports.AbstractIntegrationTest;
import com.lesports.bole.api.vo.TBNews;
import junit.framework.TestCase;
import org.junit.Test;
import org.springframework.util.Assert;

import javax.annotation.Resource;

/**
 * trunk.
 *
 * @author pangchuanxiao
 * @since 2016/3/5
 */
public class TBoleApiServiceAdapterTest extends AbstractIntegrationTest {

    @Resource
    private TBoleApiServiceAdapter thriftBoleApiService;



    @Test
    public void testGetNewsById() throws Exception {
        TBNews tbNews = thriftBoleApiService.getNewsById(30605044);
        Assert.notNull(tbNews);
    }
}