package com.ilegra.iam.authorize;

import com.ilegra.iam.authorize.model.Client;

import java.util.Map;
import java.util.Set;

import static com.ilegra.iam.authorize.ResponseType.CODE;

public class AuthorizationCodeFlow extends FlowAdapter {

  public AuthorizationCodeFlow(Client client, Map<String, String> params) {
    super(client, params);
  }

  @Override
  public ResponseType responseType() {
    return CODE;
  }

  @Override
  public Set<String> requiredParams() {
    return Set.of("scope", "response_type", "client_id", "redirect_uri");
  }

  @Override
  public String defaultScope() {
    return "openid";
  }

}
