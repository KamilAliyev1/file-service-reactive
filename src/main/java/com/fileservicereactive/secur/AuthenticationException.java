package com.fileservicereactive.secur;

public class AuthenticationException extends RuntimeException{
    public AuthenticationException() {
    }

    public AuthenticationException(String message) {
        super(message);
    }
}
