package modo.users;

import modo.domain.entity.Users;
import modo.repository.UsersRepository;
import modo.util.GeomUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase
public class UsersRepositoryTest {
    @Autowired
    UsersRepository usersRepository;


    @BeforeEach
    void tearDown() {
        usersRepository.deleteAllInBatch();
    }

    @Test
    void Repository_회원가입및조회_테스트() {
        Users users = Users.builder()
                .usersId(testUsersId)
                .nickname(testNickname)
                .location(testLocation)
                .reviewScore(testReviewScore)
                .password(testPassword)
                .build();

        usersRepository.save(users);
        Users target = usersRepository.findById("testUsersId").orElseThrow(
                () -> new IllegalArgumentException("")
        );

        assertThat(target.getUsersId()).isEqualTo(testUsersId);
        assertThat(target.getNickname()).isEqualTo(testNickname);
        assertThat(target.getPassword()).isEqualTo(testPassword);
        assertThat(target.getReviewScore()).isEqualTo(testReviewScore);
        assertThat(target.getLocation().getX()).isEqualTo(testX);
        assertThat(target.getLocation().getY()).isEqualTo(testY);
    }

    @Test
    void Repository_아이디존재여부_테스트() {
        saveTestUsers();
        assertThat(usersRepository.existsByUsersId(testUsersId)).isTrue();
        assertThat(usersRepository.existsByUsersId("invalidTestUsersId")).isFalse();
    }

    @Test
    void Repository_닉네임존재여부_테스트() {
        saveTestUsers();
        assertThat(usersRepository.existsByNickname(testNickname)).isTrue();
        assertThat(usersRepository.existsByNickname("invalidTestNickname")).isFalse();
    }

    static final String testUsersId = "testUsersId";
    static final String testPassword = "testPassword";
    static final String testNickname = "testNickname";
    static final Point testLocation = GeomUtil.createPoint(1.1, 2.2);
    static final double testX = 1.1;
    static final double testY = 2.2;
    static final double testReviewScore = 0.0;


    private void saveTestUsers() {
        Users users = Users.builder()
                .usersId(testUsersId)
                .nickname(testNickname)
                .location(testLocation)
                .reviewScore(testReviewScore)
                .password(testPassword)
                .build();

        usersRepository.save(users);
    }
}
