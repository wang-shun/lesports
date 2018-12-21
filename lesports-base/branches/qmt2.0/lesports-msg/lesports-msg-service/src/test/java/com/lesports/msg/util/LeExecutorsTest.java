package com.lesports.msg.util;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Predicate;
import junit.framework.Assert;
import junit.framework.TestCase;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * lesports-projects
 *
 * @author pangchuanxiao
 * @since 15-12-26
 */
public class LeExecutorsTest extends TestCase {
    private static final Logger LOG = LoggerFactory.getLogger(LeExecutorsTest.class);

    @Test
    public void testExecuteWithRetry() throws Exception {
        Predicate<Object> function = new Predicate<Object>() {
            @Override
            public boolean apply(@Nullable Object o) {
                return false;
            }
        };
        boolean result = LeExecutors.executeWithRetry(3, 100, function, null, "test");
        Assert.assertTrue(!result);
    }

    @Test
    public void testA() throws Exception {
        List<String> lines = FileUtils.readLines(new File("d:/tongji.txt"));
        if (CollectionUtils.isNotEmpty(lines)) {
            for (String line : lines) {
                if (StringUtils.isNotEmpty(line)) {
                    String[] ids = line.split(",");
                    Arrays.sort(ids);
                    FileUtils.write(new File("d:/sorted_tongji.txt"), JSONObject.toJSONString(ids) + "\n", true);
                }
            }
        }
    }
}
