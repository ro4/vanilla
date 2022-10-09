package me.ro4.vanilla.check;

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
