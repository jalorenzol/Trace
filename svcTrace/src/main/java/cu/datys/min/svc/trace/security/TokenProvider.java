package cu.datys.min.svc.trace.security;

import cu.datys.bim.common.constants.SvcNamesConstants;
import cu.datys.bim.webclients.WebClientFactory;
import cu.datys.bim.webclients.auth.SecuredWebClient;
import cu.datys.bim.webclients.auth.dto.ValidationDTO;
import cu.datys.domain.bim.repository.auth.ActiveTokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Optional;

@Component
@Slf4j
public class TokenProvider {

    private final ActiveTokenRepository tokenRepository;
    private SecuredWebClient securedWebClient;

    public TokenProvider(ActiveTokenRepository tokenRepository,
                         WebClientFactory factory) {
        this.tokenRepository = tokenRepository;
        this.securedWebClient = factory.build(SvcNamesConstants.SVC_AUTH, SecuredWebClient.class)
                .orElse(null);
    }

    public Authentication getAuthentication(String token, ValidationDTO dto) {
//        Claims claims = Jwts.parser()
//                .setSigningKey(JwtConstants.TOKEN_SECRET)
//                .parseClaimsJws(token)
//                .getBody();
//
//        Collection<? extends GrantedAuthority> authorities =
//                Arrays.stream(claims.get(JwtConstants.ROLES_KEY).toString().split(","))
//                        .map(SimpleGrantedAuthority::new)
//                        .collect(Collectors.toList());

        User principal = new User(dto.getNombreUsuario(), "", Collections.emptyList());

        return new UsernamePasswordAuthenticationToken(principal, token, Collections.emptyList());
    }

    public ValidationDTO validateToken(String authToken) {
        return Optional.ofNullable(securedWebClient).map(securedWebClient1 -> {
            return securedWebClient1.validateToken(authToken);
        }).orElse(null).orElse(null);
//        if (!tokenRepository.findById(authToken).isPresent()) {
//            //log.error("Inactive JWT token.");
//            return false;
//        }
//        try {
//            Jwts.parser()
//                    .setSigningKey(JwtConstants.TOKEN_SECRET)
//                    .requireIssuer(JwtConstants.TOKEN_ISSUER)
//                    .parseClaimsJws(authToken);
//            return true;
//        } catch (Exception ex) {
//            //log.error(String.format("Invalid JWT: %s", ex.getMessage()));
//            tokenRepository.deleteById(authToken);
//        }
//        return false;
    }
}
