package co.com.pragma.api.dto;

import java.time.LocalDate;

public record UserDto(
        String id,
        String firstName,
        String lastName,
        LocalDate birthDate,
        String address,
        String phoneNumber,
        String email,
        Double baseSalary,
        String password
) {
}
