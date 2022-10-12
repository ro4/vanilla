package io.github.ro4.check;

import io.github.ro4.CheckFailedException;
import org.springframework.validation.BindingResult;


public class DefaultExceptionProvider implements ExceptionProvider<CheckFailedException> {

    @Override
    public CheckFailedException produce(BindingResult result) {
        return new CheckFailedException(result);
    }
}
