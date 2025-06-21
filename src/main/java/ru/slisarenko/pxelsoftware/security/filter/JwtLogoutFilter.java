package ru.slisarenko.pxelsoftware.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Builder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.slisarenko.pxelsoftware.security.dto.TokenUser;
import ru.slisarenko.pxelsoftware.security.repository.JdbcTokenLogoutRepository;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.Date;

import static ru.slisarenko.pxelsoftware.config.Constants.JWT_LOGOUT;

@Builder
public class JwtLogoutFilter extends OncePerRequestFilter {

    private RequestMatcher requestMatcher;

    private SecurityContextRepository securityContextRepository;

    private JdbcTokenLogoutRepository jdbcTokenLogoutRepository;

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
                    context.getAuthentication().getPrincipal() instanceof TokenUser token &&
                    context.getAuthentication().getAuthorities()
                            .contains(new SimpleGrantedAuthority(JWT_LOGOUT))) {
                    jdbcTokenLogoutRepository.insertDeactivatedToken(token.getToken().id(),
                            Date.from(token.getToken().expiresAt()));

                    response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                    return;
                }
            }
            throw new AccessDeniedException("User must be authenticated with JWT");
        }
        filterChain.doFilter(request, response);
    }
}
