package mate.academy.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;

public class FieldMatchValidator implements ConstraintValidator<FieldMatch, Object> {
    private String firstFieldName;
    private String secondFieldName;

    @Override
    public void initialize(FieldMatch constraintAnnotation) {
        firstFieldName = constraintAnnotation.first();
        secondFieldName = constraintAnnotation.second();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        try {
            final Object firstObj = getFieldValue(value, firstFieldName);
            final Object secondObj = getFieldValue(value, secondFieldName);
            return firstObj != null && firstObj.equals(secondObj);
        } catch (Exception e) {
            return false;
        }
    }

    private Object getFieldValue(Object value, String fieldName)
            throws NoSuchFieldException, IllegalAccessException {
        Field field = value.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(value);
    }
}
