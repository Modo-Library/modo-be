package modo.auth;

import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import modo.domain.dto.users.Users.UsersLoginResponseDto;
import modo.enums.TokenType;
import modo.exception.authException.ReIssueBeforeAccessTokenExpiredException;
import modo.exception.authException.RefreshTokenExpiredException;
import modo.exception.authException.TokenIsExpiredException;
import modo.exception.authException.TokenIsNullException;
import modo.service.CustomUserDetailService;
import modo.service.RedisTokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;

@Log4j2
@RequiredArgsConstructor
@Component
public class JwtTokenProvider {

    @Value("${jwtToken.secretKey}")
    private String secretKey;

    private final CustomUserDetailService userDetailService;
    private final RedisTokenService redisTokenService;

    private static long accessTokenValidTime = 60 * 60L;
    private static long refreshTokenValidTime = 30 * 3600 * 60L;

    public void setAccessTokenValidTime(Long time) {
        accessTokenValidTime = time;
    }

    public void setRefreshTokenValidTime(Long time) {
        refreshTokenValidTime = time;
    }

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    // Jwt Token 의 Subject 는 usersId, Id 는 usersId 가 저장
    private String createToken(TokenType tokenType, String usersId) {

        long tokenValidTime;
        if (tokenType == TokenType.AccessToken) tokenValidTime = accessTokenValidTime;
        else tokenValidTime = refreshTokenValidTime;

        Claims claims = Jwts.claims().setSubject(usersId);

        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + tokenValidTime * 1000L))
                .signWith(SignatureAlgorithm.HS256, secretKey).compact();
    }

    public String createAccessToken(String usersId) {
        String accessToken = createToken(TokenType.AccessToken, usersId);
        redisTokenService.saveAccessToken(usersId, accessToken, accessTokenValidTime);
        return accessToken;
    }

    public String createRefreshToken(String usersId) {
        String refreshToken = createToken(TokenType.RefreshToken, usersId);
        redisTokenService.saveRefreshToken(usersId, refreshToken, refreshTokenValidTime);
        return refreshToken;
    }

    public Authentication getAuthentication(String token) throws UsernameNotFoundException {
        UserDetails userDetails = userDetailService.loadUserByUsername(getUsersId(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String getUsersId(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
    }

    public String resolveToken(HttpServletRequest request) throws TokenIsNullException {
        String token = request.getHeader("Token");
        if (token != null) {
            return token;
        }
        throw new TokenIsNullException();
    }

    public void validateToken(String token) throws SignatureException, ExpiredJwtException {
        Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);

        if (!claims.getBody().getExpiration().before(new Date())) {
            return;
        }
    }

    private boolean checkRefreshTokenIsExpired(String token) {
        Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
        return claims.getBody().getExpiration().before(new Date());
    }

    private Boolean checkAccessTokenIsExpired(String token) {
        String usersId = getUsersId(token);
        return redisTokenService.findAccessToken(usersId) == null;
    }

    public UsersLoginResponseDto reIssue(String refreshToken) throws ReIssueBeforeAccessTokenExpiredException, RefreshTokenExpiredException {
        String usersId = getUsersId(refreshToken);

        // Request Reissue before AccessToken Expired
        if (!checkAccessTokenIsExpired(refreshToken)) {
            throw new ReIssueBeforeAccessTokenExpiredException("Access Token is still valid! Can't reIssue accessToken!");
        }

        // Request Reissue after RefreshToken Expired
        if (checkRefreshTokenIsExpired(refreshToken)) {
            throw new RefreshTokenExpiredException("Refresh Token is Expired! Please re-login and use new refreshToken!");
        }

        String accessToken = createAccessToken(usersId);

        return UsersLoginResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

}
