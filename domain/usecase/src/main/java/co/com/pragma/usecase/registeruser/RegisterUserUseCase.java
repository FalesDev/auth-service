package co.com.pragma.usecase.registeruser;

import co.com.pragma.model.gateways.PasswordEncoder;
import co.com.pragma.model.user.User;
import co.com.pragma.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class RegisterUserUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Mono<User> registerUser(User user) {
        return userRepository.existsByEmail(user.getEmail())
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new IllegalArgumentException("Email already registered"));
                    }
                    String encodedPassword = passwordEncoder.encode(user.getPassword());
                    User userWithEncodedPassword = user.toBuilder()
                            .password(encodedPassword)
                            .build();
                    return userRepository.save(userWithEncodedPassword);
                });
    }
}
