//package modo.auth;
//
//import modo.domain.entity.AccessToken;
//import modo.repository.AccessTokenRepository;
//import modo.repository.RefreshTokenRepository;
//import modo.service.CustomUserDetailService;
//import modo.service.RedisTokenService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//
//@SpringBootTest(classes = {JwtTokenProvider.class}, properties = {"jwtToken.secretKey=123}"})
//@EnableRedisRepositories
//public class JwtTokenProviderTest {
//    @Autowired
//    RedisTokenService redisTokenService;
//
//    @Autowired
//    CustomUserDetailService customUserDetailService;
//
//    @Autowired
//    AccessTokenRepository accessTokenRepository;
//
//    @Autowired
//    RefreshTokenRepository refreshTokenRepository;
//
//    JwtTokenProvider jwtTokenProvider;
//
//    @BeforeEach
//    void injectRepositoryToJwtTokenProvider() {
//        jwtTokenProvider = new JwtTokenProvider(customUserDetailService, redisTokenService);
//    }
//
//    @BeforeEach
//    void tearDown() {
//        accessTokenRepository.deleteAll();
//        refreshTokenRepository.deleteAll();
//    }
//
//    @Test
//    void Service_Access토큰저장_테스트() {
//        jwtTokenProvider.createAccessToken(testUsersId);
//
//        AccessToken target = accessTokenRepository.findById(testUsersId).orElseThrow(
//                ()-> new IllegalArgumentException("")
//        );
//
//        assertThat(target.getUsersId()).isEqualTo(testUsersId);
//    }
//
//    @Test
//    void Service_Refresh토큰저장_테스트() {
//
//    }
//
//    private final static String testUsersId = "testUsersId";
//
//}
