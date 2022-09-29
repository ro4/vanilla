package me.ro4.vanilla.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
public @interface Checkable {
    CheckRule[] value() default {};
}
