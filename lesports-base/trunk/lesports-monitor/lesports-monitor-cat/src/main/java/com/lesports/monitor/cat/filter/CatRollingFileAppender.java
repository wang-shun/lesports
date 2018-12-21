package com.lesports.monitor.cat.filter;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.classic.spi.ThrowableProxy;
import ch.qos.logback.core.rolling.RollingFileAppender;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Trace;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by zhangdeqiang on 2017/1/12.
 */
public class CatRollingFileAppender<E> extends RollingFileAppender<E> {
    private static final String TYPE = "Logback";
    private static final int MAX_LEN = 100;
    private static final String ABBR_STR = " ... ";

    @Override
    protected void append(E eventObject) {
        super.append(eventObject);

        if (eventObject instanceof LoggingEvent) {
            boolean isTraceMode = Cat.getManager().isTraceMode();
            LoggingEvent event = (LoggingEvent) eventObject;
            Level level = event.getLevel();

            if (level.isGreaterOrEqual(Level.ERROR)) {
                logError(event);
            } else if (isTraceMode) {
                logTrace(event);
            }
        }
    }

    private void logError(LoggingEvent event) {
        ThrowableProxy t = (ThrowableProxy) event.getThrowableProxy();
        String message = event.getMessage();

        if (t != null) {
            if (message != null) {
                Cat.logError(message, t.getThrowable());
            } else {
                Cat.logError(t.getThrowable());
            }
        } else {
            //调用log.error()的地方是业务错误日志，没有异常的话这里暂时不拦截。如log.error("this is an error")
            //Cat.logError(new Throwable(event.getMessage()));
            //Cat.logEvent(TYPE_ERROR, StringUtils.abbreviate(message, MAX_LEN), "ERROR", message);
        }

        //这里多提交一个精简的统一Type叫 Rooster 的Event，用于统计告警扩展用
        /*String messageWithExp = message + (t != null ? t.getClass().getName() : "");
        Cat.logEvent("SUMMARY_KEY",
                StringUtils.abbreviateMiddle(messageWithExp, ABBR_STR, MAX_LEN),
                "ERROR",
                messageWithExp);*/
    }

    private void logTrace(LoggingEvent event) {
        String name = event.getLevel().toString();
        String data = event.getMessage();
        ThrowableProxy t = (ThrowableProxy) event.getThrowableProxy();

        if (t != null) {
            data = data + '\n' + buildExceptionStack(t.getThrowable());
        }
        Cat.logTrace(TYPE, name, Trace.SUCCESS, data);
    }

    private String buildExceptionStack(Throwable exception) {
        if (exception != null) {
            StringWriter writer = new StringWriter(2048);
            exception.printStackTrace(new PrintWriter(writer));
            return writer.toString();
        }

        return "";
    }

}
