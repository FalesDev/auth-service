package co.com.pragma.api;

import co.com.pragma.api.dto.request.RegisterUserRequestDto;
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
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class RouterRest {

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/api/v1/users",
                    beanClass = Handler.class,
                    beanMethod = "registerUser",
                    operation = @Operation(
                            operationId = "registerUser",
                            summary = "Register a new user",
                            requestBody = @RequestBody(
                                    required = true,
                                    content = @Content(
                                            schema = @Schema(implementation = RegisterUserRequestDto.class)
                                    )
                            )
                    )
            )
    })
    public RouterFunction<ServerResponse> routerFunction(Handler handler,
                                                         GlobalExceptionHandler globalExceptionHandler) {
        return RouterFunctions.route()
                .POST("/api/v1/users", handler::registerUser)
                .filter(globalExceptionHandler)
                .build();
    }
}
