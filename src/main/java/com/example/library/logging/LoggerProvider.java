package com.example.library.logging;

import org.aspectj.lang.JoinPoint;
import org.slf4j.Logger;

import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LoggerProvider {
    public Logger getLogger(JoinPoint joinPoint) {
        return LoggerFactory.getLogger(joinPoint.getTarget().getClass());
    }
}
