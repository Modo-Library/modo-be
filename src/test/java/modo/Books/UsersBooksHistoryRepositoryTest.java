package modo.Books;

import modo.domain.entity.Books;
import modo.domain.entity.Users;
import modo.domain.entity.UsersBooksHistory;
import modo.enums.BooksStatus;
import modo.repository.*;
import modo.util.GeomUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class UsersBooksHistoryRepositoryTest {

    @Autowired
    UsersBooksHistoryRepository usersBooksHistoryRepository;

    @Autowired
    UsersRepository usersRepository;

    @Autowired
    BooksRepository booksRepository;

    @Autowired
    UsersReviewRepository usersReviewRepository;

    @Autowired
    UsersHistoryRepository usersHistoryRepository;

    @Autowired
    PicturesRepository picturesRepository;

    @BeforeEach
    void tearDown() {
        picturesRepository.deleteAllInBatch();
        usersReviewRepository.deleteAllInBatch();
        usersHistoryRepository.deleteAllInBatch();
        booksRepository.deleteAllInBatch();
        usersRepository.deleteAllInBatch();
        usersBooksHistoryRepository.deleteAllInBatch();
    }

    @Test
    void Repository_기록저장_테스트() {
        Users testUsers = saveTestUsers();
        Books testBooks = saveTestBooks();
        UsersBooksHistory usersBooksHistory = UsersBooksHistory.builder()
                .status(testStatus)
                .books(testBooks)
                .users(testUsers)
                .build();

        usersBooksHistoryRepository.save(usersBooksHistory);
        UsersBooksHistory target = usersBooksHistoryRepository.findAll().get(0);

        assertThat(target.getStatus()).isEqualTo(testStatus);
        assertThat(target.getUsers().getUsersId()).isEqualTo(testUsersId);
        assertThat(target.getBooks().getName()).isEqualTo(testName);

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
    static final BooksStatus testStatus = BooksStatus.AVAILABLE_RENT;
    static final LocalDateTime testDeadline = LocalDateTime.now().plusDays(7L);
    static final String testDescription = "testDescription";
    static final String testImgUrl = "testImgUrl";

    private Users saveTestUsers() {
        Users users = Users.builder()
                .usersId(testUsersId)
                .nickname(testNickname)
                .location(testLocation)
                .reviewScore(testReviewScore)
                .reviewCount(testReviewCount)
                .build();

        return usersRepository.save(users);
    }

    private Books saveTestBooks() {
        Books books = Books.builder()
                .name(testName)
                .price(testPrice)
                .status(testStatus)
                .description(testDescription)
                .imgUrl(testImgUrl)
                .build();

        return booksRepository.save(books);
    }
}
