package me.ro4.vanilla;

import me.ro4.vanilla.listener.CheckListener;
import me.ro4.vanilla.support.Context;
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
        checkTemplate.registerListener(new CheckListener() {
        });
        checkTemplate.check(new Context());
    }

}
