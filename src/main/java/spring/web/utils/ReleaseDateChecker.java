package spring.web.utils;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = {FilmReleaseDateValidator.class})
public @interface ReleaseDateChecker {
    String message() default "{Validation error: Release date earlier than 28.12.1895}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}