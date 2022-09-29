package com.ilegra.iam.authorize;

import com.ilegra.iam.authorize.model.Client;
import com.ilegra.iam.exception.InvalidRequestException;
import lombok.SneakyThrows;

import java.util.Map;

import static com.ilegra.iam.authorize.ResponseType.CODE;

public class FlowFactory {

  private static final Map<ResponseType, Class<? extends Flow>> flows = Map.of(CODE, AuthorizationCodeFlow.class);

  @SneakyThrows
  public static Flow getFlow(ResponseType responseType, Client client, Map<String, String> params) {
    flows.keySet().stream()
        .filter(p -> p == responseType)
        .findAny()
        .orElseThrow(() -> new InvalidRequestException("Invalid flow", client.getRedirectUri()));
    Class<? extends Flow> clazz = flows.get(responseType);
    return clazz.getDeclaredConstructor(Client.class, Map.class).newInstance(client, params);
  }

}
