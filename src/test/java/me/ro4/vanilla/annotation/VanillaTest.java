package me.ro4.vanilla.annotation;

import me.ro4.vanilla.CheckFailedException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

public class VanillaTest {

    private Service service;

    @Before
    public void setUp() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(TestProxyConfiguration.class);
        service = context.getBean("service", Service.class);
    }

    @Test
    public void testProxy() {
        Assert.assertTrue(AopUtils.isAopProxy(service));
    }


    @Test
    public void testSimpleExpression() {
        try {
            service.methodWithAnno();
        } catch (Exception e) {
            Assert.assertTrue(e instanceof CheckFailedException);
        }
    }

    @Test
    public void testReadArgsExpression() {
        try {
            service.method2("hello1");
        } catch (CheckFailedException e) {
            Assert.assertTrue(e.getMessage().contains("hello there"));
        }
        service.method2("hello");
    }

    @Test
    public void testNotStopOnFirstFailed() {
        try {
            service.method3(1, 2);
        } catch (CheckFailedException e) {
            Assert.assertTrue(
                    e.getMessage().contains("i1 > i2")
                            && e.getMessage().contains("i1 is zero")
            );
        }
    }

    @Test
    public void testCallBeanMethod() {
        service.method4();
        service.method5(2, 1);
    }


    @Configuration
    @EnableVanilla
    protected static class TestProxyConfiguration {

        @Bean
        public Service service() {
            return new Service();
        }
    }

    @SuppressWarnings("unused")
    protected static class Service {
        @Checkable({@CheckRule(expression = "true"), @CheckRule(expression = "false")})
        public void methodWithAnno() {

        }

        @Checkable(@CheckRule(expression = "#p0 == 'hello'", message = "hello there"))
        public void method2(String par) {

        }

        @Checkable(
                value = {
                        @CheckRule(expression = "#p0 > #p1", message = "i1 > i2"),
                        @CheckRule(expression = "#p0 == 0", message = "i1 is zero")
                }
        )
        public void method3(int i1, int i2) {

        }

        @Checkable(@CheckRule(expression = "@service.beanMethod()"))
        public void method4() {

        }


        @Checkable(@CheckRule(expression = "@service.i1GtI2(#p0, #p1)"))
        public void method5(int i1, int i2) {

        }

        public boolean beanMethod() {
            return true;
        }

        public boolean i1GtI2(int i1, int i2) {
            return i1 > i2;
        }
    }


}
