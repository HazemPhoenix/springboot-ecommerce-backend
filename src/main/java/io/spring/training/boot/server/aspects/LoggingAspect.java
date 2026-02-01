package io.spring.training.boot.server.aspects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Aspect
@Component
public class LoggingAspect {
    private final Logger logger = Logger.getLogger(LoggingAspect.class.getName());

    @Before("execution(* io.spring.training.boot.server.*.*.*(..))")
    public void log(JoinPoint joinPoint){
        logger.info("Calling: " + joinPoint.getTarget().getClass() + "." +joinPoint.getSignature().getName());
    }
}
