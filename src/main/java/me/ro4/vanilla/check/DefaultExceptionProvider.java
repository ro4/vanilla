package me.ro4.vanilla.check;

import me.ro4.vanilla.CheckFailedException;
import org.springframework.validation.BindingResult;


public class DefaultExceptionProvider implements ExceptionProvider<CheckFailedException> {

    @Override
    public CheckFailedException produce(BindingResult result) {
        return new CheckFailedException(result);
    }
}
