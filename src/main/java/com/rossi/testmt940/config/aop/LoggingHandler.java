package com.rossi.testmt940.config.aop;


import com.rossi.testmt940.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Example From :
 * https://makeinjava.com/logging-aspect-restful-web-service-spring-aop-request-response/
 * https://www.baeldung.com/spring-aop-pointcut-tutorial
 */

@Slf4j
@Aspect
@Component
public class LoggingHandler extends BasePointCut {

    @Autowired
    private JsonUtil jsonUtil;

    @Before("restController() && allMethod() && args(..)")
    public void logAfter(JoinPoint joinPoint) {
        Object request = Optional.ofNullable(joinPoint).filter(j -> j.getArgs().length > 0).map(l -> l.getArgs()[0]).orElse(null);
        log.info("REQUEST FORM: {}", jsonUtil.toJsonString(request));
    }

    @AfterReturning(pointcut = "restController() && allMethod()", returning = "response")
    public void logAfter(Object response) {
        if(response instanceof ResponseEntity){
            ResponseEntity<?> responseEntity = (ResponseEntity)response;
            log.info("RESPONSE REST: {}",jsonUtil.toJsonString(responseEntity.getBody()));
        }
    }

    @AfterReturning(pointcut = "controllerAdvice() && allMethod()", returning = "response")
    public void logAfterError(Object response) {
        if(response instanceof ResponseEntity){
            ResponseEntity<?> responseEntity = (ResponseEntity)response;
            log.info("RESPONSE ERROR: {}",jsonUtil.toJsonString(responseEntity.getBody()));
        }
    }
}
