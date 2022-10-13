package io.github.ro4.annotation;

import io.github.ro4.CheckTemplate;
import io.github.ro4.Context;
import io.github.ro4.check.CheckResult;
import io.github.ro4.check.ExceptionProvider;
import io.github.ro4.constant.MagicMark;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.util.ObjectUtils;
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
            ctx.setAttribute(MagicMark.EXPRESSION, buildExpression(checkRule));
            ctx.setAttribute(MagicMark.MESSAGE, checkRule.message());
            if (checkTemplate.check(ctx)) {
                continue;
            }
            bindingResult.reject(checkRule.field(), checkRule.message());
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

    protected String buildExpression(CheckRule checkRule) {
        String expression = checkRule.expression();

        if (!ObjectUtils.isEmpty(checkRule.andExpression())) {
            expression = String.format("(%s) && (%s)", expression, checkRule.andExpression());
        }

        if (!ObjectUtils.isEmpty(checkRule.orExpression())) {
            expression = String.format("(%s) || (%s)", expression, checkRule.orExpression());
        }
        return expression;
    }
}