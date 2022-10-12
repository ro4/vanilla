package io.github.ro4.check;

import io.github.ro4.Context;

public interface Checker {

    Checker ALWAYS_PASS = ctx -> true;

    Checker ALWAYS_REJECT = ctx -> false;

    boolean pass(Context ctx);
}
