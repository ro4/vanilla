package me.ro4.vanilla.annotation;

import me.ro4.vanilla.CheckTemplate;
import me.ro4.vanilla.constant.MagicMark;
import me.ro4.vanilla.support.Context;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

public class CheckableMethodInterceptor implements MethodInterceptor {

    private CheckTemplate checkTemplate;

    public void setCheckTemplate(CheckTemplate checkTemplate) {
        this.checkTemplate = checkTemplate;
    }

    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        Checkable anno = methodInvocation.getMethod().getAnnotation(Checkable.class);
        Object[] args = methodInvocation.getArguments();
        Context ctx = new Context();
        ctx.setAttribute(MagicMark.ARGS_NAME, args);
        for (CheckRule checkRule : anno.value()) {
            ctx.setAttribute(MagicMark.EXPRESSION, checkRule.expression());
            ctx.setAttribute(MagicMark.MESSAGE, checkRule.message());
            checkTemplate.check(ctx);
        }
        return methodInvocation.proceed();
    }
}