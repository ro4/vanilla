package io.github.ro4.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckRule {
    String expression() default "";

    String andExpression() default "";

    String orExpression() default "";

    String message() default "failed";

    String field() default "";
}
