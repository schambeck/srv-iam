package com.ilegra.iam.user;

import com.ilegra.iam.exception.InvalidCredentialsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;

    public User create(User user) {
        log.info("Create: {}", user);
        cryptPassword(user);
        return repository.save(user);
    }

    public List<User> list() {
        return StreamSupport.stream(repository.findAll().spliterator(), false)
            .collect(Collectors.toList());
    }

    private void cryptPassword(User user) {
        byte[] salt = PasswordCrypt.getSalt();
        String generatedSecuredPasswordHash = PasswordCrypt.generateStrongPasswordHash(user.getPassword(), salt);
        user.setPassword(generatedSecuredPasswordHash);
        String encode = Base64.getEncoder().encodeToString(salt);
        user.setSalt(encode);
        log.info("cryptPassword: {}", user);
    }

    public LoginResponse login(LoginRequest request) {
        log.info("login: {}", request);
        User user = repository.findByUsername(request.getUsername())
            .orElseThrow(() -> new InvalidCredentialsException("Invalid credentials: " + request.getUsername()));
        log.info("user: {}", user);
        byte[] decode = Base64.getDecoder().decode(user.getSalt());
        String generatedSecuredPasswordHash = PasswordCrypt.generateStrongPasswordHash(request.getPassword(), decode);
        log.info("generatedSecuredPasswordHash: {}", generatedSecuredPasswordHash);
        if (!repository.existsByUsernameAndPassword(request.getUsername(), generatedSecuredPasswordHash)) {
            throw new InvalidCredentialsException("Invalid credentials");
        }
        return LoginResponse.builder()
                .sessionId(UUID.randomUUID())
                .build();
    }

}
