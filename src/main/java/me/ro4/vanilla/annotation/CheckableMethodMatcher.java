package me.ro4.vanilla.annotation;

import org.springframework.aop.MethodMatcher;

import java.lang.reflect.Method;

public class CheckableMethodMatcher implements MethodMatcher {

    @Override
    public boolean matches(Method method, Class<?> targetClass) {
        return method.isAnnotationPresent(Checkable.class);
    }

    @Override
    public boolean isRuntime() {
        return false;
    }

    @Override
    public boolean matches(Method method, Class<?> targetClass, Object... args) {
        return matches(method, targetClass);
    }
}