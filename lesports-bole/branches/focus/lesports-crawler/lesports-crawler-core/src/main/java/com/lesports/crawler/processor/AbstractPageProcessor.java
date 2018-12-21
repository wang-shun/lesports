package com.lesports.crawler.processor;

import java.util.Collection;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.lesports.crawler.model.source.Content;
import com.lesports.crawler.model.source.Source;
import com.lesports.crawler.utils.Constants;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

/**
 * aa.
 *
 * @author pangchuanxiao
 * @since 2015/11/20
 */
public abstract class AbstractPageProcessor<T> implements PageProcessor {
	private static final Logger LOG = LoggerFactory.getLogger(AbstractPageProcessor.class);
	protected Site site = Site.me();
	protected volatile boolean enabled ;
	public static final Long GAMEFTYPE_FOOTBALL = 100015000l;
	public static final Long GAMEFTYPE_BASKETBALL = 100014000l;
	public static final Long GAMEFTYPE_TENNIS = 100016000l;
	public static final Long GAMEFTYPE_GENERAL = 100019000l;

	@Resource
	private ProcessorRegistry processorRegistry;
	
	@PostConstruct
	public void register() {
	    processorRegistry.register(getSource(), getContent(), this);
	}
	
	@Override
	public void process(Page page) {
		try {
			T t = doProcess(page);
			page.putField(Constants.KEY_DATA, t);
			page.addTargetRequests(getTargetRequests(page));
		} catch (Exception e) {
			LOG.error("{}", e.getMessage(), e);
		}

		page.putField(Constants.KEY_URL, page.getRequest().getUrl());
	}

	@Override
	public Site getSite() {
		return site;
	}

	public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    protected List<String> getTargetRequests(Page page) {
		return Lists.newArrayList();
	}

    protected abstract T doProcess(Page page);

    protected abstract Source getSource();
	
    protected abstract Content getContent();
	
	protected boolean requiredEmpty(String selector, String value, String url) {
		if (Strings.isNullOrEmpty(value)) {
			LOG.error("element empty in {} on page {}", selector, url);
			return true;
		}

		return false;
	}

	@SuppressWarnings("rawtypes")
	protected boolean requiredEmpty(String selector, Collection value, String url) {
		if (CollectionUtils.isEmpty(value)) {
			LOG.error("element empty in {} on page {}", selector, url);
			return true;
		}

		return false;
	}
}
