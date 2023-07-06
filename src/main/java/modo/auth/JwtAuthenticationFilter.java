package modo.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import modo.exception.authException.TokenIsExpiredException;
import modo.exception.authException.TokenIsNullException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@Log4j2
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException, TokenIsNullException, TokenIsExpiredException {

        // Resolve Token. If token field is null, throw TokenIsNullException
        String token = jwtTokenProvider.resolveToken((HttpServletRequest) request);

        // Validate Token. If token is expired, throw TokenIsExpiredException
        // If token signature is wrong, throw SignatureException
        jwtTokenProvider.validateToken(token);

        // Get Authentication information from JwtToken
        // If user is not found, throw UsernameNotFoundException
        Authentication authentication = jwtTokenProvider.getAuthentication(token);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        chain.doFilter(request, response);
    }
}
