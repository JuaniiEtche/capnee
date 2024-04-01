package com.gidas.capneebe.global.exceptions.customs;

public class BadCredentialsException extends RuntimeException{
    public BadCredentialsException(String message) {
        super(message);
    }
}
