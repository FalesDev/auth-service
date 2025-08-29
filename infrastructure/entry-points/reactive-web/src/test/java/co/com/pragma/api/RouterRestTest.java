package co.com.pragma.api;

import co.com.pragma.api.dto.UserDto;
import co.com.pragma.api.dto.request.RegisterUserRequestDto;
import co.com.pragma.api.exception.GlobalExceptionHandler;
import co.com.pragma.api.mapper.UserMapper;
import co.com.pragma.api.service.ValidationService;
import co.com.pragma.model.gateways.CustomLogger;
import co.com.pragma.model.user.User;
import co.com.pragma.usecase.registeruser.RegisterUserUseCase;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;

@ContextConfiguration(classes = {
        RouterRest.class,
        Handler.class,
        GlobalExceptionHandler.class
})
@WebFluxTest
class RouterRestTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private RegisterUserUseCase registerUserUseCase;
    @MockitoBean private UserMapper userMapper;
    @MockitoBean private ValidationService validationService;
    @MockitoBean private CustomLogger customLogger;

    private RegisterUserRequestDto registerUserRequestDto;

    @BeforeEach
    void setUp() {
        User userEntity = new User();
        userEntity.setId(UUID.randomUUID());
        userEntity.setFirstName("Fabricio");
        userEntity.setLastName("Rodriguez");
        userEntity.setEmail("fabricio@test.com");
        userEntity.setIdDocument("password123");
        userEntity.setPhoneNumber("123456789");
        userEntity.setIdRole(UUID.randomUUID());
        userEntity.setBaseSalary(3000.00);
        userEntity.setPassword("password123");

        registerUserRequestDto = new RegisterUserRequestDto(
                userEntity.getFirstName(),
                userEntity.getLastName(),
                userEntity.getEmail(),
                userEntity.getIdDocument(),
                userEntity.getPhoneNumber(),
                userEntity.getBaseSalary(),
                userEntity.getPassword()
        );

        UserDto userDto = new UserDto(
                userEntity.getId(),
                userEntity.getFirstName(),
                userEntity.getLastName(),
                userEntity.getEmail(),
                userEntity.getIdDocument(),
                userEntity.getPhoneNumber(),
                userEntity.getIdRole(),
                userEntity.getBaseSalary(),
                userEntity.getPassword()
        );

        Mockito.when(validationService.validate(any(RegisterUserRequestDto.class)))
                .thenReturn(Mono.just(registerUserRequestDto));

        Mockito.when(userMapper.toEntity(any(RegisterUserRequestDto.class)))
                .thenReturn(userEntity);
        Mockito.when(userMapper.toResponse(any(User.class)))
                .thenReturn(userDto);

        Mockito.when(registerUserUseCase.registerUser(any(User.class)))
                .thenReturn(Mono.just(userEntity));

        Handler handler = new Handler(registerUserUseCase, userMapper, validationService);
        webTestClient = WebTestClient.bindToRouterFunction(
                new RouterRest().routerFunction(handler, new GlobalExceptionHandler(customLogger))
        ).build();
    }

    @Test
    @DisplayName("Should return 201 Created when register-user request is successful")
    void testRegisterUserEndpointSuccess() {
        webTestClient.post()
                .uri("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(registerUserRequestDto)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(UserDto.class)
                .value(response -> {
                    Assertions.assertThat(response.email()).isEqualTo(registerUserRequestDto.email());
                    Assertions.assertThat(response.firstName()).isEqualTo(registerUserRequestDto.firstName());
                });
    }

    @Test
    @DisplayName("Should return 500 Internal Server Error when unexpected exception occurs during register-user")
    void testRegisterUserUnexpectedException() {
        Mockito.when(validationService.validate(any(RegisterUserRequestDto.class)))
                .thenReturn(Mono.error(new RuntimeException()));

        webTestClient.post()
                .uri("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(registerUserRequestDto)
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody()
                .jsonPath("$.status").isEqualTo("500");
    }
}
