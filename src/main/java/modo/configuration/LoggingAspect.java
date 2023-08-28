package modo.configuration;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;

@Log4j2
@Component
@Aspect
public class LoggingAspect {
//    @Pointcut("execution(* modo.service..*(..))")
//    private void serviceTarget() {
//    }

    @Pointcut("execution(* modo.repository..*(..))")
    private void repositoryTarget() {
    }

    @Pointcut("execution(* modo.controller..*(..))")
    private void controllerTarget() {
    }

    @Around("controllerTarget()")
    public Object controllerExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String classpath = joinPoint.getSignature().getDeclaringType().getName();
        try {
            return joinPoint.proceed();
        } finally {
            long finish = System.currentTimeMillis();
            Signature signature = joinPoint.getSignature();
            String className = joinPoint.getTarget().getClass().getSimpleName();
            log.debug("{}.{}({}) : {}ms", classpath, signature.getName(), Arrays.toString(joinPoint.getArgs()), (finish - start));
            log.info("{} {} {}ms", request.getMethod(), request.getRequestURI(), (finish - start));
            if ((finish - start) > 1000) {
                log.warn("Below Http Execution Time is Over 1000ms!");
                log.warn("{} {} : {}ms", request.getMethod(), request.getRequestURI(), (finish - start));
            }
        }
    }

//    @Around("serviceTarget()")
//    public Object serviceExecution(ProceedingJoinPoint joinPoint) throws Throwable {
//        long start = System.currentTimeMillis();
//        try {
//            return joinPoint.proceed();
//        } finally {
//            long finish = System.currentTimeMillis();
//            Signature signature = joinPoint.getSignature();
//            String className = joinPoint.getTarget().getClass().getSimpleName();
//            log.debug("{}.{}({}) : {}ms", className, signature.getName(), Arrays.toString(joinPoint.getArgs()), (finish - start));
//        }
//    }

    @Around("repositoryTarget()")
    public Object dataAccessExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        try {
            return joinPoint.proceed();
        } finally {
            long finish = System.currentTimeMillis();
            Signature signature = joinPoint.getSignature();
            String className = joinPoint.getTarget().getClass().getSimpleName();
            log.debug("{}.{}({}) : {}ms", className, signature.getName(), Arrays.toString(joinPoint.getArgs()), (finish - start));
        }
    }

}
