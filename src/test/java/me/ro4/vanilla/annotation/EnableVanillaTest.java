package me.ro4.vanilla.annotation;

import me.ro4.vanilla.listener.CheckListener;
import me.ro4.vanilla.listener.CheckListenerSupport;
import me.ro4.vanilla.support.Context;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

public class EnableVanillaTest {

    @Test
    public void testProxy() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(
                TestProxyConfiguration.class, ListenerConfig.class
        );
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

    @Configuration
    protected static class ListenerConfig {
        @Bean
//        @Order(100)
        public CheckListener checkListener1() {
            return new Listener1();
        }

        @Bean
//        @Order(3)
        public CheckListener checkListener2() {
            return new Listener2();
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

    @Order(1000)
    protected static class Listener1 extends CheckListenerSupport {
        @Override
        public void onPass(Context ctx) {
            System.out.println("pass");
        }

    }

    @Order(100)
    protected static class Listener2 extends CheckListenerSupport {
        @Override
        public void onReject(Context ctx) {
            System.out.println("reject");
        }
    }


}
