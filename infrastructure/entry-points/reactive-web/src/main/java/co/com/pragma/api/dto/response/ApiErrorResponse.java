package co.com.pragma.api.dto.response;

import java.util.List;

public record ApiErrorResponse(
        int status,
        String message,
        List<FieldError> errors
) {
    public record FieldError(
            String field,
            String message
    ) {}
}