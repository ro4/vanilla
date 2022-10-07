package me.ro4.vanilla;

import me.ro4.vanilla.listener.CheckListenerSupport;
import me.ro4.vanilla.support.Context;
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
        checkTemplate.registerListener(new CheckListenerSupport() {
        });
        checkTemplate.check(new Context());
        Checker checker = new SpELChecker();
        checkTemplate.setChecker(checker);
        Assert.assertEquals(checker, checkTemplate.getChecker());
    }

}
