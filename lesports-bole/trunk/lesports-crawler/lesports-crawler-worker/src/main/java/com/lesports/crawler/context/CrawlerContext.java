package com.lesports.crawler.context;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.Resource;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import com.lesports.crawler.processor.ProcessorRegistry;

/**
 * aa.
 *
 * @author pangchuanxiao
 * @since 2015/11/18
 */
@Component
public class CrawlerContext implements ApplicationListener<ContextRefreshedEvent> {
    private final static ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool();

    @Resource
    private SpiderFactory spiderFactory;
    @Resource
    private ProcessorRegistry processorRegistry;
    
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if(event.getApplicationContext().getParent() == null){
            // 容器启动完成开始执行
            processorRegistry.loadEnabledProcessors();
            EXECUTOR_SERVICE.execute(spiderFactory.getInstance());
        }
    }
}
