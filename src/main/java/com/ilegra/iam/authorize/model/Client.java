package com.ilegra.iam.authorize.model;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class Client {

  private UUID id;
  private String name;
  private String redirectUri;

}
