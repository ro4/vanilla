package io.github.ro4.check;

import io.github.ro4.ExpressionException;
import io.github.ro4.constant.MagicMark;
import io.github.ro4.Context;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public class SpELChecker implements Checker {
    private static final SpelExpressionParser PARSER = new SpelExpressionParser();

    private BeanFactory beanFactory = null;

    public SpELChecker() {

    }

    public SpELChecker(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override
    public boolean pass(Context ctx) {
        if (!ctx.hasAttribute(MagicMark.EXPRESSION)) {
            throw new ExpressionException("expression not found");
        }
        StandardEvaluationContext spELCtx = new StandardEvaluationContext();
        // ugly design, temporary work
        if (null != beanFactory) {
            spELCtx.setBeanResolver(new BeanFactoryResolver(beanFactory));
        }
        for (String s : ctx.attributeNames()) {
            spELCtx.setVariable(s, ctx.getAttribute(s));
        }
        String expression = (String) ctx.getAttribute(MagicMark.EXPRESSION);
        assert expression != null;
        return Boolean.TRUE.equals(PARSER.parseExpression(expression).getValue(spELCtx, Boolean.TYPE));
    }
}
