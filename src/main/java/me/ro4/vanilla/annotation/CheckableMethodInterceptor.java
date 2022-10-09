package me.ro4.vanilla.annotation;

import me.ro4.vanilla.CheckTemplate;
import me.ro4.vanilla.Context;
import me.ro4.vanilla.check.CheckResult;
import me.ro4.vanilla.check.ExceptionProvider;
import me.ro4.vanilla.constant.MagicMark;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.validation.BindingResult;

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
        Object[] args = methodInvocation.getArguments();
        BindingResult bindingResult = new CheckResult(methodInvocation.getMethod().getName());
        for (CheckRule checkRule : anno.value()) {
            Context ctx = new Context();
            for (int i = 0; i < args.length; i++) {
                ctx.setAttribute(MagicMark.ARGS_NAME + i, args[i]);
            }
            ctx.setAttribute(MagicMark.EXPRESSION, checkRule.expression());
            ctx.setAttribute(MagicMark.MESSAGE, checkRule.message());
            if (checkTemplate.check(ctx)) {
                continue;
            }
            bindingResult.reject(checkRule.key(), checkRule.message());
            if (stopOnFirstFailure) {
                break;
            }
        }
        if (bindingResult.hasErrors()) {
            RuntimeException throwable = exceptionProvider.produce(bindingResult);
            if (throwable != null) {
                throw throwable;
            }
        }
        return methodInvocation.proceed();
    }
}