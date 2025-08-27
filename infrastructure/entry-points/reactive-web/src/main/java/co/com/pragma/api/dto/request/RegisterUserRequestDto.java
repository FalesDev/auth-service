package co.com.pragma.api.dto.request;

import jakarta.validation.constraints.*;

public record RegisterUserRequestDto(
        @NotBlank(message = "FirstName is required")
        String firstName,
        @NotBlank(message = "LastName is required")
        String lastName,
        @Pattern(
                regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
                message = "Email should be valid"
        )
        @NotBlank(message = "Email is required")
        String email,
        String idDocument,
        String phoneNumber,
        @NotNull(message = "BaseSalary is required")
        @Min(value = 0, message = "BaseSalary must be greater or equal to 0")
        @Max(value = 15000000, message = "BaseSalary must be less than or equal to 15,000,000")
        Double baseSalary,
        @NotBlank(message = "Password is required")
        String password
){
}
