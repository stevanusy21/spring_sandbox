package com.springboot.sandbox.common.validator;

import com.springboot.sandbox.common.validator.annotation.IndonesianPhoneNumber;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class IndonesianPhoneNumberValidator
        implements ConstraintValidator<IndonesianPhoneNumber, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return true;
        }
        return value.matches("^0\\d{1,12}$")
                || value.matches("^\\+62\\d{1,12}$");
    }

}
