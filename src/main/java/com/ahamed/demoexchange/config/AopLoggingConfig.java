package com.ahamed.demoexchange.config;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;


import java.time.Instant;

@Component
@Aspect
@Slf4j
public class AopLoggingConfig {

    @Around("execution(* com.ahamed.demoexchange.controller.*.*(..))")
    public Object logControllerMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        Instant start = Instant.now();
        Object result = joinPoint.proceed();
        Instant end = Instant.now();
        log.info("Executed {}.{} in {} ms", joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(), end.toEpochMilli() - start.toEpochMilli());
        return result;
    }
}
