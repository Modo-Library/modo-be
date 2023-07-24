package modo.Books;

import modo.domain.entity.Books;
import modo.domain.entity.Users;
import modo.enums.BooksStatus;
import modo.repository.BooksRepository;
import modo.repository.UsersRepository;
import modo.util.GeomUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase
public class BooksRepositoryTest {

    @Autowired
    BooksRepository booksRepository;

    @Autowired
    UsersRepository usersRepository;

    @BeforeEach
    void tearDown() {
        usersRepository.deleteAllInBatch();
        booksRepository.deleteAllInBatch();
    }

    @Test
    void Repository_책저장_테스트() {
        saveTestUsers();
        Books books = Books.builder()
                .name(testName)
                .price(testPrice)
                .status(testStatus)
                .description(testDescription)
                .imgUrl(testImgUrl)
                .build();

        booksRepository.save(books);
        Books target = booksRepository.findAll().get(0);

        assertThat(target.getName()).isEqualTo(testName);
        assertThat(target.getPrice()).isEqualTo(testPrice);
        assertThat(target.getStatus()).isEqualTo(testStatus);
        assertThat(target.getDescription()).isEqualTo(testDescription);
        assertThat(target.getImgUrl()).isEqualTo(testImgUrl);
        assertThat(target.getCreatedAt()).isBeforeOrEqualTo(LocalDateTime.now());
        assertThat(target.getModifiedAt()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    // Test Users Information : static final variable
    static final String testUsersId = "testUsersId";
    static final String testPassword = "testPassword";
    static final String testNickname = "testNickname";
    static final Point testLocation = GeomUtil.createPoint(1.1, 2.2);
    static final double testX = 1.1;
    static final double testY = 2.2;
    static final double testReviewScore = 0.0;
    static final Long testReviewCount = 0L;

    // Test Books Information : static final variable
    static final String testName = "testName";
    static final Long testPrice = 1000L;
    static final BooksStatus testStatus = BooksStatus.AVAILABLE;
    static final LocalDateTime testDeadline = LocalDateTime.now().plusDays(7L);
    static final String testDescription = "testDescription";
    static final String testImgUrl = "testImgUrl";


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
