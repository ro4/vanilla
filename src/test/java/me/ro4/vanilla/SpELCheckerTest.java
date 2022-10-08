package me.ro4.vanilla;

import me.ro4.vanilla.check.Checker;
import me.ro4.vanilla.check.SpELChecker;
import me.ro4.vanilla.constant.MagicMark;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class SpELCheckerTest {
    private Checker checker;

    private Context ctx;

    @Before
    public void setUp() {
        checker = new SpELChecker();
        ctx = new Context();
        ctx.setAttribute(MagicMark.ARGS_NAME, "hello");
    }

    @Test
    public void testRegular() {
        ctx.setAttribute(MagicMark.EXPRESSION, "true");
        Assert.assertTrue(checker.pass(ctx));
        ctx.setAttribute(MagicMark.EXPRESSION, "false");
        Assert.assertFalse(checker.pass(ctx));
        ctx.setAttribute(MagicMark.EXPRESSION, " 1 != 2");
        Assert.assertTrue(checker.pass(ctx));
    }

    @Test
    public void testNoExpression() {
        ctx.removeAttribute(MagicMark.EXPRESSION);
        Assert.assertThrows(ExpressionException.class, () -> checker.pass(ctx));
    }

    @Test
    public void testArgs() {
        Object[] args = new Object[3];
        args[0] = 1;
        args[1] = 2;
        args[2] = 3;
        ctx.setAttribute(MagicMark.ARGS_NAME, args);
        ctx.setAttribute(MagicMark.EXPRESSION, "#p[1] > #p[0]");
        Assert.assertTrue(checker.pass(ctx));
        ctx.setAttribute(MagicMark.EXPRESSION, "#p[1] > #p[2]");
        Assert.assertFalse(checker.pass(ctx));
    }
}
