package com.lesports.crawler.pipeline;

import com.lesports.crawler.utils.Constants;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

/**
 * aa.
 *
 * @author pangchuanxiao
 * @since 2015/11/18
 */
public class DataAttachPipeline implements Pipeline {
    Map<Class, DataAttachHandler> handlers = new HashMap<>();
    @Override
    public void process(ResultItems resultItems, Task task) {
        Object data = resultItems.get(Constants.KEY_DATA);
        if (null == data) {
            resultItems.put(Constants.KEY_IS_DATA_NULL, true);
            return;
        }
        DataAttachHandler dataHandler = handlers.get(data.getClass());
        if (null == dataHandler) {
            resultItems.put(Constants.KEY_IS_HANDLER_NULL, true);
            return;
        }
        boolean result = dataHandler.handle(resultItems, task, data);
        resultItems.put(Constants.KEY_ATTACH_RESULT, result);
    }

    public DataAttachPipeline addHandler(DataAttachHandler dataHandler) {
        Class clazz = getAnnotationValue(dataHandler);
        if (null != clazz) {
            handlers.put(clazz, dataHandler);
        }
        return this;
    }

    private Class getAnnotationValue(DataAttachHandler dataHandler) {
        Class clazz = dataHandler.getClass();
        Annotation annotation = clazz.getAnnotation(Model.class);
        if (null == annotation) {
            return null;
        }
        return ((Model)annotation).clazz();
    }
}
