package me.ro4.vanilla;

import me.ro4.vanilla.constant.MagicMark;
import me.ro4.vanilla.support.Context;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public class SpELChecker implements Checker {
    private static final SpelExpressionParser PARSER = new SpelExpressionParser();

    @Override
    public boolean pass(Context ctx) {
        if (!ctx.hasAttribute(MagicMark.EXPRESSION)) {
            throw new ExpressionException("expression not found");
        }
        StandardEvaluationContext spELCtx = new StandardEvaluationContext();
        for (String s : ctx.attributeNames()) {
            spELCtx.setVariable(s, ctx.getAttribute(s));
        }
        String expression = (String) ctx.getAttribute(MagicMark.EXPRESSION);
        assert expression != null;
        return Boolean.TRUE.equals(PARSER.parseExpression(expression).getValue(spELCtx, Boolean.TYPE));
    }
}
