package ru.slisarenko.pxelsoftware.security.exception;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

import javax.naming.AuthenticationException;

public class TokenAuthenticationUserDetailsServiceException extends UsernameNotFoundException {
    public TokenAuthenticationUserDetailsServiceException(String msg) {
        super(msg);
    }
    public TokenAuthenticationUserDetailsServiceException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
