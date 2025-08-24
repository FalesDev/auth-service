package co.com.pragma.api.exception;

import co.com.pragma.api.dto.response.ApiErrorResponse;
import co.com.pragma.model.exception.EmailAlreadyExistsException;
import co.com.pragma.model.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.HandlerFilterFunction;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GlobalExceptionHandler implements HandlerFilterFunction<ServerResponse, ServerResponse> {

    @Override
    @NonNull
    public Mono<ServerResponse> filter(@NonNull ServerRequest request,
                                       @NonNull HandlerFunction<ServerResponse> next) {
        return next.handle(request)
                .onErrorResume(ValidationException.class, ex -> {
                    List<ApiErrorResponse.FieldError> fieldErrors = ex.getErrors().entrySet().stream()
                            .map(e -> new ApiErrorResponse.FieldError(e.getKey(), e.getValue()))
                            .collect(Collectors.toList());

                    ApiErrorResponse response = new ApiErrorResponse(
                            OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
                            400,
                            "Validation failed",
                            ex.getMessage(),
                            request.path(),
                            fieldErrors
                    );
                    return ServerResponse.badRequest().bodyValue(response);
                })
                .onErrorResume(EmailAlreadyExistsException.class, ex -> {
                    ApiErrorResponse response = new ApiErrorResponse(
                            OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
                            409,
                            "User already exists",
                            ex.getMessage(),
                            request.path(),
                            null
                    );
                    return ServerResponse.status(409).bodyValue(response);
                })
                .onErrorResume(EntityNotFoundException.class, ex -> {
                    ApiErrorResponse response = new ApiErrorResponse(
                            OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
                            404,
                            "Role not found",
                            ex.getMessage(),
                            request.path(),
                            null
                    );
                    return ServerResponse.status(404).bodyValue(response);
                })
                .onErrorResume(ex -> {
                    ApiErrorResponse response = new ApiErrorResponse(
                            OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
                            500,
                            "Unexpected Error",
                            ex.getMessage(),
                            request.path(),
                            null
                    );
                    return ServerResponse.status(500).bodyValue(response);
                });
    }
}
