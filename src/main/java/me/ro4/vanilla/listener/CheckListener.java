package me.ro4.vanilla.listener;

import me.ro4.vanilla.support.Context;

public interface CheckListener {

    void onOpen(Context ctx);

    void onPass(Context ctx);

    void onReject(Context ctx);

    void onError(Context ctx, Throwable throwable);
}
