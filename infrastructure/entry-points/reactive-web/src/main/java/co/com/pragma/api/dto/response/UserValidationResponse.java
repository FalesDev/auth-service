package co.com.pragma.api.dto.response;

public record UserValidationResponse(
        String email,
        String idDocument,
        String role
) {
}
