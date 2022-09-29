package com.ilegra.iam.authorize.model;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class Authorization {

  private UUID id;
  private String authorizationCode;
  private String accessToken;

}
