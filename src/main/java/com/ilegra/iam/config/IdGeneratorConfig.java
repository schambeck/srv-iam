package com.ilegra.iam.config;

import com.ilegra.iam.user.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.relational.core.mapping.event.BeforeConvertCallback;

import java.util.UUID;

//@Configuration
class IdGeneratorConfig {

  @Bean
  BeforeConvertCallback<User> beforeSaveCallback() {
    return (user) -> {
      if (user.getId() == null) {
        user.setId(UUID.randomUUID());
      }
      return user;
    };
  }

}
