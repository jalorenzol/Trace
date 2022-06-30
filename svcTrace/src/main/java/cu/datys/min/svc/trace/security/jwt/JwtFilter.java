package cu.datys.min.svc.trace.security.jwt;

import cu.datys.bim.common.constants.JwtConstants;
import cu.datys.bim.webclients.auth.dto.ValidationDTO;
import cu.datys.min.svc.trace.security.TokenProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Optional;

/**
 * Filters incoming requests and installs a Spring Security principal if a header corresponding to a valid user is
 * found.
 */
public class JwtFilter extends GenericFilterBean {

    private TokenProvider tokenProvider;

    public JwtFilter(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        String jwt = resolveToken(request);
        ValidationDTO dto = this.tokenProvider.validateToken(jwt);
        if (StringUtils.hasText(jwt) && Optional.ofNullable(dto).isPresent()) {
            Authentication authentication = this.tokenProvider.getAuthentication(jwt, dto);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        chain.doFilter(req, res);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(JwtConstants.AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(JwtConstants.TOKEN_PREFIX)) {
            return bearerToken.substring(JwtConstants.TOKEN_PREFIX.length());
        } else {
            return request.getHeader(JwtConstants.XAUTH_HEADER);
        }
    }
}
