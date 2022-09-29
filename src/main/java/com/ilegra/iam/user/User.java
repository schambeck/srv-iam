package com.ilegra.iam.user;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;

import java.util.UUID;

@Getter
@Setter
@ToString
public class User {

  @Id
  private String id;
  private String username;
  private String password;
  private String salt;
  private String email;
  private String firstName;
  private String lastName;

}
