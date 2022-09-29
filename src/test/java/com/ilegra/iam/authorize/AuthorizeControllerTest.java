package com.ilegra.iam.authorize;

import com.ilegra.iam.authorize.model.Authorization;
import com.ilegra.iam.authorize.model.AuthorizationRepository;
import com.ilegra.iam.authorize.model.Client;
import com.ilegra.iam.authorize.model.ClientRepository;
import com.ilegra.iam.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.UUID;

import static io.restassured.http.ContentType.JSON;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.mockMvc;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers = AuthorizeController.class)
class AuthorizeControllerTest {

  @Autowired
  private MockMvc mvc;
  @MockBean
  private ClientRepository clientRepository;
  @MockBean
  private UserRepository userRepository;
  @MockBean
  private AuthorizationRepository authorizationRepository;

  @BeforeEach
  void setUp() {
    mockMvc(mvc);
  }

  @Test
  void authorize() {
    Client client = Client.builder()
        .id(UUID.randomUUID())
        .name("asldfkjsadf09")
        .redirectUri("http://localhost:4200")
        .build();
    Authorization authorization = Authorization.builder()
        .id(UUID.randomUUID())
        .authorizationCode("45893475kjrfsd098")
        .build();
    when(clientRepository.findByName("asldfkjsadf09")).thenReturn(client);
    when(userRepository.existsByUsernameAndPassword("user01", "pass1")).thenReturn(true);
    when(authorizationRepository.createAuthorizationCode()).thenReturn("45893475kjrfsd098");
    when(authorizationRepository.create("45893475kjrfsd098")).thenReturn(authorization);

    given().
        header("Authorization", "Basic dXNlcjAxOnBhc3Mx").
        param("scope", "openid").
        param("response_type", "CODE").
        param("client_id", "asldfkjsadf09").
        param("redirect_uri", "http://localhost:4200").
    when().
        get("/auth/authorize").
    then().
        log().all().
        statusCode(302).
        header("Location", "http://localhost:4200?code=45893475kjrfsd098");
  }

  @Test
  void token() {
    String accessToken = UUID.randomUUID().toString();
    Authorization authorization = Authorization.builder()
        .id(UUID.randomUUID())
        .authorizationCode("45893475kjrfsd098")
        .accessToken(accessToken)
        .build();
    when(authorizationRepository.findByCode("45893475kjrfsd098")).thenReturn(Optional.of(authorization));
    TokenRequest payload = TokenRequest.builder()
        .grantType("authorization_code")
        .code("45893475kjrfsd098")
        .redirectUri("http://localhost:4200")
        .build();

    given().
        contentType(JSON).
        body(payload).
    when().
        post("/auth/token").
    then().
        log().all().
        statusCode(200).
        body("accessToken", equalTo(accessToken));
  }

  @Test
  void authorizeWithoutResponseType() {
    Client client = Client.builder()
        .id(UUID.randomUUID())
        .name("asldfkjsadf09")
        .redirectUri("http://localhost:4200")
        .build();
    when(clientRepository.findByName("asldfkjsadf09")).thenReturn(client);

    given().
        param("scope", "openid").
        param("response_type", "").
        param("client_id", "asldfkjsadf09").
        param("redirect_uri", "http://localhost:4200").
    when().
        get("/auth/authorize").
    then().
        log().all().
        statusCode(302).
        header("Location", "http://localhost:4200?error=invalid_request&error_description=Invalid response_type: ''");
  }

  @Test
  void authorizeWithoutClientId() {
    Client client = Client.builder()
        .id(UUID.randomUUID())
        .name("asldfkjsadf09")
        .redirectUri("http://localhost:4200")
        .build();
    when(clientRepository.findByName("asldfkjsadf09")).thenReturn(client);

    given().
        param("scope", "openid").
        param("response_type", "CODE").
        param("client_id", "").
        param("redirect_uri", "http://localhost:4200").
    when().
        get("/auth/authorize").
    then().
        log().all().
        statusCode(302).
        header("Location", "?error=invalid_request&error_description=Invalid client_id: ");
  }

  @Test
  void authorizeWithoutRedirectUri() {
    Client client = Client.builder()
        .id(UUID.randomUUID())
        .name("asldfkjsadf09")
        .redirectUri("http://localhost:4200")
        .build();
    when(clientRepository.findByName("asldfkjsadf09")).thenReturn(client);

    given().
        param("scope", "openid").
        param("response_type", "CODE").
        param("client_id", "asldfkjsadf09").
        param("redirect_uri", "").
    when().
        get("/auth/authorize").
    then().
        log().all().
        statusCode(302).
        header("Location", "http://localhost:4200?error=invalid_request&error_description=Param is blank: 'redirect_uri'");
  }

}