package com.lesports.crawler.processor.zhibo8;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.lesports.crawler.model.source.Source;
import com.lesports.crawler.processor.AbstractPageProcessor;

/**
 * aa.
 *
 * @author pangchuanxiao
 * @since 2015/11/20
 */
public abstract class AbstractZhibo8Processor<T>  extends AbstractPageProcessor<T>  {
	  
    String getId(String url) {
        String exp = "(\\w+)\\.htm$";
        Pattern pattern = Pattern.compile(exp);
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return null;
        }
    }
    
    @Override
    protected Source getSource() {
        return Source.ZHIBO8;
    }
}
