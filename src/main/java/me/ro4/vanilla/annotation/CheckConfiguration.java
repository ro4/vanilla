package me.ro4.vanilla.annotation;

import me.ro4.vanilla.CheckTemplate;
import me.ro4.vanilla.check.DefaultExceptionProvider;
import me.ro4.vanilla.check.ExceptionProvider;
import me.ro4.vanilla.check.SpELChecker;
import org.aopalliance.aop.Advice;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.aop.support.ComposablePointcut;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Role;
import org.springframework.stereotype.Component;

@Role(BeanDefinition.ROLE_SUPPORT)
@Component
public class CheckConfiguration extends AbstractPointcutAdvisor implements InitializingBean, BeanFactoryAware {

    private Advice advice;

    private Pointcut pointcut;

    private BeanFactory beanFactory;

    @Override
    public Pointcut getPointcut() {
        return pointcut;
    }

    @Override
    public Advice getAdvice() {
        return advice;
    }

    @Override
    public void afterPropertiesSet() {
        pointcut = new ComposablePointcut(new CheckableMethodMatcher());
        CheckableMethodInterceptor checkableMethodInterceptor = new CheckableMethodInterceptor();
        checkableMethodInterceptor.setCheckTemplate(buildCheckTemplate());
        checkableMethodInterceptor.setExceptionProvider(buildExceptionProvider());
        advice = checkableMethodInterceptor;
    }

    protected CheckTemplate buildCheckTemplate() {
        CheckTemplate checkTemplate = new CheckTemplate();
        checkTemplate.setChecker(new SpELChecker());
        return checkTemplate;
    }

    protected ExceptionProvider<?> buildExceptionProvider() {
        ExceptionProvider<?> exceptionProvider;
        try {
            exceptionProvider = beanFactory.getBean(ExceptionProvider.class);
        } catch (NoSuchBeanDefinitionException e) {
            exceptionProvider = new DefaultExceptionProvider();
        }
        return exceptionProvider;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
}
