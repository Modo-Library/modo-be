package modo.auth;

import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import modo.enums.TokenType;
import modo.service.CustomUserDetailService;
import modo.service.RedisTokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
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

    private static long accessTokenValidTime = 3600 * 60L;
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
//        claims.setId(usersId);
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

    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userDetailService.loadUserByUsername(getUID(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String getUID(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
    }

    public String getUsersId(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
    }

    public String resolveToken(HttpServletRequest request) throws Exception {
        String token = request.getHeader("Token");
        if (token == null) {
            log.warn("token is null");
            throw new Exception();
        }
        return token;
    }

    public boolean validateToken(String token) throws Exception {
        Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
        return !claims.getBody().getExpiration().before(new Date());
    }

    private boolean checkRefreshTokenIsExpired(String token) {
        Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
        return claims.getBody().getExpiration().before(new Date());
    }

    private Boolean checkAccessTokenIsExpired(String token) {
        String usersId = getUID(token);
        return redisTokenService.findAccessToken(usersId) == null;
    }

    public String reIssue(HttpServletRequest request) throws Exception {
        String refreshToken = resolveToken(request);

        // RefreshToken is not expired, throw exception
        if (checkRefreshTokenIsExpired(refreshToken))
            throw new Exception();

        // AccessToken is expired, throw exception
        if (!checkAccessTokenIsExpired(refreshToken))
            throw new Exception();

        String UID = getUID(refreshToken);
        String usersId = getUsersId(refreshToken);

        return createAccessToken(usersId);
    }

}
