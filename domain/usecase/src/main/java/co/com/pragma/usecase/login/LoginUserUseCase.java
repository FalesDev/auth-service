package co.com.pragma.usecase.login;

import co.com.pragma.model.exception.EntityNotFoundException;
import co.com.pragma.model.exception.InvalidCredentialsException;
import co.com.pragma.model.gateways.PasswordHasher;
import co.com.pragma.model.token.Token;
import co.com.pragma.model.token.gateways.TokenRepository;
import co.com.pragma.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class LoginUserUseCase {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final TokenRepository tokenRepository;

    public Mono<Token> login(String email, String rawPassword) {
        return userRepository.findByEmail(email)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("User not found")))
                .filter(user -> passwordHasher.matches(rawPassword, user.getPassword()))
                .switchIfEmpty(Mono.error(new InvalidCredentialsException("Invalid password")))
                .flatMap(tokenRepository::generateAccessToken);
    }
}
