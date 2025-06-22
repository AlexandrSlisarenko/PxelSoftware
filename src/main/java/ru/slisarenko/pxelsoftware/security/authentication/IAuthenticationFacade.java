package ru.slisarenko.pxelsoftware.security.authentication;

import org.springframework.security.core.Authentication;

public interface IAuthenticationFacade {

    Authentication getAuthentication();

    String getUsername();
}
