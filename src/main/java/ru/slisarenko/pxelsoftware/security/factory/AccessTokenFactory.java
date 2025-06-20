package ru.slisarenko.pxelsoftware.security.factory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.slisarenko.pxelsoftware.security.dto.Token;

import java.time.Duration;
import java.time.Instant;
import java.util.function.Function;

import static ru.slisarenko.pxelsoftware.config.Constants.PREFIX_ROLE_USER;

@Slf4j
@RequiredArgsConstructor
public class AccessTokenFactory implements Function<Token, Token> {

    private final Duration durationOfAccess;

    @Override
    public Token apply(Token token) {
        log.info("Create access token");
        var momentOfCreated = Instant.now();

        var authorities = token.authorities().stream()
                .filter(authority -> authority.startsWith(PREFIX_ROLE_USER))
                .map(authority -> authority.substring(PREFIX_ROLE_USER.length()))
                .peek(log::info)
                .toList();

        return Token.builder()
                .id(token.id())
                .expiresAt(momentOfCreated.plus(durationOfAccess))
                .username(token.username())
                .createdAt(momentOfCreated)
                .authorities(authorities)
                .build();
    }
}
