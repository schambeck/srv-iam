package com.ilegra.iam.user;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends CrudRepository<User, UUID> {

  boolean existsByUsernameAndPassword(String username, String password);

  Optional<User> findByUsername(String username);

}
