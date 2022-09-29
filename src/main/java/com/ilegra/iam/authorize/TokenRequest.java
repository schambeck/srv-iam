package com.ilegra.iam.authorize;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TokenRequest {

  private String grantType;
  private String code;
  private String redirectUri;

}
