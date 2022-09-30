package me.ro4.vanilla;

import me.ro4.vanilla.listener.CheckListener;
import me.ro4.vanilla.support.Context;
import org.junit.Test;

public class CheckTemplateTest {

    @Test
    public void testTemplateRegular() {
        CheckTemplate checkTemplate = new CheckTemplate();
        checkTemplate.setChecker(new SpELChecker());
        checkTemplate.registerListener(new CheckListener() {
        });
        checkTemplate.check(new Context());
    }

}
