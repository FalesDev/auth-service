package co.com.pragma.api;

import co.com.pragma.api.dto.UserDto;
import co.com.pragma.api.dto.request.LoginRequest;
import co.com.pragma.api.dto.request.RegisterUserRequestDto;
import co.com.pragma.api.dto.request.UserValidationRequest;
import co.com.pragma.api.dto.response.AuthResponse;
import co.com.pragma.api.dto.response.UserValidationResponse;
import co.com.pragma.api.exception.GlobalExceptionHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class RouterRest {

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/api/v1/users",
                    method = RequestMethod.POST,
                    beanClass = Handler.class,
                    beanMethod = "registerUser",
                    operation = @Operation(
                            operationId = "registerUser",
                            summary = "Register a new user",
                            tags = {"User"},
                            requestBody = @RequestBody(
                                    required = true,
                                    content = @Content(
                                            schema = @Schema(implementation = RegisterUserRequestDto.class)
                                    )
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "201",
                                            description = "User successfully registered",
                                            content = @Content(
                                                    mediaType = "application/json",
                                                    schema = @Schema(implementation = UserDto.class)
                                            )
                                    )
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/users/document",
                    method = RequestMethod.POST,
                    beanClass = Handler.class,
                    beanMethod = "findUserByIdDocument",
                    operation = @Operation(
                            operationId = "findUserByIdDocument",
                            summary = "Find a user by IdDocument",
                            tags = {"User"},
                            requestBody = @RequestBody(
                                    required = true,
                                    content = @Content(
                                            schema = @Schema(implementation = UserValidationRequest.class)
                                    )
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "User found",
                                            content = @Content(
                                                    mediaType = "application/json",
                                                    schema = @Schema(implementation = UserValidationResponse.class)
                                            )
                                    )
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/login",
                    method = RequestMethod.POST,
                    beanClass = Handler.class,
                    beanMethod = "loginUser",
                    operation = @Operation(
                            operationId = "loginUser",
                            summary = "Login user",
                            tags = {"Auth"},
                            requestBody = @RequestBody(
                                    required = true,
                                    content = @Content(
                                            schema = @Schema(implementation = LoginRequest.class)
                                    )
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "201",
                                            description = "User successfully logged in",
                                            content = @Content(
                                                    mediaType = "application/json",
                                                    schema = @Schema(implementation = AuthResponse.class)
                                            )
                                    )
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> routerFunction(Handler handler,
                                                         GlobalExceptionHandler globalExceptionHandler) {
        return RouterFunctions.route()
                .POST("/api/v1/users", handler::registerUser)
                .POST("/api/v1/login", handler::loginUser)
                .POST("/api/v1/users/document", handler::findUserByIdDocument)
                .filter(globalExceptionHandler)
                .build();
    }
}
