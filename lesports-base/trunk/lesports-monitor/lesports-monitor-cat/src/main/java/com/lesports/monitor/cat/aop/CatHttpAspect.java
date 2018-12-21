package com.lesports.monitor.cat.aop;

import com.dianping.cat.Cat;
import com.dianping.cat.CatConstants;
import com.dianping.cat.message.Event;
import com.dianping.cat.message.Transaction;
import com.lesports.monitor.cat.constant.CatMonitorAnnotation;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Enumeration;

/**
 * 实现HTTP接口的日志切面
 * Created by zhangdeqiang on 2017/3/7.
 */
//@Aspect
//@Component
public class CatHttpAspect {
    private static final Logger LOGGER = LoggerFactory.getLogger(CatHttpAspect.class);

    /**
     * 定义一个切入点【method annotation】.
     */
    @Pointcut("@annotation(com.lesports.monitor.cat.constant.CatMonitorAnnotation)")
    public void catCut() {
    }

    @AfterReturning(pointcut = "catCut()", returning = "returnValue")
    public void doAfterReturning(JoinPoint joinPoint, Object returnValue) {
        // 处理完请求
        LOGGER.info("CatHttpAspect.doAfterReturning()");

        if (returnValue == null) {

            Transaction t = Cat.newTransaction(CatConstants.TYPE_RESPONSE, "cat-monitor");
            String methodName = joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName();
            try {
                Cat.logEvent(joinPoint.getSignature().getDeclaringTypeName(), methodName);
                t.setStatus(Transaction.SUCCESS);
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                Cat.logError(joinPoint.getSignature().getDeclaringTypeName(), e);
                t.setStatus(e.getClass().getSimpleName());
            } finally {
                t.complete();
            }
        }


    }
}
