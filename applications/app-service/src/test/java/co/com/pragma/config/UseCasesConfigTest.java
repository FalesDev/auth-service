package co.com.pragma.config;

import co.com.pragma.model.gateways.CustomLogger;
import co.com.pragma.model.gateways.PasswordHasher;
import co.com.pragma.model.gateways.TransactionManager;
import co.com.pragma.model.role.gateways.RoleRepository;
import co.com.pragma.model.user.gateways.UserRepository;
import co.com.pragma.usecase.registeruser.RegisterUserUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

public class UseCasesConfigTest {

    @Test
    @DisplayName("Should register RegisterUserUseCase bean in application context")
    void testRegisterUserUseCaseBeanExists() {
        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(TestConfig.class)) {

            RegisterUserUseCase registerUserUseCase = context.getBean(RegisterUserUseCase.class);
            assertNotNull(registerUserUseCase, "RegisterUserUseCase bean should be registered");
        }
    }

    @Configuration
    @Import(UseCasesConfig.class)
    static class TestConfig {

        @Bean
        UserRepository userRepository() { return mock(UserRepository.class); }
        @Bean
        RoleRepository roleRepository() { return mock(RoleRepository.class); }
        @Bean
        PasswordHasher passwordEncoder() { return mock(PasswordHasher.class); }
        @Bean
        TransactionManager transactionManager() { return mock(TransactionManager.class); }
        @Bean
        CustomLogger customLogger() { return mock(CustomLogger.class); }
    }
}