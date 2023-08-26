package modo.users;

import modo.domain.entity.Users;
import modo.repository.*;
import modo.util.GeomUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class UsersRepositoryTest {
    @Autowired
    UsersRepository usersRepository;

    @Autowired
    UsersHistoryRepository usersHistoryRepository;

    @Autowired
    BooksRepository booksRepository;

    @Autowired
    PicturesRepository picturesRepository;

    @Autowired
    private ChatRoomsRepository chatRoomsRepository;

    @Autowired
    private ChatMessagesRepository chatMessagesRepository;


    @BeforeEach
    void tearDown() {
        chatMessagesRepository.deleteAllInBatch();
        chatRoomsRepository.deleteAllInBatch();
        picturesRepository.deleteAllInBatch();
        booksRepository.deleteAllInBatch();
        usersHistoryRepository.deleteAllInBatch();
        usersRepository.deleteAllInBatch();
    }

    @Test
    void Repository_회원가입및조회_테스트() {
        Users users = Users.builder()
                .usersId(testUsersId)
                .nickname(testNickname)
                .location(testLocation)
                .reviewScore(testReviewScore)
                .reviewCount(testReviewCount)
                .build();

        usersRepository.save(users);
        Users target = usersRepository.findById("testUsersId").orElseThrow(
                () -> new IllegalArgumentException("")
        );

        assertThat(target.getUsersId()).isEqualTo(testUsersId);
        assertThat(target.getNickname()).isEqualTo(testNickname);
        assertThat(target.getReviewScore()).isEqualTo(testReviewScore);
        assertThat(target.getLocation().getX()).isEqualTo(testY);
        assertThat(target.getLocation().getY()).isEqualTo(testX);
        assertThat(target.getReviewCount()).isEqualTo(testReviewCount);
    }

    @Test
    void Repository_회원조회_실패_테스트() {
        assertThrows(IllegalArgumentException.class, () -> usersRepository.findById(testUsersId).orElseThrow(() -> new IllegalArgumentException("")));
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
    static final Long testReviewCount = 0L;


    private void saveTestUsers() {
        Users users = Users.builder()
                .usersId(testUsersId)
                .nickname(testNickname)
                .location(testLocation)
                .reviewScore(testReviewScore)
                .reviewCount(testReviewCount)
                .build();

        usersRepository.save(users);
    }
}
