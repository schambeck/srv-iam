package com.ilegra.iam.user;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class LoginResponse {

    private UUID sessionId;

}
