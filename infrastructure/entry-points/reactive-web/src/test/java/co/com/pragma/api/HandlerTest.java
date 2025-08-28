package co.com.pragma.api;

import co.com.pragma.api.dto.UserDto;
import co.com.pragma.api.dto.request.RegisterUserRequestDto;
import co.com.pragma.api.mapper.UserMapper;
import co.com.pragma.api.service.ValidationService;
import co.com.pragma.model.user.User;
import co.com.pragma.usecase.registeruser.RegisterUserUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HandlerTest {

    @Mock
    private RegisterUserUseCase registerUserUseCase;

    @Mock
    private UserMapper userMapper;

    @Mock
    private ValidationService validationService;

    @Mock
    private ServerRequest request;

    @InjectMocks
    private Handler handler;

    private RegisterUserRequestDto requestDto;
    private User user;
    private UserDto responseDto;

    @BeforeEach
    void setup() {
        requestDto = new RegisterUserRequestDto(
                "Fabricio",
                "Rodriguez",
                "fabricio@example.com",
                "77777777",
                "909090909",
                2500.50,
                "password123"
        );

        user = User.builder()
                .id(UUID.randomUUID())
                .firstName(requestDto.firstName())
                .lastName(requestDto.lastName())
                .email(requestDto.email())
                .idDocument(requestDto.idDocument())
                .phoneNumber(requestDto.phoneNumber())
                .idRole(UUID.randomUUID())
                .baseSalary(requestDto.baseSalary())
                .password(requestDto.password())
                .build();

        responseDto = new UserDto(
                user.getId(),
                "Fabricio",
                "Rodriguez",
                "fabricio@example.com",
                "77777777",
                "909090909",
                user.getIdRole(),
                2500.50,
                "password123"
        );
    }

    @Test
    @DisplayName("Should register user and return 201")
    void testRegisterUserSuccess() {
        when(request.bodyToMono(RegisterUserRequestDto.class)).thenReturn(Mono.just(requestDto));
        when(validationService.validate(requestDto)).thenReturn(Mono.just(requestDto));
        when(userMapper.toEntity(requestDto)).thenReturn(user);
        when(registerUserUseCase.registerUser(user)).thenReturn(Mono.just(user));
        when(userMapper.toResponse(user)).thenReturn(responseDto);

        Mono<ServerResponse> responseMono = handler.registerUser(request);

        StepVerifier.create(responseMono)
                .expectNextCount(1)
                .verifyComplete();
    }
}
