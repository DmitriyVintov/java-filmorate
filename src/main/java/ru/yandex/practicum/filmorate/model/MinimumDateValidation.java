package ru.yandex.practicum.filmorate.model;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class MinimumDateValidation implements ConstraintValidator<MinimumDate, LocalDate> {
    private LocalDate valueDate;

    @Override
    public void initialize(MinimumDate constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        valueDate = LocalDate.parse(constraintAnnotation.value());
    }

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        return value == null || !value.isBefore(valueDate);
    }
}