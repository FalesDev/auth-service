package co.com.pragma.api.dto.request;

import java.time.LocalDate;

public record RegisterUserRequestDto(
        String firstName,
        String lastName,
        LocalDate birthDate,
        String address,
        String phoneNumber,
        String email,
        Double baseSalary,
        String password
){
}
