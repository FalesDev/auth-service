package co.com.pragma.model.user;

import co.com.pragma.model.exception.ValidationException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class UserValidation {

    private static final Pattern EMAIL_REGEX =
            Pattern.compile("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");

    public void validateUser(User user) {
        List<ValidationException.FieldError> errors = new ArrayList<>();

        if (user.getFirstName() == null || user.getFirstName() .trim().isEmpty()) {
            errors.add(new ValidationException.FieldError("firstName", "First name is required"));
        }

        if (user.getLastName() == null || user.getLastName() .trim().isEmpty()) {
            errors.add(new ValidationException.FieldError("lastName", "First name is required"));
        }

        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            errors.add(new ValidationException.FieldError("email", "Email is required"));
        } else if (!EMAIL_REGEX.matcher(user.getEmail()).matches()) {
            errors.add(new ValidationException.FieldError("email", "Email format is invalid"));
        }

        if (user.getBaseSalary() == null) {
            errors.add(new ValidationException.FieldError("baseSalary", "Base salary is required"));
        } else if (user.getBaseSalary() < 0 || user.getBaseSalary() > 15000000) {
            errors.add(new ValidationException.FieldError("baseSalary", "Base salary must be between 0 and 15,000,000"));
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }
}
