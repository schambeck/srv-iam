package com.ilegra.iam.authorize;

import com.ilegra.iam.authorize.model.Client;
import com.ilegra.iam.exception.InvalidRequestException;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
abstract class FlowAdapter implements Flow {

  private final Client client;
  private final Map<String, String> params;

  @Override
  public void validateRequiredParams() {
    for (String param : params.keySet()) {
      Map.Entry<String, String> entry = params.entrySet().stream()
          .filter(p -> param.equals(p.getKey()))
          .findAny()
          .orElseThrow(() -> new InvalidRequestException("Param is missing: '" + param + "'", client.getRedirectUri()));
      if (entry.getValue().isBlank()) {
        throw new InvalidRequestException("Param is blank: '" + param + "'", client.getRedirectUri());
      }
    }
  }

  @Override
  public boolean isOpenIdConnect() {
    if (defaultScope() != null) {
      String scope = params.get("scope");
      return scope.contains(defaultScope());
    }
    return false;
  }

  @Override
  public void validateRedirectUri(Client client) {
    String redirectUri = params.get("redirect_uri");
    if (!client.getRedirectUri().contains(redirectUri)) {
      throw new InvalidRequestException("Invalid redirect URI: " + redirectUri + ". Should be: " + client.getRedirectUri(), client.getRedirectUri());
    }
  }

}
