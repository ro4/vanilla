package io.github.ro4;

import io.github.ro4.check.Checker;
import org.junit.Assert;
import org.junit.Test;

public class CheckerTest {
    @Test
    public void testAlways() {
        Context ctx = new Context();
        Assert.assertTrue("must be true", Checker.ALWAYS_PASS.pass(ctx));
        Assert.assertFalse("must be false", Checker.ALWAYS_REJECT.pass(ctx));
    }
}
