package modo.tokens;

import modo.configuration.RedisConfiguration;
import modo.domain.entity.AccessToken;
import modo.repository.AccessTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@EnableRedisRepositories
//@Import(RedisConfiguration.class)
//@DataJpaTest
@SpringBootTest
public class AccessTokenRepositoryTest {

    @Autowired
    AccessTokenRepository accessTokenRepository;

    @BeforeEach
    void tearDown() {
        accessTokenRepository.deleteAll();
    }

    @Test
    void Repository_AccessToken_생성_테스트() {
        //given
        saveNewToken();

        //when
        AccessToken target = accessTokenRepository.findById(testUsersId).orElseThrow(IllegalAccessError::new);

        //then
        assertThat(target.getTokenValue()).isEqualTo(testTokenValue);
    }

    @Test
    void Repository_AccessToken_조회실패_테스트() {
        assertThrows(IllegalArgumentException.class, () -> accessTokenRepository.findById("InvalidPK").orElseThrow(
                () -> new IllegalArgumentException("")
        ));
    }

    @Test
    void Repository_AccessToken_만료_테스트() {
        saveNewToken();
        //when
        try {
            Thread.sleep(AccessTokenValidTime * 1000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        assertThrows(IllegalArgumentException.class, () -> accessTokenRepository.findById(testUsersId).orElseThrow(
                () -> new IllegalArgumentException("")
        ));
    }

    private void saveNewToken() {
        accessTokenRepository.save(testAccessTokenEntity);
    }

    private static final String testUsersId = "test@gmail.com";
    private static final String testTokenValue = "testAccessToken";
    private static final Long AccessTokenValidTime = 1L;
    private static final AccessToken testAccessTokenEntity = AccessToken.builder()
            .usersId(testUsersId)
            .tokenValue(testTokenValue)
            .expiredTime(AccessTokenValidTime)
            .build();
}