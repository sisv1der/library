package com.example.library.logging;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class GlobalExceptionHandlerLoggingAspect {
    private final LoggerProvider loggerProvider;

    @Pointcut("@target(org.springframework.web.bind.annotation.ControllerAdvice) && " +
            "execution(public @org.springframework.web.bind.annotation.ExceptionHandler * *(..))")
    public void globalExceptionHandlerMethods() {}

    GlobalExceptionHandlerLoggingAspect(LoggerProvider loggerProvider) {
        this.loggerProvider = loggerProvider;
    }

    @Before("globalExceptionHandlerMethods()")
    public void logBefore(JoinPoint joinPoint) {
        Logger logger = loggerProvider.getLogger(joinPoint);
        if (logger.isDebugEnabled()) {
            logger.debug("Class: {} | Entering method: {} | Args: {}",
                    joinPoint.getTarget().getClass().getSimpleName(),
                    joinPoint.getSignature().getName(),
                    Arrays.toString(joinPoint.getArgs()));
        }
    }

    @AfterReturning(pointcut = "globalExceptionHandlerMethods()", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        Logger logger = loggerProvider.getLogger(joinPoint);
        if (logger.isDebugEnabled()) {
            logger.debug("Class: {} | Executed method: {} | Args: {} | Result: [{}]",
                    joinPoint.getTarget().getClass().getSimpleName(),
                    joinPoint.getSignature().getName(),
                    Arrays.toString(joinPoint.getArgs()),
                    result);
        }
    }

    @AfterThrowing(pointcut = "globalExceptionHandlerMethods()", throwing = "ex")
    public void logAfterThrowing(JoinPoint joinPoint, Exception ex) {
        Logger logger = loggerProvider.getLogger(joinPoint);
        logger.error("Class: {} | Failed method: {} | Args: {} | Exception: {} - {}",
                joinPoint.getTarget().getClass().getSimpleName(),
                joinPoint.getSignature().getName(),
                Arrays.toString(joinPoint.getArgs()),
                ex.getClass().getSimpleName(),
                ex.getMessage(),
                ex);
    }
}
