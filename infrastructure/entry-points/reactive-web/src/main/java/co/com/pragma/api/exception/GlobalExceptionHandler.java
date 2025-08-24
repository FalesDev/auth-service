package co.com.pragma.api.exception;

import co.com.pragma.api.dto.response.ApiErrorResponse;
import co.com.pragma.model.exception.EmailAlreadyExistsException;
import co.com.pragma.model.exception.EntityNotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@Slf4j
@Order(-2)
public class GlobalExceptionHandler implements WebExceptionHandler {

    private final ObjectMapper objectMapper;

    public GlobalExceptionHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String message = "Unexpected error";
        List<ApiErrorResponse.FieldError> fieldErrors = List.of();

        if (ex instanceof IllegalStateException) {
            status = HttpStatus.CONFLICT;
            message = "Operation not allowed";
            log.warn("Conflict operation: {} - Path: {}", ex.getMessage(), exchange.getRequest().getPath());
        } else if (ex instanceof EntityNotFoundException) {
            status = HttpStatus.NOT_FOUND;
            message = ex.getMessage();
            log.warn("Entity not found: {} - Path: {}", ex.getMessage(), exchange.getRequest().getPath());
        } else if (ex instanceof EmailAlreadyExistsException) {
            status = HttpStatus.CONFLICT;
            message = ex.getMessage();
            log.warn("Duplicate email attempt: {} - Path: {}", ex.getMessage(), exchange.getRequest().getPath());
        } else {
            log.error("Unexpected error in request to {}: {}",
                    exchange.getRequest().getPath(), ex.getMessage(), ex);
        }

        ApiErrorResponse error = new ApiErrorResponse(
                status.value(),
                message,
                fieldErrors
        );

        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        DataBuffer buffer;
        try {
            buffer = exchange.getResponse().bufferFactory()
                    .wrap(objectMapper.writeValueAsBytes(error));
        } catch (JsonProcessingException e) {
            log.error("Error serializing ApiErrorResponse: {}", e.getMessage(), e);
            return exchange.getResponse().setComplete();
        }

        return exchange.getResponse().writeWith(Mono.just(buffer));
    }
}
