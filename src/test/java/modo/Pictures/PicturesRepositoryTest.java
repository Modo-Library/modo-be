package modo.Pictures;

import modo.domain.entity.Books;
import modo.domain.entity.Pictures;
import modo.domain.entity.Users;
import modo.enums.BooksStatus;
import modo.repository.BooksRepository;
import modo.repository.PicturesRepository;
import modo.repository.UsersHistoryRepository;
import modo.repository.UsersRepository;
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
public class PicturesRepositoryTest {

    @Autowired
    PicturesRepository picturesRepository;

    @Autowired
    BooksRepository booksRepository;

    @Autowired
    UsersRepository usersRepository;

    @Autowired
    UsersHistoryRepository usersHistoryRepository;

    Books testBooks;

    @BeforeEach
    void tearDown() {
        usersHistoryRepository.deleteAllInBatch();
        usersRepository.deleteAllInBatch();
        booksRepository.deleteAllInBatch();
        picturesRepository.deleteAllInBatch();
    }

    @Test
    void Repository_사진저장_테스트() {
        Pictures pictures = Pictures.builder()
                .filename(testFilename)
                .imgUrl(testImgUrl)
                .build();

        picturesRepository.save(pictures);

        Pictures target = picturesRepository.findAll().get(0);

        assertThat(target.getImgUrl()).isEqualTo(testImgUrl);
        assertThat(target.getFilename()).isEqualTo(testFilename);
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

    // Test Pictures Information : static final variable
    static final String testFilename = "testFilename";


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

    private void saveTestBooks() {
        Books books = Books.builder()
                .name(testName)
                .price(testPrice)
                .status(testStatus)
                .description(testDescription)
                .imgUrl(testImgUrl)
                .build();

        testBooks = booksRepository.save(books);
    }
}
