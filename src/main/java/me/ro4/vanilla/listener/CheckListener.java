package me.ro4.vanilla.listener;

import me.ro4.vanilla.support.Context;

public interface CheckListener {

    default void onOpen(Context ctx) {

    }

    default void onPass(Context ctx) {

    }

    default void onReject(Context ctx) {

    }

    default void onError(Context ctx, Throwable throwable) {

    }
}
