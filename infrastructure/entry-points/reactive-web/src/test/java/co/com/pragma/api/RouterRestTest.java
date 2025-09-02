package co.com.pragma.api;

import co.com.pragma.api.dto.UserDto;
import co.com.pragma.api.dto.request.LoginRequest;
import co.com.pragma.api.dto.request.RegisterUserRequestDto;
import co.com.pragma.api.dto.response.AuthResponse;
import co.com.pragma.api.exception.GlobalExceptionHandler;
import co.com.pragma.api.mapper.TokenMapper;
import co.com.pragma.api.mapper.UserMapper;
import co.com.pragma.api.service.ValidationService;
import co.com.pragma.model.gateways.CustomLogger;
import co.com.pragma.model.token.Token;
import co.com.pragma.model.user.User;
import co.com.pragma.usecase.finduserbyiddocument.FindUserByIdDocumentUseCase;
import co.com.pragma.usecase.login.LoginUseCase;
import co.com.pragma.usecase.registeruser.RegisterUseCase;
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
    private RegisterUseCase registerUseCase;
    @MockitoBean
    private LoginUseCase loginUseCase;
    @MockitoBean private UserMapper userMapper;
    @MockitoBean
    private TokenMapper tokenMapper;
    @MockitoBean private ValidationService validationService;
    @MockitoBean private CustomLogger customLogger;

    private RegisterUserRequestDto registerUserRequestDto;
    private LoginRequest loginRequest;

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

        loginRequest = new LoginRequest(
                userEntity.getEmail(),
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

        Token token = new Token("test-token", 3600L);
        AuthResponse authResponse = new AuthResponse("test-token", 3600L);

        Mockito.when(validationService.validate(any(RegisterUserRequestDto.class)))
                .thenReturn(Mono.just(registerUserRequestDto));

        Mockito.when(validationService.validate(any(LoginRequest.class)))
                .thenReturn(Mono.just(loginRequest));

        Mockito.when(userMapper.toEntity(any(RegisterUserRequestDto.class)))
                .thenReturn(userEntity);

        Mockito.when(userMapper.toResponse(any(User.class)))
                .thenReturn(userDto);

        Mockito.when(tokenMapper.toResponse(any(Token.class)))
                .thenReturn(authResponse);

        Mockito.when(registerUseCase.register(any(User.class)))
                .thenReturn(Mono.just(userEntity));

        Mockito.when(loginUseCase.login(any(String.class), any(String.class)))
                .thenReturn(Mono.just(token));

        Handler handler = new Handler(registerUseCase, loginUseCase, userMapper, tokenMapper, validationService);
        webTestClient = WebTestClient.bindToRouterFunction(
                new RouterRest().routerFunction(handler, new GlobalExceptionHandler(customLogger))
        ).build();
    }

    @Test
    @DisplayName("Should return 201 Created when register request is successful")
    void testRegisterEndpointSuccess() {
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
    @DisplayName("Should return 200 OK when login request is successful")
    void testLoginEndpointSuccess() {
        webTestClient.post()
                .uri("/api/v1/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(loginRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(AuthResponse.class)
                .value(response -> {
                    Assertions.assertThat(response.accessToken()).isEqualTo("test-token");
                    Assertions.assertThat(response.expiresIn()).isEqualTo(3600L);
                });
    }

    @Test
    @DisplayName("Should return 500 Internal Server Error when unexpected exception occurs during register")
    void testRegisterUnexpectedException() {
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
