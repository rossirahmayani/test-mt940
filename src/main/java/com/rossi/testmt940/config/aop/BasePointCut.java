package com.rossi.testmt940.config.aop;
import org.aspectj.lang.annotation.Pointcut;


public class BasePointCut {

    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void restController() {
        // Do nothing because of X and Y.
    }

    @Pointcut("within(@org.springframework.web.bind.annotation.ControllerAdvice *)")
    public void controllerAdvice() {
        // Do nothing because of X and Y.
    }

    @Pointcut("execution(* *.*(..))")
    protected void allMethod() {
        // Do nothing because of X and Y.
    }

    @Pointcut("execution(public * *(..))")
    protected void loggingPublicOperation() {
        // Do nothing because of X and Y.
    }

    @Pointcut("execution(* *.*(..))")
    protected void loggingAllOperation() {
        // Do nothing because of X and Y.
    }

    @Pointcut("within(com.rossi.testmt940..*)")
    protected void logAnyFunctionWithinResource() {
        // Do nothing because of X and Y.
    }



}
