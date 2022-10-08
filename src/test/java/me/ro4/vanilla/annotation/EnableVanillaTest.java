package me.ro4.vanilla.annotation;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

public class EnableVanillaTest {

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
            Assert.assertTrue(e instanceof IllegalArgumentException);
        }
    }

    @Test
    public void testReadArgsExpression() {
        try {
            service.method2("hello1");
        } catch (Exception e) {
            Assert.assertEquals("hello there", e.getMessage());
        }
        service.method2("hello");
    }

    @Test
    public void testNotStopOnFirstFailed() {
        try {
            service.method3(1, 2);
        } catch (Exception e) {
            Assert.assertEquals("i1 > i2,i1 is zero", e.getMessage());
        }
    }

    @Test
    public void testCallBeanMethod() {
        service.method4();
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

        @Checkable(@CheckRule(expression = "#p[0] == 'hello'", message = "hello there"))
        public void method2(String par) {

        }

        @Checkable(stopOnFirstFailure = false,
                value = {
                        @CheckRule(expression = "#p[0] > #p[1]", message = "i1 > i2"),
                        @CheckRule(expression = "#p[0] == 0", message = "i1 is zero")
                }
        )
        public void method3(int i1, int i2) {

        }


        @Checkable(@CheckRule(expression = "@service.beanMethod()"))
        public void method4() {

        }

        public boolean beanMethod() {
            return true;
        }
    }


}
