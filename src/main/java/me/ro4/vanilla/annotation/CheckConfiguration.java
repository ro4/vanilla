package me.ro4.vanilla.annotation;

import me.ro4.vanilla.CheckTemplate;
import me.ro4.vanilla.SpELChecker;
import me.ro4.vanilla.listener.CheckListener;
import org.aopalliance.aop.Advice;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.aop.support.ComposablePointcut;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CheckConfiguration extends AbstractPointcutAdvisor implements InitializingBean, ApplicationContextAware {

    private Advice advice;

    private Pointcut pointcut;

    private ApplicationContext applicationContext;

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
        List<CheckListener> listeners = new ArrayList<>(applicationContext.getBeansOfType(CheckListener.class).values());
        CheckTemplate checkTemplate = new CheckTemplate();
        checkTemplate.setListeners(listeners.toArray(new CheckListener[0]));
        checkTemplate.setChecker(new SpELChecker());
        pointcut = new ComposablePointcut(new CheckableMethodMatcher());
        CheckableMethodInterceptor checkableMethodInterceptor = new CheckableMethodInterceptor();
        checkableMethodInterceptor.setCheckTemplate(checkTemplate);
        advice = checkableMethodInterceptor;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
