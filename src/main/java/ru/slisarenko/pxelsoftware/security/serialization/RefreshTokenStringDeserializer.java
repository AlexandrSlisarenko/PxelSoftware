package ru.slisarenko.pxelsoftware.security.serialization;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEDecrypter;
import com.nimbusds.jwt.EncryptedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.slisarenko.pxelsoftware.security.dto.Token;

import java.text.ParseException;
import java.util.UUID;
import java.util.function.Function;

import static ru.slisarenko.pxelsoftware.config.Constants.CLAIM_AUTHORITIES;

@Slf4j
@RequiredArgsConstructor
public class RefreshTokenStringDeserializer implements Function<String, Token>{

    private final JWEDecrypter decrypter;

    @Override
    public Token apply(String stringToken) {
        try{
            var encryptedJWT = EncryptedJWT.parse(stringToken);
            encryptedJWT.decrypt(this.decrypter);
            log.debug("Decrypted JWT: {}", encryptedJWT);
            var claims = encryptedJWT.getJWTClaimsSet();
            return Token.builder()
                    .id(UUID.fromString(claims.getJWTID()))
                    .username(claims.getSubject())
                    .authorities(claims.getStringListClaim(CLAIM_AUTHORITIES))
                    .createdAt(claims.getIssueTime().toInstant())
                    .expiresAt(claims.getExpirationTime().toInstant())
                    .build();

        }catch (ParseException | JOSEException e) {
            log.error(e.getMessage(), e);
        }

        return null;
    }
}
