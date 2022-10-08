package me.ro4.vanilla.check;

import me.ro4.vanilla.Context;

import java.util.List;

public interface ExceptionProvider<T extends Throwable> {

    T produce(List<Context> contexts);
}
