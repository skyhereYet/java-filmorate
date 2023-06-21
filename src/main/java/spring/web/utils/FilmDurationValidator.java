package spring.web.utils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.Duration;

public class FilmDurationValidator implements ConstraintValidator<DurationPositiveChecker, Duration> {
    @Override
    public boolean isValid(Duration duration, ConstraintValidatorContext constraintValidatorContext) {
        if (duration.isNegative() || duration.isZero()) {
            return false;
        }
        return true;
    }
}
