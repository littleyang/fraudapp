package com.fraud.aop;

import com.sct.context.interceptors.CommonResultInterceptor;
import com.sct.context.interceptors.InvocationDigestInterceptor;
import com.sct.context.interceptors.InvocationTraceInterceptor;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * @author zhouyang
 * @version 1.0.0
 * @description:
 * @since 2024/4/21
 */

@Aspect
@Component
@Configuration
public class LarkPassAopConfig {


    /**
     * RPC API通用返回结果封装
     * @return
     */
    @Bean(name = "apiCommonResultInterceptor")
    public CommonResultInterceptor createApiCommonResultInterceptor(){
        return new CommonResultInterceptor();
    }


    /**
     * Rest API 通用结果封装
     * @return
     */
    @Bean(name = "restResultInterceptor")
    public CommonResultInterceptor createRestResultInterceptor(){
        return new CommonResultInterceptor(true);
    }

    /**
     * 调用链路结果封装
     * @return
     */
    @Bean(name = "traceInterceptor")
    public InvocationTraceInterceptor createInvocationTraceInterceptor(){
        return new InvocationTraceInterceptor();
    }

    /**
     * 摘要日志封装
     * @return
     */
    @Bean(name = "apiDigestInterceptor")
    public InvocationDigestInterceptor createApiDigestInterceptor(){
        InvocationDigestInterceptor interceptor = new InvocationDigestInterceptor("api-digest-log");
        interceptor.setAppName("app");
        return interceptor;
    }

    /**
     * RPC API result摘要日志切面配置
     * @return
     */
    @Bean
    public Advisor apiPointcutAdvisor(){
        AspectJExpressionPointcut apiPointcut = new AspectJExpressionPointcut();
        apiPointcut.setExpression("execution(* com.sct.app.api..*.*(..))");
        return new DefaultPointcutAdvisor(apiPointcut,createApiDigestInterceptor());
    }

    @Bean
    public Advisor apiResultPointcutAdvisor(){
        AspectJExpressionPointcut apiPointcut = new AspectJExpressionPointcut();
        apiPointcut.setExpression("execution(* com.sct.app.api..*.*(..))");
        return new DefaultPointcutAdvisor(apiPointcut,createApiCommonResultInterceptor());
    }




    @Bean(name = "bizDigestInterceptor")
    public InvocationDigestInterceptor createBizDigestInterceptor(){
        InvocationDigestInterceptor interceptor = new InvocationDigestInterceptor("biz-digest-log");
        interceptor.setAppName("app");
        return interceptor;
    }

    @Bean
    public Advisor bizPointcutAdvisor(){
        AspectJExpressionPointcut apiPointcut = new AspectJExpressionPointcut();
        apiPointcut.setExpression("execution(* com.sct.app.biz..*.*(..))");
        return new DefaultPointcutAdvisor(apiPointcut,createBizDigestInterceptor());
    }

    @Bean
    public Advisor bizPointcutTraceAdvisor(){
        AspectJExpressionPointcut apiPointcut = new AspectJExpressionPointcut();
        apiPointcut.setExpression("execution(* com.sct.app.biz..*.*(..))");
        return new DefaultPointcutAdvisor(apiPointcut,createInvocationTraceInterceptor());
    }


    @Bean(name = "dalDigestInterceptor")
    public InvocationDigestInterceptor createDalDigestInterceptor(){
        InvocationDigestInterceptor interceptor = new InvocationDigestInterceptor("dal-digest-log");
        interceptor.setAppName("app");
        return interceptor;
    }

    @Bean
    public Advisor dalPointcutAdvisor(){
        AspectJExpressionPointcut apiPointcut = new AspectJExpressionPointcut();
        apiPointcut.setExpression("execution(* com.sct.app.repository.app.mapper.*.*(..))");
        return new DefaultPointcutAdvisor(apiPointcut,createDalDigestInterceptor());
    }


    /**
     * API call摘要配置
     * @return
     */
    @Bean(name = "salDigestInterceptor")
    public InvocationDigestInterceptor createSalDigestInterceptor(){
        InvocationDigestInterceptor interceptor = new InvocationDigestInterceptor("sal-digest-log");
        interceptor.setAppName("app");
        return interceptor;
    }

    @Bean
    public Advisor salPointcutAdvisor(){
        AspectJExpressionPointcut apiPointcut = new AspectJExpressionPointcut();
        apiPointcut.setExpression("execution(* com.sct.app.sal.*.*(..)))");
        return new DefaultPointcutAdvisor(apiPointcut,createSalDigestInterceptor());
    }


    @Bean
    public Advisor restApiResultPointcutAdvisor(){
        AspectJExpressionPointcut apiPointcut = new AspectJExpressionPointcut();
        apiPointcut.setExpression("@within(org.springframework.web.bind.annotation.RestController) " +
                "and ( @annotation(org.springframework.web.bind.annotation.RequestMapping) " +
                "or @annotation(org.springframework.web.bind.annotation.GetMapping) " +
                "or @annotation(org.springframework.web.bind.annotation.PostMapping) " +
                "or @annotation(org.springframework.web.bind.annotation.PutMapping) " +
                "or @annotation(org.springframework.web.bind.annotation.DeleteMapping))");
        return new DefaultPointcutAdvisor(apiPointcut,createRestResultInterceptor());
    }

}
