package com.lesports.crawler.component.filter;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * read local file to do url filter.
 *
 * @author pangchuanxiao
 * @since 2015/11/12
 */
public class RegexConfigFilter implements FilterChain {
    private static final Logger LOG = LoggerFactory.getLogger(RegexConfigFilter.class);

    private List<Pattern> whiteList = new ArrayList<>();
    private List<Pattern> blackList = new ArrayList<>();

    public RegexConfigFilter() {
        String path = RegexConfigFilter.class.getClassLoader().getResource("regex-filter.conf").getPath();
        if (StringUtils.isNotEmpty(path)) {
            try {
                List<String> lines = FileUtils.readLines(new File(path));
                for (String line : lines) {
                    String type = line.substring(0, 1);
                    String exp = line.substring(1);
                    Pattern pattern = getPattern(exp);
                    if (null == pattern) {
                        continue;
                    }
                    if ("+".equals(type)) {
                        whiteList.add(pattern);
                    } else if ("-".equals(type)) {
                        blackList.add(pattern);
                    }
                }
            } catch (IOException e) {
                LOG.error("{}", e.getMessage(), e);
            }
        }
    }

    private Pattern getPattern(String exp) {
        return Pattern.compile("(" + exp.replace(".", "\\.").replace("*", "[^\"'#]*") + ")");
    }

    @Override
    public boolean isValid(Request request, Task task) {
        for (Pattern black : blackList) {
            if (black.matcher(request.getUrl()).find()) {
                return false;
            }
        }
        return true;
//        boolean isInWhite = false;
//        for (Pattern white : whiteList) {
//            if (white.matcher(request.getUrl()).find()) {
//                isInWhite = true;
//            }
//        }
//        return isInWhite;
    }
}
