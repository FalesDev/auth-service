package co.com.pragma.api.config;

import co.com.pragma.model.user.UserValidation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ValidationConfig {

    @Bean
    public UserValidation userValidation() {
        return new UserValidation();
    }
}
