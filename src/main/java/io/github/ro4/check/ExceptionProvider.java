package io.github.ro4.check;

import org.springframework.validation.BindingResult;

public interface ExceptionProvider<T extends RuntimeException> {

    T produce(BindingResult result);
}
