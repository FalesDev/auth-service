package co.com.pragma.api.exception;

import co.com.pragma.api.dto.response.ApiErrorResponse;
import co.com.pragma.model.exception.*;
import co.com.pragma.model.gateways.CustomLogger;
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

@Component
@RequiredArgsConstructor
public class GlobalExceptionHandler implements HandlerFilterFunction<ServerResponse, ServerResponse> {

    private final CustomLogger logger;

    @Override
    @NonNull
    public Mono<ServerResponse> filter(@NonNull ServerRequest request,
                                       @NonNull HandlerFunction<ServerResponse> next) {
        return next.handle(request)
                .onErrorResume(ValidationException.class, ex -> {
                    logger.warn("Validation error at: " + ex.getErrors());
                    ApiErrorResponse response = ApiErrorResponse.builder()
                            .timestamp(OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))
                            .status(400)
                            .error("BAD REQUEST")
                            .message("Validation failed")
                            .errors(ex.getErrors())
                            .build();
                    return ServerResponse.badRequest().bodyValue(response);
                })
                .onErrorResume(EmailAlreadyExistsException.class, ex -> {
                    logger.warn("Email conflict at: " + ex.getMessage());
                    ApiErrorResponse response = ApiErrorResponse.builder()
                            .timestamp(OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))
                            .status(409)
                            .error("CONFLICT")
                            .message(ex.getMessage())
                            .build();
                    return ServerResponse.status(409).bodyValue(response);
                })
                .onErrorResume(IdDocumentAlreadyExistsException.class, ex -> {
                    logger.warn("IdDocument conflict at: " + ex.getMessage());
                    ApiErrorResponse response = ApiErrorResponse.builder()
                            .timestamp(OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))
                            .status(409)
                            .error("CONFLICT")
                            .message(ex.getMessage())
                            .build();
                    return ServerResponse.status(409).bodyValue(response);
                })
                .onErrorResume(EntityNotFoundException.class, ex -> {
                    logger.warn("Entity not found at: " + ex.getMessage());
                    ApiErrorResponse response = ApiErrorResponse.builder()
                            .timestamp(OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))
                            .status(404)
                            .error("NOT FOUND")
                            .message(ex.getMessage())
                            .build();
                    return ServerResponse.status(404).bodyValue(response);
                })
                .onErrorResume(TokenValidationException.class, ex -> {
                    logger.warn("JWT validation failed: " + ex.getMessage());
                    ApiErrorResponse response = ApiErrorResponse.builder()
                            .timestamp(OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))
                            .status(401)
                            .error("UNAUTHORIZED")
                            .message(ex.getMessage())
                            .build();
                    return ServerResponse.status(401).bodyValue(response);
                })
                .onErrorResume(InvalidCredentialsException.class, ex -> {
                    logger.warn("Authentication failed: " + ex.getMessage());
                    ApiErrorResponse response = ApiErrorResponse.builder()
                            .timestamp(OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))
                            .status(401)
                            .error("UNAUTHORIZED")
                            .message(ex.getMessage())
                            .build();
                    return ServerResponse.status(401).bodyValue(response);
                })
                .onErrorResume(ex -> {
                    logger.error("Internal server error at: " + ex.getMessage());
                    ApiErrorResponse response = ApiErrorResponse.builder()
                            .timestamp(OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))
                            .status(500)
                            .error("INTERNAL SERVER ERROR")
                            .message(ex.getMessage())
                            .build();
                    return ServerResponse.status(500).bodyValue(response);
                });
    }
}
