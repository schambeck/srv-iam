package com.ilegra.iam.authorize;

import com.ilegra.iam.authorize.model.Authorization;
import com.ilegra.iam.authorize.model.AuthorizationRepository;
import com.ilegra.iam.authorize.model.Client;
import com.ilegra.iam.authorize.model.ClientRepository;
import com.ilegra.iam.exception.InvalidCredentialsException;
import com.ilegra.iam.exception.InvalidRequestException;
import com.ilegra.iam.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.http.HttpStatus.FOUND;

@Slf4j
@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
class AuthorizeController {

  private final ClientRepository clientRepository;
  private final UserRepository userRepository;
  private final AuthorizationRepository authorizationRepository;

//   @RequestParam("scope") String scope,
//   @RequestParam("response_type") ResponseType responseType,
//   @RequestParam("client_id") String clientId,
//   @RequestParam("redirect_uri") String redirectUri,
//   @RequestParam(name = "state", required = false) String state,
//   @RequestParam(name = "response_mode", required = false) String responseMode,
//   @RequestParam(name = "nonce", required = false) String nonce,
//   @RequestParam(name = "display", required = false) String display,
//   @RequestParam(name = "prompt", required = false) String prompt,
//   @RequestParam(name = "max_age", required = false) String maxAge,
//   @RequestParam(name = "ui_locales", required = false) String uiLocales,
//   @RequestParam(name = "id_token_hint", required = false) String idTokenHint,
//   @RequestParam(name = "login_hint", required = false) String loginHint,
//   @RequestParam(name = "act_values", required = false) String acrValues) {

  @GetMapping("authorize")
  ResponseEntity<Void> authorize(@RequestParam Map<String, String> params,
                                 @RequestHeader(value = "Authorization", required = false) String authorization) {
    String clientId = getClientId(params);
    Client client = validateClientId(clientId);
    ResponseType responseType = getResponseType(params, client);
    String redirectUri = getRedirectUri(params);

    Flow flow = FlowFactory.getFlow(responseType, client, params);
    flow.validateRequiredParams();
    flow.validateRedirectUri(client);
    Credentials credentials = getCredentials(authorization, client);
    String code = authenticate(credentials);
    String location = redirectUri + "?code=" + code;
    log.info("Location: {}", location);

    return ResponseEntity.status(FOUND).header("Location", location).build();
  }

  @PostMapping("token")
  ResponseEntity<Authorization> token(@RequestBody TokenRequest request) {
    Authorization authorization = authorizationRepository.findByCode(request.getCode())
        .orElseThrow(() -> new IllegalArgumentException("Autorization code not found: " + request.getCode()));
    log.info("Authorization: {}", authorization);
    return ResponseEntity.ok(authorization);
  }

  private String authenticate(Credentials credentials) {
    if (!userRepository.existsByUsernameAndPassword(credentials.getUsername(), credentials.getPassword())) {
      throw new InvalidCredentialsException("Invalid credentials");
    }
    String code = authorizationRepository.createAuthorizationCode();
    authorizationRepository.create(code);
    return code;
  }

  private Credentials getCredentials(String authorization, Client client) {
    if (authorization == null || authorization.isBlank()) {
      throw new InvalidRequestException("Invalid authorization: '" + authorization + "'", client.getRedirectUri());
    }
    String codedAuth = authorization.substring(6);
    byte[] decode = Base64.getDecoder().decode(codedAuth);
    String decodeStr = new String(decode, UTF_8);
    String[] fields = decodeStr.split(":");
    return Credentials.builder()
        .username(fields[0])
        .password(fields[1])
        .build();
  }

  private Client validateClientId(String clientId) {
    Client client = getClient(clientId);
    if (client == null) {
      throw new InvalidRequestException("Invalid client_id: " + clientId);
    }
    return client;
  }

  private Client getClient(String clientId) {
    return clientRepository.findByName(clientId);
  }

  private String getRedirectUri(Map<String, String> params) {
    return params.get("redirect_uri");
  }

  private String getClientId(Map<String, String> params) {
    return params.get("client_id");
  }

  private ResponseType getResponseType(Map<String, String> params, Client client) {
    String responseTypeParam = params.get("response_type");
    if (responseTypeParam.isBlank()) {
      throw new InvalidRequestException("Invalid response_type: '" + responseTypeParam + "'", client.getRedirectUri());
    }
    return ResponseType.valueOf(responseTypeParam);
  }

}
