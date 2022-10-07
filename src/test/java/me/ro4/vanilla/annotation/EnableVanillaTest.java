package me.ro4.vanilla.annotation;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

public class EnableVanillaTest {

    @Test
    public void testProxy() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(TestProxyConfiguration.class);
        Service service = context.getBean("service", Service.class);
        Assert.assertTrue(AopUtils.isAopProxy(service));
        service.methodWithAnno();
    }

    @Configuration
    @EnableVanilla
    protected static class TestProxyConfiguration {

        @Bean
        public Service service() {
            return new Service();
        }
    }

    protected static class Service {
        @Checkable({
                @CheckRule(expression = "true"),
                @CheckRule(expression = "false")
        })
        public void methodWithAnno() {

        }
    }
}
