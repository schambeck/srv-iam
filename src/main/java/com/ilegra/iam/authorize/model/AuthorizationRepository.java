package com.ilegra.iam.authorize.model;

import com.ilegra.iam.exception.InvalidRequestException;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class AuthorizationRepository {

  private static final List<Authorization> authorizations = new ArrayList<>();

  public Authorization create(String code) {
    if (exists(code)) {
      throw new InvalidRequestException("Authorization code already exists: " + code);
    }
    Authorization authorization = Authorization.builder()
        .id(UUID.randomUUID())
        .authorizationCode(code)
        .accessToken(UUID.randomUUID().toString())
        .build();
    authorizations.add(authorization);
    return authorization;
  }

  public Optional<Authorization> findByCode(String code) {
    return authorizations.stream()
        .filter(p -> code.equals(p.getAuthorizationCode()))
        .findAny();
  }

  public String createAuthorizationCode() {
    int leftLimit = 48; // numeral '0'
    int rightLimit = 122; // letter 'z'
    int targetStringLength = 10;
    Random random = new Random();
    return random.ints(leftLimit, rightLimit + 1)
        .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
        .limit(targetStringLength)
        .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
        .toString();
  }

  public boolean exists(String code) {
    return authorizations.stream()
        .anyMatch(p -> code.equals(p.getAuthorizationCode()));
  }

}
