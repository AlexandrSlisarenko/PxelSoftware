package ru.slisarenko.pxelsoftware.security.factory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import ru.slisarenko.pxelsoftware.security.dto.Token;

import java.time.Duration;
import java.time.Instant;
import java.util.LinkedList;
import java.util.UUID;
import java.util.function.Function;

import static ru.slisarenko.pxelsoftware.config.Constants.*;

@Slf4j
@RequiredArgsConstructor
public class RefreshTokenFactory implements Function<Authentication, Token> {

    private final Duration refreshInterval;

    @Override
    public Token apply(Authentication authentication) {
        log.info("Create Refresh Token");
        var authorities = new LinkedList<String>();
        var momentOfCreated = Instant.now();

        authorities.add(JWT_REFRESH);
        authorities.add(JWT_LOGOUT);
        log.info("Authentication authorities:");
        authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .peek(log::info)
                .map(authority -> PREFIX_ROLE_USER + authority)
                .forEach(authorities::add);

        return Token.builder()
                .username(authentication.getName())
                .authorities(authorities)
                .id(UUID.randomUUID())
                .expiresAt(momentOfCreated.plus(refreshInterval))
                .createdAt(momentOfCreated)
                .build();
    }
}
