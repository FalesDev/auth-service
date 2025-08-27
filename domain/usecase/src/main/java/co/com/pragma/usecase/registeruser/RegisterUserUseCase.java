package co.com.pragma.usecase.registeruser;

import co.com.pragma.model.exception.EmailAlreadyExistsException;
import co.com.pragma.model.exception.EntityNotFoundException;
import co.com.pragma.model.gateways.CustomLogger;
import co.com.pragma.model.gateways.PasswordEncoder;
import co.com.pragma.model.gateways.TransactionManager;
import co.com.pragma.model.role.gateways.RoleRepository;
import co.com.pragma.model.user.User;
import co.com.pragma.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class RegisterUserUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final TransactionManager transactionManager;
    private final CustomLogger customLogger;

    public Mono<User> registerUser(User user) {
        customLogger.trace("Starting user registration for email: {}", user.getEmail());
        return transactionManager.executeInTransaction(userRepository.existsByEmail(user.getEmail().toLowerCase())
                .flatMap(exists -> {
                    if (exists) {
                        customLogger.trace("Registration failed: Email already exists - {}", user.getEmail());
                        return Mono.error(new EmailAlreadyExistsException("Email is already registered"));
                    }
                    customLogger.trace("Email {} is available, proceeding with role lookup", user.getEmail());

                    return roleRepository.findByName("CLIENT")
                            .switchIfEmpty(Mono.defer(() -> {
                                customLogger.trace("Role 'CLIENT' not found in database");
                                return Mono.error(new EntityNotFoundException("Role not found"));
                            }))
                            .flatMap(role -> {
                                customLogger.trace("Role 'CLIENT' found, preparing user data");
                                user.setEmail(user.getEmail().toLowerCase());
                                user.setPassword(passwordEncoder.encode(user.getPassword()));
                                user.setIdRole(role.getId());

                                return userRepository.save(user)
                                        .doOnSuccess(savedUser ->
                                                customLogger.trace("User registered successfully: {}", savedUser.getEmail())
                                        );
                            });
                })
                .doOnError(error -> customLogger.trace("Registration process failed for {}: {}", user.getEmail(), error.getMessage()))
        );
    }
}
