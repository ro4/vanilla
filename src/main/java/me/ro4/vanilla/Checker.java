package me.ro4.vanilla;

import me.ro4.vanilla.support.Context;

public interface Checker {

    Checker ALWAYS_PASS = ctx -> true;

    Checker ALWAYS_REJECT = ctx -> false;

    boolean pass(Context ctx);
}
