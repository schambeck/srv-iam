package com.ilegra.iam.authorize;

import com.ilegra.iam.authorize.model.Client;

import java.util.Set;

public interface Flow {
  ResponseType responseType();

  Set<String> requiredParams();

  String defaultScope();

  void validateRequiredParams();

  boolean isOpenIdConnect();

  void validateRedirectUri(Client client);

}
