package me.ro4.vanilla.annotation;

import me.ro4.vanilla.CheckTemplate;
import me.ro4.vanilla.check.ExceptionProvider;
import me.ro4.vanilla.constant.MagicMark;
import me.ro4.vanilla.Context;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.util.ArrayList;
import java.util.List;

public class CheckableMethodInterceptor implements MethodInterceptor {

    private CheckTemplate checkTemplate;

    private ExceptionProvider<?> exceptionProvider;

    public void setCheckTemplate(CheckTemplate checkTemplate) {
        this.checkTemplate = checkTemplate;
    }

    public void setExceptionProvider(ExceptionProvider<?> exceptionProvider) {
        this.exceptionProvider = exceptionProvider;
    }

    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        Checkable anno = methodInvocation.getMethod().getAnnotation(Checkable.class);
        boolean stopOnFirstFailure = anno.stopOnFirstFailure();
        List<Context> failed = new ArrayList<>();
        Object[] args = methodInvocation.getArguments();
        for (CheckRule checkRule : anno.value()) {
            Context ctx = new Context();
            ctx.setAttribute(MagicMark.ARGS_NAME, args);
            ctx.setAttribute(MagicMark.EXPRESSION, checkRule.expression());
            ctx.setAttribute(MagicMark.MESSAGE, checkRule.message());
            if (checkTemplate.check(ctx)) {
                continue;
            }
            failed.add(ctx);
            if (stopOnFirstFailure) {
                break;
            }
        }
        if (!failed.isEmpty()) {
            Throwable throwable = exceptionProvider.produce(failed);
            if (throwable != null) {
                throw throwable;
            }
        }
        return methodInvocation.proceed();
    }
}