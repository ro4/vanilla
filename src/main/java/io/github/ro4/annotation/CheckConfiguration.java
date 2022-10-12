package io.github.ro4.annotation;

import io.github.ro4.CheckTemplate;
import io.github.ro4.check.DefaultExceptionProvider;
import io.github.ro4.check.ExceptionProvider;
import io.github.ro4.check.SpELChecker;
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

    private transient Advice advice;

    private transient Pointcut pointcut;

    private transient BeanFactory beanFactory;

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
        checkTemplate.setChecker(new SpELChecker(beanFactory));
        return checkTemplate;
    }

    protected ExceptionProvider<?> buildExceptionProvider() {
        try {
            return beanFactory.getBean(ExceptionProvider.class);
        } catch (NoSuchBeanDefinitionException e) {
            return new DefaultExceptionProvider();
        }
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
}
