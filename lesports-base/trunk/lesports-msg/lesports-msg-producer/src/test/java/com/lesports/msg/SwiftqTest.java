package com.lesports.msg;

import com.lesports.msg.core.ActionType;
import com.lesports.msg.core.LeMessage;
import com.lesports.msg.core.LeMessageBuilder;
import com.lesports.msg.core.MessageSource;
import com.lesports.msg.producer.SwiftMessageApis;
import com.lesports.utils.math.LeNumberUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.springframework.util.Assert;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by lufei1 on 2015/6/1.
 */
public class SwiftqTest extends AbstractIntegrationTest {


    @Test
    public void send() throws ExecutionException, InterruptedException {
        LeMessage message = LeMessageBuilder.create().setEntityId(24800195l).setSource(MessageSource.SMS_SERVICE)
                .setActionType(ActionType.ADD).build();
        Future<Boolean> result = SwiftMessageApis.sendMsgAsync(message);
        Assert.isTrue(result.get());
    }

    @Test
    public void testConnection() throws ExecutionException, InterruptedException {
        for (int i = 0; i < 10; i++) {
            LeMessage message = LeMessageBuilder.create().setEntityId(100015003l).setSource(MessageSource.SMS_SERVICE)
                    .setActionType(ActionType.ADD).build();
            Future<Boolean> result = SwiftMessageApis.sendMsgAsync(message);
            Assert.isTrue(result.get());
        }
    }

    @Test
    public void fixData() throws ExecutionException, InterruptedException, IOException {
        List<String> lines = FileUtils.readLines(new File("d:/temp"));
        if (CollectionUtils.isNotEmpty(lines)) {
            for (String line : lines) {
                long id = LeNumberUtils.toLong(line);
                if (id <= 0) {
                    continue;
                }
                LeMessage message = LeMessageBuilder.create().setEntityId(id).setSource(MessageSource.SMS_SERVICE)
                        .setActionType(ActionType.UPDATE).build();
                Future<Boolean> result = SwiftMessageApis.sendMsgAsync(message);
                Assert.isTrue(result.get());

            }
        }

    }
}
