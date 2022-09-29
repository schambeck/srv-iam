package com.ilegra.iam.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("users")
@RequiredArgsConstructor
class UserController {

  private final UserService service;

  @PostMapping
  ResponseEntity<User> create(@RequestBody User user) {
    log.info("user: {}", user);
    User created = service.create(user);
    return ResponseEntity.ok(created);
  }

  @PostMapping("login")
  ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
    log.info("request: {}", request);
    LoginResponse response = service.login(request);
    return ResponseEntity.ok(response);
  }

}
