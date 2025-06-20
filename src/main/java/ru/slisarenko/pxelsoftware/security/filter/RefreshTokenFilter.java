package ru.slisarenko.pxelsoftware.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.slisarenko.pxelsoftware.security.dto.AccessAndRefreshToken;
import ru.slisarenko.pxelsoftware.security.dto.Token;
import ru.slisarenko.pxelsoftware.security.dto.TokenUser;


import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.function.Function;

import static ru.slisarenko.pxelsoftware.config.Constants.JWT_REFRESH;

@RequiredArgsConstructor
@Builder
public class RefreshTokenFilter extends OncePerRequestFilter {

    private final RequestMatcher requestMatcher;

    private final SecurityContextRepository securityContextRepository;

    private final Function<Token, Token> accessTokenFactory;

    private final Function<Token, String> accessTokenStringSerializer;

    private final ObjectMapper objectMapper;


    /**
     * Same contract as for {@code doFilter}, but guaranteed to be
     * just invoked once per request within a single request thread.
     * See {@link #shouldNotFilterAsyncDispatch()} for details.
     * <p>Provides HttpServletRequest and HttpServletResponse arguments instead of the
     * default ServletRequest and ServletResponse ones.
     *
     * @param request
     * @param response
     * @param filterChain
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (this.requestMatcher.matches(request)) {
            if (this.securityContextRepository.containsContext(request)) {
                var context = this.securityContextRepository.loadDeferredContext(request).get();
                if (context != null && context.getAuthentication() instanceof PreAuthenticatedAuthenticationToken &&
                    context.getAuthentication().getPrincipal() instanceof TokenUser user &&
                    context.getAuthentication().getAuthorities()
                            .contains(new SimpleGrantedAuthority(JWT_REFRESH))) {
                    var accessToken = this.accessTokenFactory.apply(user.getToken());

                    response.setStatus(HttpServletResponse.SC_OK);
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    this.objectMapper.writeValue(response.getWriter(),
                            AccessAndRefreshToken.builder()
                                    .accessToken(this.accessTokenStringSerializer.apply(accessToken))
                                    .expiresAtAccessToken(accessToken.expiresAt().toString())
                                    .refreshToken(null)
                                    .expiresAtRefreshToken(null)
                                    .build());
                    return;
                }
            }

            throw new AccessDeniedException("User must be authenticated with JWT");
        }

        filterChain.doFilter(request, response);

    }
}
