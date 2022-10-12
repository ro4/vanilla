package io.github.ro4;

import io.github.ro4.check.Checker;
import io.github.ro4.check.SpELChecker;
import io.github.ro4.constant.MagicMark;
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
