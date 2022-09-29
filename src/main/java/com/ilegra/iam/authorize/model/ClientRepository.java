package com.ilegra.iam.authorize.model;

import com.ilegra.iam.exception.InvalidRequestException;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class ClientRepository {

  private static final List<Client> clients =
      List.of(Client.builder()
        .id(UUID.randomUUID())
        .name("asldfkjsadf09")
        .redirectUri("http://localhost:4200")
        .build());

  public Client findByName(String name) {
    return clients.stream()
        .filter(p -> p.getName().equals(name))
        .findAny()
        .orElseThrow(() -> new InvalidRequestException("Invalid client: " + name));
  }

}
