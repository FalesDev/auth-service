package co.com.pragma.api;

import co.com.pragma.api.dto.RegisterUserDTO;
import co.com.pragma.api.mapper.UserDTOMapper;
import co.com.pragma.usecase.registeruser.RegisterUserUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class Handler {

    private final RegisterUserUseCase registerUserUseCase;
    private final UserDTOMapper userDTOMapper;

    public Mono<ServerResponse> registerUser(ServerRequest request) {
        return request.bodyToMono(RegisterUserDTO.class)
                .map(userDTOMapper::toEntity)
                .flatMap(registerUserUseCase::registerUser)
                .map(userDTOMapper::toResponse)
                .flatMap(dto -> ServerResponse
                        .status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(dto)
                )
                /*.onErrorResume(e -> ServerResponse
                        .status(HttpStatus.BAD_REQUEST)
                        .bodyValue(Map.of("error", e.getMessage()))
                )*/;
    }
}
