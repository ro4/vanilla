package io.github.ro4.check;

import org.springframework.validation.AbstractBindingResult;

public class CheckResult extends AbstractBindingResult {

    public CheckResult(String objectName) {
        super(objectName);
    }

    @Override
    public Object getTarget() {
        return null;
    }

    @Override
    protected Object getActualFieldValue(String field) {
        return null;
    }
}
