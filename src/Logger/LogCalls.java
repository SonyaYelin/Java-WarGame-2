package Logger;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class LogCalls {
   @Pointcut("execution(* * *(*))")
    public void allMethods() {}
    
    @Before("allMethods")
    public void logCalls(JoinPoint theJoinPoint) {
    	String methodName = theJoinPoint.getSignature().getName();
    	System.out.println(methodName);
    }
}

