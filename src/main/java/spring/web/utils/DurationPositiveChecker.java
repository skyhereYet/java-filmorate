package spring.web.utils;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = {FilmDurationValidator.class})

public @interface DurationPositiveChecker {
    String message() default "{Validation error: Duration negative or zero}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
