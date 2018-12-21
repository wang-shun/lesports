package com.lesports.crawler.formatter;

import com.lesports.utils.LeDateUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import us.codecraft.webmagic.model.formatter.ObjectFormatter;

/**
 * aa.
 *
 * @author pangchuanxiao
 * @since 2015/11/4
 */
public class SinaPubDateFormatter implements ObjectFormatter<String> {
    public static final DateTimeFormatter PARSER = DateTimeFormat.forPattern("yyyy年MM月dd日HH:mm");
    @Override
    public String format(String raw) throws Exception {
        if(null == raw) {
            return null;
        }
        raw = raw.trim();
        DateTime dateTime  = PARSER.parseDateTime(raw);
        if (null == dateTime) {
            return null;
        }
        return LeDateUtils.formatYYYYMMDDHHMMSS(dateTime.toDate());
    }

    @Override
    public Class<String> clazz() {
        return String.class;
    }

    @Override
    public void initParam(String[] extra) {
    }
}
