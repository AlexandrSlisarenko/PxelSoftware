package ru.slisarenko.pxelsoftware.security.converter;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import ru.slisarenko.pxelsoftware.security.dto.Token;


import java.util.Objects;
import java.util.function.Function;

import static ru.slisarenko.pxelsoftware.config.Constants.AUTHORIZATION_SCHEMA;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationConverter implements AuthenticationConverter {

    private final Function<String, Token> accessTokenDeserializer;

    private final Function<String, Token> refreshTokenDeserializer;

    @Override
    public Authentication convert(HttpServletRequest request) {
        Authentication token = null;
        var authorization = request.getHeader("Authorization");
        if (Objects.nonNull(authorization) && authorization.startsWith(AUTHORIZATION_SCHEMA)) {
            var stringToken = authorization.substring(AUTHORIZATION_SCHEMA.length());
            log.info("stringToken = {}", stringToken);
            var accessToken = this.accessTokenDeserializer.apply(stringToken);
            if(Objects.nonNull(accessToken)) {
                token = new PreAuthenticatedAuthenticationToken(accessToken, stringToken);
                return token;
            }
            var refreshToken = this.refreshTokenDeserializer.apply(stringToken);
            if(Objects.nonNull(refreshToken)) {
                token = new PreAuthenticatedAuthenticationToken(refreshToken, stringToken);
                return token;
            }
        }

        return token;
    }
}
