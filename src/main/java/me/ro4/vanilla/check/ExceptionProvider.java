package me.ro4.vanilla.check;

import org.springframework.validation.BindingResult;

public interface ExceptionProvider<T extends RuntimeException> {

    T produce(BindingResult result);
}
