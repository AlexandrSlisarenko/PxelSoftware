package ru.slisarenko.pxelsoftware.security.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import ru.slisarenko.pxelsoftware.security.dto.Token;
import ru.slisarenko.pxelsoftware.security.dto.TokenUser;
import ru.slisarenko.pxelsoftware.security.exception.TokenAuthenticationUserDetailsServiceException;
import ru.slisarenko.pxelsoftware.security.repository.JdbcTokenLogoutRepository;

import java.time.Instant;

@RequiredArgsConstructor
public class TokenAuthenticationUserDetailsService implements AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken> {

    private final JdbcTokenLogoutRepository repository;

    @Override
    public UserDetails loadUserDetails(PreAuthenticatedAuthenticationToken authenticationToken) throws UsernameNotFoundException {

        if(authenticationToken.getPrincipal() instanceof Token token){
            try{
            var userPasswordFromDB = repository.getPassword(token.username());
            var indexStart = userPasswordFromDB.indexOf("}") + 1;
            var userPassword = userPasswordFromDB.substring(indexStart, userPasswordFromDB.length() - 1);
            return new TokenUser(token.username(),
                    userPassword,
                    true,
                    true,
                    !repository.isDeactivatedToken(token.id()) &&
                    token.expiresAt().isAfter(Instant.now()),
                    true,
                    token.authorities().stream()
                            .map(SimpleGrantedAuthority::new)
                            .toList(),
                    token
            );
            }catch (Exception e){
                throw new TokenAuthenticationUserDetailsServiceException(e.getMessage(), e);
            }
        }
        throw new TokenAuthenticationUserDetailsServiceException("Principal mast be type Token");
    }
}
