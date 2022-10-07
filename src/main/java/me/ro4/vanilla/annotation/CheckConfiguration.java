package me.ro4.vanilla.annotation;

import me.ro4.vanilla.CheckTemplate;
import me.ro4.vanilla.SpELChecker;
import me.ro4.vanilla.listener.CheckListener;
import org.aopalliance.aop.Advice;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.aop.support.ComposablePointcut;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Role;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

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
        CheckTemplate checkTemplate = new CheckTemplate();
        if (beanFactory instanceof ListableBeanFactory) {
            List<CheckListener> listeners = new ArrayList<>(((ListableBeanFactory) beanFactory)
                    .getBeansOfType(CheckListener.class).values());
            AnnotationAwareOrderComparator.sort(listeners);
            checkTemplate.setListeners(listeners.toArray(new CheckListener[0]));
        }
        checkTemplate.setChecker(new SpELChecker());
        pointcut = new ComposablePointcut(new CheckableMethodMatcher());
        CheckableMethodInterceptor checkableMethodInterceptor = new CheckableMethodInterceptor();
        checkableMethodInterceptor.setCheckTemplate(checkTemplate);
        advice = checkableMethodInterceptor;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
}
