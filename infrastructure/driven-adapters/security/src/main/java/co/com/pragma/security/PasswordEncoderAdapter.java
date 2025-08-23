package co.com.pragma.security;

import co.com.pragma.model.gateways.PasswordEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PasswordEncoderAdapter implements PasswordEncoder {

    private final org.springframework.security.crypto.password.PasswordEncoder springSecurityEncoder;

    @Override
    public String encode(String rawPassword) {
        return springSecurityEncoder.encode(rawPassword);
    }

    @Override
    public boolean matches(String rawPassword, String encodedPassword) {
        return springSecurityEncoder.matches(rawPassword, encodedPassword);
    }
}
