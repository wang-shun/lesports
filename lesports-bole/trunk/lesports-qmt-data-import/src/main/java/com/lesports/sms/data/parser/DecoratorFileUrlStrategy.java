package com.lesports.sms.data.parser;

import com.lesports.sms.data.model.TransModel;

import java.util.List;

/**
 * Created by qiaohongxin on 2017/2/27.
 */
public abstract class DecoratorFileUrlStrategy implements FileUrlStrategy {
    FileUrlStrategy fileUrlStrategy;

    protected DecoratorFileUrlStrategy(FileUrlStrategy fileUrlStrategy) {
        this.fileUrlStrategy = fileUrlStrategy;
    }

    public FileUrlStrategy getFileUrlStrategy() {
        return fileUrlStrategy;
    }

    public void setFileUrlStrategy(FileUrlStrategy fileUrlStrategy) {
        this.fileUrlStrategy = fileUrlStrategy;
    }
}
