package com.lesports.crawler.processor;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.lesports.crawler.model.config.CrawlerConfig;
import com.lesports.crawler.model.source.Content;
import com.lesports.crawler.model.source.Source;
import com.lesports.crawler.repository.CrawlerConfigRepository;

import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.model.annotation.TargetUrl;
import us.codecraft.webmagic.processor.PageProcessor;

/**
 * ProcessorRegistry
 * 
 * @author denghui
 *
 */
@Component
public class ProcessorRegistry {
    private final static Logger LOG = LoggerFactory.getLogger(ProcessorRegistry.class);
    private final static ScheduledExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor();
    private Map<String, Multimap<AbstractPageProcessor<?>, Pattern>> registry = new HashMap<>();

    @Resource
    private CrawlerConfigRepository crawlerConfigRepository;

    public void loadEnabledProcessors() {
        // 等待第一次执行完成
        ConfigLoader configLoader = new ConfigLoader();
        configLoader.run();
        EXECUTOR_SERVICE.scheduleAtFixedRate(configLoader, 30, 30, TimeUnit.SECONDS);
    }

    /**
     * 根据源和类型注册
     * 
     * @param source
     * @param content
     * @param pageProcessor
     */
    public void register(Source source, Content content, AbstractPageProcessor<?> pageProcessor) {
        String key = buildKey(source, content);
        Multimap<AbstractPageProcessor<?>, Pattern> subRegistry = registry.get(key);
        if (subRegistry == null) {
            subRegistry = ArrayListMultimap.create();
        }

        List<Pattern> patterns = getPatternsOfPageProcessor(pageProcessor);
        subRegistry.putAll(pageProcessor, patterns);
        registry.put(key, subRegistry);
    }

    /**
     * 根据源和类型获取处理器
     * 
     * @param source
     * @param content
     * @return
     */
    public Collection<AbstractPageProcessor<?>> get(Source source, Content content) {
        String key = buildKey(source, content);
        Multimap<AbstractPageProcessor<?>, Pattern> multimap = registry.get(key);
        if (multimap != null) {
            return multimap.keySet();
        }
        return Collections.emptyList();
    }

    /**
     * 获取可以处理特定url的所有开启的处理器
     * 
     * @param url
     * @return
     */
    public List<PageProcessor> getEnabledProcessorsForUrl(String url) {
        List<PageProcessor> processors = new ArrayList<>();
        for (Entry<String, Multimap<AbstractPageProcessor<?>, Pattern>> entry : registry.entrySet()) {
            for (Entry<AbstractPageProcessor<?>, Pattern> subEntry : entry.getValue().entries()) {
                if (subEntry.getKey().isEnabled() && isMatchedPattern(url, subEntry.getValue())) {
                    processors.add(subEntry.getKey());
                }
            }
        }
        return processors;
    }

    /**
     * 判断是否有开启的处理器可以处理url
     * 
     * @param url
     * @return
     */
    public boolean hasEnabledProcessorForUrl(String url) {
        for (Entry<String, Multimap<AbstractPageProcessor<?>, Pattern>> entry : registry.entrySet()) {
            for (Entry<AbstractPageProcessor<?>, Pattern> subEntry : entry.getValue().entries()) {
                if (subEntry.getKey().isEnabled() && isMatchedPattern(url, subEntry.getValue())) {
                    return true;
                }
            }
        }
        LOG.info("NO enabled processor for url: {}", url);
        return false;
    }

    /**
     * 获取处理URL对应的Site配置
     * 
     * @param url
     * @return
     */
    public Site getSiteForUrl(String url) {
        for (PageProcessor processor : getEnabledProcessorsForUrl(url)) {
            return processor.getSite();
        }

        return null;
    }

    private String buildKey(Source source, Content content) {
        return String.format("%s_%s", source, content);
    }

    private List<Pattern> getPatternsOfPageProcessor(PageProcessor pageProcessor) {
        List<Pattern> patterns = Lists.newArrayList();
        Class<?> clazz = pageProcessor.getClass();
        Annotation annotation = clazz.getAnnotation(TargetUrl.class);
        if (annotation == null) {
            patterns.add(Pattern.compile("(.*)"));
        } else {
            TargetUrl targetUrl = (TargetUrl) annotation;
            String[] value = targetUrl.value();
            for (String s : value) {
                patterns.add(Pattern.compile("(" + s.replace(".", "\\.").replace("*", "[^\"'#]*") + ")"));
            }
        }

        return patterns;
    }

    private boolean isMatchedPattern(String url, Pattern pattern) {
        return pattern.matcher(url).find();
    }

    class ConfigLoader implements Runnable {

        @Override
        public void run() {
            List<CrawlerConfig> configs = crawlerConfigRepository.getAllConfigs();
            for (CrawlerConfig config : configs) {
                Collection<AbstractPageProcessor<?>> processors = get(config.getSource(), config.getContent());
                for (AbstractPageProcessor<?> processor : processors) {
                    processor.setEnabled(config.isEnabled());
                }
            }

        }
    }
}
