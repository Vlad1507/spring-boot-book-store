package com.store.bookstore.validation.match;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.HashMap;
import java.util.Map;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public class FieldMatchValidator implements ConstraintValidator<FieldMatch, Object> {

    public static final SpelExpressionParser EXPRESSION_PARSER = new SpelExpressionParser();
    private String[] fieldNames;

    @Override
    public void initialize(FieldMatch constraintAnnotation) {
        this.fieldNames = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null) {
            return false;
        }
        try {
            EvaluationContext evaluationContext = new StandardEvaluationContext(value);
            Map<String, Object> fieldValues = new HashMap<>();
            for (String fieldName : fieldNames) {
                fieldValues.put(fieldName,
                        EXPRESSION_PARSER.parseExpression(fieldName).getValue(evaluationContext));
            }
            Object fieldValue1 = fieldValues.get(fieldNames[0]);
            Object fieldValue2 = fieldValues.get(fieldNames[1]);
            if (fieldValue1 == null || fieldValue2 == null) {
                constraintValidatorContext.disableDefaultConstraintViolation();
                constraintValidatorContext
                        .buildConstraintViolationWithTemplate("Both fields "
                                + fieldNames[0] + " and " + fieldNames[1] + " are required.")
                        .addConstraintViolation();
                return false;
            }
            if (!fieldValue1.equals(fieldValue2)) {
                constraintValidatorContext.disableDefaultConstraintViolation();
                constraintValidatorContext
                        .buildConstraintViolationWithTemplate("Fields "
                                + fieldNames[0] + " and " + fieldNames[1] + " do not match")
                        .addConstraintViolation();
            }
            if (fieldValue1.equals(fieldValue2)) {
                return true;
            }
        } catch (Exception e) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext
                    .buildConstraintViolationWithTemplate("Password fields validation failed")
                    .addConstraintViolation();
            throw new RuntimeException("Failed fields validation", e);
        }
        return false;
    }
}
