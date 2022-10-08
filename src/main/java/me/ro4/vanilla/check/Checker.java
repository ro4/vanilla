package me.ro4.vanilla.check;

import me.ro4.vanilla.Context;

public interface Checker {

    Checker ALWAYS_PASS = ctx -> true;

    Checker ALWAYS_REJECT = ctx -> false;

    boolean pass(Context ctx);
}
