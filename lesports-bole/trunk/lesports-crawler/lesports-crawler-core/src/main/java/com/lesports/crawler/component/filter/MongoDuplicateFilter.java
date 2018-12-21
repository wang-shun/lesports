package com.lesports.crawler.component.filter;

import com.lesports.crawler.model.FetchedRequest;
import com.lesports.crawler.repository.FetchedRepository;
import com.lesports.crawler.utils.SpringUtils;
import com.lesports.utils.LeDateUtils;
import com.lesports.utils.math.LeNumberUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

/**
 * put fetched url to redis, and check if it is fetched when do the duplicate operation.
 *
 * @author pangchuanxiao
 * @since 2015/11/12
 */
public class MongoDuplicateFilter implements FilterChain {
    private static final Logger LOG = LoggerFactory.getLogger(MongoDuplicateFilter.class);
    private FetchedRepository fetchedRepository = SpringUtils.getBean(FetchedRepository.class);
    private List<IntervalSkiper> skipList = new ArrayList<>();

    public MongoDuplicateFilter() {
        String path = RegexConfigFilter.class.getClassLoader().getResource("duplicate-skiper.conf").getPath();
        if (StringUtils.isNotEmpty(path)) {
            try {
                List<String> lines = FileUtils.readLines(new File(path));
                for (String line : lines) {
                    String[] exps = StringUtils.split(line, " ");
                    Pattern pattern = getPattern(exps[0]);
                    if (null == pattern) {
                        continue;
                    }
                    long interval = 0;
                    if (exps.length > 1 && LeNumberUtils.toInt(interval) > 0) {
                        interval = LeNumberUtils.toInt(interval);
                    }
                    IntervalSkiper intervalSkiper = new IntervalSkiper(pattern, interval);
                    skipList.add(intervalSkiper);
                }
            } catch (IOException e) {
                LOG.error("{}", e.getMessage(), e);
            }
        }
    }

    @Override
    public boolean isValid(Request request, Task task) {
        FetchedRequest fetchedObject = fetchedRepository.findOne(request.getUrl());
        if (null == fetchedObject) {
            return true;
        }
        if (FetchedRequest.Status.FETCHING == fetchedObject.getStatus() || FetchedRequest.Status.WAITING == fetchedObject.getStatus()) {
            return false;
        }

        boolean valid = false;
        if (CollectionUtils.isNotEmpty(skipList)) {
            for (IntervalSkiper intervalSkiper : skipList) {
                Pattern pattern = intervalSkiper.getPattern();
                long interval = intervalSkiper.getInterval();
                long lastTimeStamp = LeDateUtils.parseYYYYMMDDHHMMSS(fetchedObject.getFetchTime()).getTime();
                long timeAfterLast = (new Date().getTime() - lastTimeStamp) / 1000;
                if (pattern.matcher(request.getUrl()).find() && timeAfterLast > interval) {
                    valid = true;
                }
            }
        }
        return valid;
    }

    private Pattern getPattern(String exp) {
        return Pattern.compile("(" + exp.replace(".", "\\.").replace("*", "[^\"'#]*") + ")");
    }

}
