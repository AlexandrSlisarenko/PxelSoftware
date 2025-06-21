package ru.slisarenko.pxelsoftware.security.serialization;

import com.nimbusds.jose.*;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.JWTClaimsSet;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.slisarenko.pxelsoftware.security.dto.Token;

import java.util.Date;
import java.util.function.Function;

import static ru.slisarenko.pxelsoftware.config.Constants.*;

@Slf4j
@RequiredArgsConstructor
@Builder
public class RefreshTokenStringSerializer implements Function<Token, String> {

    private final JWEEncrypter encrypter;

    @Override
    public String apply(Token token) {
        var jwtHeaders = new JWEHeader.Builder(JWE_ALGORITHM_SERIALIZATION, ENCRYPTION_METHOD_SERIALIZATION)
                .keyID(token.id().toString())
                .build();
        var claimJwt = new JWTClaimsSet.Builder()
                .claim(CLAIM_AUTHORITIES, token.authorities())
                .jwtID(token.id().toString())
                .subject(token.username())
                .issueTime(Date.from(token.createdAt()))
                .expirationTime(Date.from(token.expiresAt()))
                .build();

        var encryptedJWT = new EncryptedJWT(jwtHeaders, claimJwt);

        try {
            encryptedJWT.encrypt(this.encrypter);
            log.info("Encrypted JWT: {}", encryptedJWT);
            return encryptedJWT.serialize();
        } catch (JOSEException e){
            log.error(e.getMessage(), e);
        }

        return "";
    }
}
