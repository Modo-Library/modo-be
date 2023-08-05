package modo.Books;

import modo.domain.entity.Books;
import modo.domain.entity.Users;
import modo.enums.BooksStatus;
import modo.repository.BooksRepository;
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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class BooksRepositoryTest {

    @Autowired
    BooksRepository booksRepository;

    @Autowired
    UsersRepository usersRepository;

    @Autowired
    UsersHistoryRepository usersHistoryRepository;

    @BeforeEach
    void tearDown() {
        usersHistoryRepository.deleteAllInBatch();
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

    @Test
    void Repository_책조회_특정이름포함_테스트() {
        saveTestUsers();
        Books books = Books.builder()
                .name(testName)
                .price(testPrice)
                .status(testStatus)
                .description(testDescription)
                .imgUrl(testImgUrl)
                .build();

        Books books1 = Books.builder()
                .name("testNameBook1")
                .price(testPrice)
                .status(testStatus)
                .description(testDescription)
                .imgUrl(testImgUrl)
                .build();

        booksRepository.save(books);
        booksRepository.save(books1);

        List<Books> result = booksRepository.findBooksByNameContaining(testName);
        assertThat(result.size()).isEqualTo(2L);

        result = booksRepository.findBooksByNameContaining("1");
        assertThat(result.size()).isEqualTo(1L);
    }

    @Test
    void Repository_거리책조회_테스트() {
        Books books1 = Books.builder()
                .name("testNameBook1")
                .price(testPrice)
                .status(testStatus)
                .description(testDescription)
                .imgUrl(testImgUrl)
                .build();
        Point testLocation1 = GeomUtil.createPoint(1.1, 2.2);
        books1.setLocation(testLocation1);

        Books books2 = Books.builder()
                .name("testNameBook2")
                .price(testPrice)
                .status(testStatus)
                .description(testDescription)
                .imgUrl(testImgUrl)
                .build();

        Point testLocation2 = GeomUtil.createPoint(2.2, 1.1);
        books2.setLocation(testLocation2);

        Books books3 = Books.builder()
                .name("testNameBook3")
                .price(testPrice)
                .status(testStatus)
                .description(testDescription)
                .imgUrl(testImgUrl)
                .build();

        Point testLocation3 = GeomUtil.createPoint(1.5, 1.5);
        books3.setLocation(testLocation3);

        booksRepository.save(books1);
        booksRepository.save(books2);
        booksRepository.save(books3);

        List<Books> result = booksRepository.findBooksWithDistance(1.5, 1.5, 1000);
        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    void Repository_거리책조회_특정이름포함_테스트() {
        Books books1 = Books.builder()
                .name("testNameBook1")
                .price(testPrice)
                .status(testStatus)
                .description(testDescription)
                .imgUrl(testImgUrl)
                .build();
        Point testLocation1 = GeomUtil.createPoint(1.1, 2.2);
        books1.setLocation(testLocation1);

        Books books2 = Books.builder()
                .name("testNameBook2")
                .price(testPrice)
                .status(testStatus)
                .description(testDescription)
                .imgUrl(testImgUrl)
                .build();

        Point testLocation2 = GeomUtil.createPoint(2.2, 1.1);
        books2.setLocation(testLocation2);

        Books books3 = Books.builder()
                .name("anotherTestNameBook3")
                .price(testPrice)
                .status(testStatus)
                .description(testDescription)
                .imgUrl(testImgUrl)
                .build();

        Point testLocation3 = GeomUtil.createPoint(1.5, 1.5);
        books3.setLocation(testLocation3);

        booksRepository.save(books1);
        booksRepository.save(books2);
        booksRepository.save(books3);

        List<Books> result = booksRepository.findBooksByNameContainingWithDistance(1.5, 1.5, 1000, "testName");
        assertThat(result.size()).isEqualTo(2);

        result = booksRepository.findBooksByNameContainingWithDistance(1.5, 1.5, 1000, "Book");
        assertThat(result.size()).isEqualTo(2);

        result = booksRepository.findBooksByNameContainingWithDistance(1.5, 1.5, 1000, "2");
        assertThat(result.size()).isEqualTo(1);
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
