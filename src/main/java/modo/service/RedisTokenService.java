package modo.service;

import lombok.RequiredArgsConstructor;
import modo.domain.entity.AccessToken;
import modo.domain.entity.RefreshToken;
import modo.repository.AccessTokenRepository;
import modo.repository.RefreshTokenRepository;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RedisTokenService {
    private final AccessTokenRepository accessTokenRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    public void saveRefreshToken(String usersId, String value, Long duration) {
        RefreshToken refreshToken = RefreshToken.builder()
                .usersId(usersId)
                .tokenValue(value)
                .expiredTime(duration)
                .build();
        refreshTokenRepository.save(refreshToken);
    }

    public void saveAccessToken(String usersId, String value, Long duration) {
        AccessToken accessToken = AccessToken.builder()
                .usersId(usersId)
                .tokenValue(value)
                .expiredTime(duration)
                .build();
        accessTokenRepository.save(accessToken);
    }

    public String findRefreshToken(String key) {
        RefreshToken refreshToken = refreshTokenRepository.findById(key)
                .orElse(null);
        return refreshToken != null ? refreshToken.getTokenValue() : null;
    }

    public String findAccessToken(String key) {
        AccessToken accessToken = accessTokenRepository.findById(key)
                .orElse(null);
        return accessToken != null ? accessToken.getTokenValue() : null;
    }

    public void deleteRefreshToken(String key) {
        refreshTokenRepository.deleteById(key);
    }

    public void deleteAccessToken(String key) {
        accessTokenRepository.deleteById(key);
    }
}
