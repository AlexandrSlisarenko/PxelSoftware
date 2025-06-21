package ru.slisarenko.pxelsoftware.security.serialization;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.slisarenko.pxelsoftware.security.dto.Token;

import java.util.Date;
import java.util.function.Function;

import static ru.slisarenko.pxelsoftware.config.Constants.JWS_ALGORITHM_SERIALIZATION;
import static ru.slisarenko.pxelsoftware.config.Constants.CLAIM_AUTHORITIES;

@Slf4j
@RequiredArgsConstructor
@Builder
public class AccessTokenStringSerialization implements Function<Token, String> {

    private final JWSSigner signer;

    @Override
    public String apply(Token token) {
        var jwtHeaders = new JWSHeader.Builder(JWS_ALGORITHM_SERIALIZATION)
                .keyID(token.id().toString())
                .build();
        var claimJwt = new JWTClaimsSet.Builder()
                .claim(CLAIM_AUTHORITIES, token.authorities())
                .jwtID(token.id().toString())
                .subject(token.username())
                .issueTime(Date.from(token.createdAt()))
                .expirationTime(Date.from(token.expiresAt()))
                .build();

        var signedJWT = new SignedJWT(jwtHeaders, claimJwt);
        log.info("Signed JWT: {}", signedJWT);
        try {
            signedJWT.sign(this.signer);
            log.info("Signed JWT sign: {}", signedJWT);
            return signedJWT.serialize();
        } catch (JOSEException e){
            log.error(e.getMessage(), e);
        }
        return "";
    }
}
