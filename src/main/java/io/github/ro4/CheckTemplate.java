package io.github.ro4;

import io.github.ro4.check.Checker;

public class CheckTemplate {
    private volatile Checker checker;

    public boolean check(Context ctx) {
        return checker.pass(ctx);
    }

    public void setChecker(Checker checker) {
        this.checker = checker;
    }

    public Checker getChecker() {
        return checker;
    }

}
