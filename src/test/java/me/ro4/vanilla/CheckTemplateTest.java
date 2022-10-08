package me.ro4.vanilla;

import me.ro4.vanilla.check.Checker;
import me.ro4.vanilla.check.SpELChecker;
import me.ro4.vanilla.constant.MagicMark;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CheckTemplateTest {

    private CheckTemplate checkTemplate;

    @Before
    public void setUp() {
        checkTemplate = new CheckTemplate();
        checkTemplate.setChecker(new SpELChecker());
    }

    @Test
    public void testTemplateRegular() {
        Context ctx = new Context();
        ctx.setAttribute(MagicMark.EXPRESSION, "true");
        checkTemplate.check(ctx);
        Checker checker = new SpELChecker();
        checkTemplate.setChecker(checker);
        Assert.assertEquals(checker, checkTemplate.getChecker());
    }

}
