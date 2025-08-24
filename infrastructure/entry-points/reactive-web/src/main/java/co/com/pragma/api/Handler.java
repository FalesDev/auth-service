package co.com.pragma.api;

import co.com.pragma.api.dto.request.RegisterUserRequestDto;
import co.com.pragma.api.mapper.UserMapper;
import co.com.pragma.api.service.ValidationService;
import co.com.pragma.usecase.registeruser.RegisterUserUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class Handler {

    private final RegisterUserUseCase registerUserUseCase;
    private final UserMapper userMapper;
    private final ValidationService validationService;

    public Mono<ServerResponse> registerUser(ServerRequest request) {
        return request.bodyToMono(RegisterUserRequestDto.class)
                .flatMap(validationService::validate)
                .map(userMapper::toEntity)
                .flatMap(registerUserUseCase::registerUser)
                .map(userMapper::toResponse)
                .flatMap(dto -> ServerResponse
                        .status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(dto)
                );
    }
}
