package com.rossi.testmt940.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@Slf4j
@Component
public class LoggerInterceptor extends HandlerInterceptorAdapter {

    private static final String REQUEST = "REQUEST";
    private static final String ID = "ID";
    private static final String EXECUTION_TIME = "executionTime";
    private static final String LOG_APPEND = "logAppend";

    /**
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     *
     * Additional if you want to log all request use link below :
     * // https://www.baeldung.com/spring-http-logging
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Long millis = System.currentTimeMillis();
        request.setAttribute(EXECUTION_TIME,    millis);
        request.setAttribute(ID, millis);
        String requestURI = request.getRequestURI();
        String contextPath = request.getContextPath();
        String realPath = requestURI.substring(contextPath.length());
        request.setAttribute(EXECUTION_TIME,    millis);
        request.setAttribute(ID, millis);
        StringBuilder logAppend = new StringBuilder(realPath.replace("/","_"));
        Optional.ofNullable(request.getParameter("storeId"))
                .ifPresent(storeId ->{
                    logAppend.append("_");
                    logAppend.append(storeId);
                });
        logAppend.append("_");
        logAppend.append(millis);
        MDC.put(REQUEST,logAppend.toString().replaceFirst("_",""));
        request.setAttribute(LOG_APPEND, logAppend.toString());
        log.info(" :::::: START REQUEST :::::: ");
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        Long startTime = (Long) request.getAttribute(EXECUTION_TIME);
        String sessionID = request.getAttribute(ID).toString();
        MDC.put(REQUEST,request.getAttribute(LOG_APPEND).toString());
        log.info(" :::::: COMPLETE REQUEST for SessionID {} in {} ms", sessionID,(System.currentTimeMillis() - startTime));
        MDC.clear();
    }
}
