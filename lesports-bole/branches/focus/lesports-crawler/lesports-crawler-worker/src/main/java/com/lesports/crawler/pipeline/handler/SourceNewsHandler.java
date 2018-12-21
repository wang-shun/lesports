package com.lesports.crawler.pipeline.handler;

import java.util.Date;

import org.apache.commons.beanutils.LeBeanUtils;

import com.lesports.crawler.model.source.SourceNews;
import com.lesports.crawler.pipeline.DataAttachHandler;
import com.lesports.crawler.pipeline.Model;
import com.lesports.crawler.repository.SourceNewsRepository;
import com.lesports.crawler.utils.SpringUtils;
import com.lesports.id.api.IdType;
import com.lesports.id.client.LeIdApis;
import com.lesports.msg.core.ActionType;
import com.lesports.msg.core.LeMessage;
import com.lesports.msg.core.LeMessageBuilder;
import com.lesports.msg.core.MessageSource;
import com.lesports.msg.producer.SwiftMessageApis;
import com.lesports.utils.LeDateUtils;

import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;

/**
 * 处理抓取到的新闻
 * 
 * @author denghui
 *
 */
@Model(clazz = SourceNews[].class)
public class SourceNewsHandler implements DataAttachHandler<SourceNews[]> {

    private SourceNewsRepository snr = SpringUtils.getBean(SourceNewsRepository.class);

    @Override
    public boolean handle(ResultItems resultItems, Task task, SourceNews[] t) {
        for (SourceNews sn : t) {
            SourceNews e = snr.getBySourceAndSourceId(sn.getSource(), sn.getSourceId());
            String now = LeDateUtils.formatYYYYMMDDHHMMSS(new Date());
            if (e != null) {
                LeBeanUtils.copyNotEmptyPropertiesQuietly(e, sn);
                e.setUpdateAt(now);
                snr.save(e);
                // send message
                LeMessage message = LeMessageBuilder.create().setEntityId(e.getId())
                        .setIdType(IdType.BOLE_NEWS).setActionType(ActionType.UPDATE)
                        .setSource(MessageSource.BOLE).build();
                SwiftMessageApis.sendMsgAsync(message);
            } else {
                sn.setId(LeIdApis.nextId(IdType.BOLE_NEWS));
                sn.setCreateAt(now);
                sn.setUpdateAt(now);
                snr.save(sn);
                // send message
                LeMessage message = LeMessageBuilder.create().setEntityId(sn.getId())
                        .setIdType(IdType.BOLE_NEWS).setActionType(ActionType.ADD)
                        .setSource(MessageSource.BOLE).build();
                SwiftMessageApis.sendMsgAsync(message);
            }
        }

        return true;
    }

}
