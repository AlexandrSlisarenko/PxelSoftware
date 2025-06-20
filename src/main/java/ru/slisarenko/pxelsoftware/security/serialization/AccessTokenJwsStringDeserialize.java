package ru.slisarenko.pxelsoftware.security.serialization;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jwt.SignedJWT;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.slisarenko.pxelsoftware.security.dto.Token;

import java.text.ParseException;
import java.util.UUID;
import java.util.function.Function;

import static ru.slisarenko.pxelsoftware.config.Constants.CLAIM_AUTHORITIES;

@Slf4j
@RequiredArgsConstructor
@Builder
public class AccessTokenJwsStringDeserialize implements Function<String, Token> {

    private final JWSVerifier verifier;

    @Override
    public Token apply(String stringToken) {

        try {
            var signedJWT = SignedJWT.parse(stringToken);
            if (signedJWT.verify(this.verifier)) {
                var claims = signedJWT.getJWTClaimsSet();
                return Token.builder()
                        .id(UUID.fromString(claims.getJWTID()))
                        .username(claims.getSubject())
                        .authorities(claims.getStringListClaim(CLAIM_AUTHORITIES))
                        .createdAt(claims.getIssueTime().toInstant())
                        .expiresAt(claims.getExpirationTime().toInstant())
                        .build();

            }
        } catch (ParseException | JOSEException exception) {
            log.error(exception.getMessage(), exception);
        }

        return null;
    }
}
