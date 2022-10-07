package me.ro4.vanilla;

import me.ro4.vanilla.listener.CheckListener;
import me.ro4.vanilla.support.Context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CheckTemplate {
    private volatile CheckListener[] listeners = new CheckListener[0];
    private volatile Checker checker;

    public void check(Context ctx) {
        doOnOpen(ctx);
        Boolean result = null;
        try {
            result = checker.pass(ctx);
        } catch (Throwable throwable) {
            doOnError(ctx, throwable);
        }
        if (result != null) {
            if (result) {
                doOnPass(ctx);
            } else {
                doOnReject(ctx);
            }
        }
    }

    public void setChecker(Checker checker) {
        this.checker = checker;
    }

    @SuppressWarnings("unused")
    public Checker getChecker() {
        return checker;
    }

    public void setListeners(CheckListener[] listeners) {
        this.listeners = listeners.clone();
    }

    public void registerListener(CheckListener listener) {
        registerListener(listener, this.listeners.length);
    }

    public void registerListener(CheckListener listener, int index) {
        List<CheckListener> list = new ArrayList<>(Arrays.asList(this.listeners));
        if (index >= list.size()) {
            list.add(listener);
        } else {
            list.add(index, listener);
        }
        this.listeners = list.toArray(new CheckListener[0]);
    }

    protected void doOnOpen(Context ctx) {
        for (CheckListener listener : listeners) {
            listener.onOpen(ctx);
        }
    }

    protected void doOnPass(Context ctx) {
        for (CheckListener listener : listeners) {
            listener.onPass(ctx);
        }
    }

    protected void doOnReject(Context ctx) {
        for (int i = this.listeners.length; i-- > 0; ) {
            this.listeners[i].onReject(ctx);
        }
    }

    protected void doOnError(Context ctx, Throwable throwable) {
        for (int i = this.listeners.length; i-- > 0; ) {
            this.listeners[i].onError(ctx, throwable);
        }
    }
}
