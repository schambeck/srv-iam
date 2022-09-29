package com.ilegra.iam.exception;

public class InvalidRequestException extends RuntimeException {

    private final String redirectUri;

    public InvalidRequestException(String message) {
        this(message, null);
    }

    public InvalidRequestException(String message, String redirectUri) {
        super(message);
        this.redirectUri = redirectUri;
    }

    public String getRedirectUri() {
        return redirectUri;
    }
}
