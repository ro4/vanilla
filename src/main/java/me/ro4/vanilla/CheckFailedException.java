package me.ro4.vanilla;

import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;


@SuppressWarnings("all")
public class CheckFailedException extends RuntimeException {


    private final BindingResult bindingResult;

    public CheckFailedException(BindingResult bindingResult) {
        Assert.notNull(bindingResult, "BindingResult must not be null");
        this.bindingResult = bindingResult;
    }

    public final BindingResult getBindingResult() {
        return this.bindingResult;
    }


    /**
     * Returns diagnostic information about the errors held in this object.
     */
    @Override
    public String getMessage() {
        return this.bindingResult.toString();
    }

    @Override
    public boolean equals(@Nullable Object other) {
        return (this == other || this.bindingResult.equals(other));
    }

    @Override
    public int hashCode() {
        return this.bindingResult.hashCode();
    }
}
