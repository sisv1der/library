package com.example.library.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class LoggingAspect {
    private static final String SERVICE_PACKAGE = "execution(* com.example.library.service.*.*(..))";

    @Before(SERVICE_PACKAGE)
    public void logBefore(JoinPoint joinPoint) {
        Logger logger = getLogger(joinPoint);
        if (logger.isDebugEnabled()) {
            logger.debug("Class: {} | Entering method: {} | Args: [{}]",
                    joinPoint.getTarget().getClass().getSimpleName(),
                    joinPoint.getSignature().getName(),
                    Arrays.toString(joinPoint.getArgs())
            );
        }
    }

    @AfterReturning(pointcut = SERVICE_PACKAGE, returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        Logger logger = getLogger(joinPoint);
        if (logger.isDebugEnabled()) {
            logger.debug("Class: {} | Executed method: {} | Args: {} | Result: [{}]",
                    joinPoint.getTarget().getClass().getSimpleName(),
                    joinPoint.getSignature().getName(),
                    Arrays.toString(joinPoint.getArgs()),
                    result
            );
        }
    }

    @AfterThrowing(pointcut = SERVICE_PACKAGE, throwing = "ex")
    public void logAfterThrowing(JoinPoint joinPoint, Exception ex) {
        Logger logger = getLogger(joinPoint);
            logger.error("Class: {} | Failed method: {} | Args: {} | Exception: {} - {}",
                    joinPoint.getTarget().getClass().getSimpleName(),
                    joinPoint.getSignature().getName(),
                    Arrays.toString(joinPoint.getArgs()),
                    ex.getClass().getSimpleName(),
                    ex.getMessage(),
                    ex
            );
    }

    private Logger getLogger(JoinPoint joinPoint) {
        return LoggerFactory.getLogger(joinPoint.getTarget().getClass());
    }
}