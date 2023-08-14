package modo.Books;

import lombok.extern.log4j.Log4j2;
import modo.domain.dto.books.EachBooksResponseDto;
import modo.domain.entity.Books;
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
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Log4j2
public class BooksRepositoryTest {

    @Autowired
    BooksRepository booksRepository;

    @Autowired
    UsersRepository usersRepository;

    @Autowired
    UsersHistoryRepository usersHistoryRepository;

    @Autowired
    PicturesRepository picturesRepository;

    @BeforeEach
    void tearDown() {
        usersHistoryRepository.deleteAllInBatch();
        picturesRepository.deleteAllInBatch();
        booksRepository.deleteAllInBatch();
        usersRepository.deleteAllInBatch();
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
    void Repository_책리스트조회_특정이름포함_처음10개조회후_마지막조회값과거리가같은신규10개저장_테스트() {

        // 처음에는 조회하는 좌표와 좌표값이 같은 (distance 값이 0인) 책 10개 저장
        // 이후에는 조회하는 좌표와 좌표값이 점점 멀어지는 책 10개 저장
        // 이까지 저장한 다음 책 10개를 1번 조회, 이때 10번째 id를 저장해둠
        // 그다음에는 조회하는 좌표와 좌표값이 같은 (distance 값이 0인) 책 10개 다시 저장
        // 다시 책 10개를 조회했을 때, 10개의 책이 모두 distance 가 0인지 검증

        double latitude = 50.0;
        double longitude = 50.0;

        for (int i = 0; i < 10; i++) {
            Books books = Books.builder()
                    .name("Distance is 0")
                    .price(testPrice)
                    .status(testStatus)
                    .description(testDescription)
                    .imgUrl(testImgUrl)
                    .build();
            Point testLocation = GeomUtil.createPoint(50.0, 50.0);
            books.setLocation(testLocation);

            booksRepository.save(books);
        }

        for (int i = 0; i < 10; i++) {
            latitude += 0.01;
            longitude -= 0.01;

            Books books = Books.builder()
                    .name("Distance is increasing")
                    .price(testPrice)
                    .status(testStatus)
                    .description(testDescription)
                    .imgUrl(testImgUrl)
                    .build();
            Point testLocation = GeomUtil.createPoint(latitude, longitude);
            books.setLocation(testLocation);

            booksRepository.save(books);
        }


        List<EachBooksResponseDto> result = booksRepository.findBooksByNameContainingWithDistanceWithNoOffset(50, 50, 100000, "Distance", 0, 1L)
                .stream()
                .map(each -> new EachBooksResponseDto(each))
                .collect(Collectors.toList());
        assertThat(result.size()).isEqualTo(10L);

        result.stream()
                .forEach(books -> {
                    log.info(books.getBooksId());
                    log.info(books.getName());
                });

        EachBooksResponseDto lastBooks = result.get(9);
        Long startId = lastBooks.getBooksId();
        int lastDistance = lastBooks.getDistance();


        for (int i = 0; i < 10; i++) {
            Books duplicate = Books.builder()
                    .name("Distance is 0")
                    .price(testPrice)
                    .status(testStatus)
                    .description(testDescription)
                    .imgUrl(testImgUrl)
                    .build();
            Point testLocation = GeomUtil.createPoint(50.0, 50.0);
            duplicate.setLocation(testLocation);
            booksRepository.save(duplicate);
        }

        result = booksRepository.findBooksByNameContainingWithDistanceWithNoOffset(50, 50, 100000, "Distance", lastDistance, startId)
                .stream()
                .map(each -> new EachBooksResponseDto(each))
                .collect(Collectors.toList());

        // 다시 조회한 10개의 리스트의 책의 거리가 모두 0인지 조회
        result.stream()
                .forEach(books -> {
                    log.info(books.getBooksId());
                    log.info(books.getName());
                    assertThat(books.getDistance()).isEqualTo(0);
                    assertThat(books.getBooksId()).isGreaterThan(startId);
                });
    }

    @Test
    void Repository_책리스트조회_특정이름포함_이름A인5개책과_이름B인5개책저장시_A로검색_테스트() {

        // name=A 인 책 5개와 name=B 인 책 5개 저장
        // searchingWord=A 로 검색했을 때 결과가 5개인지 검증

        double latitude = 50.0;
        double longitude = 50.0;

        for (int i = 0; i < 5; i++) {
            latitude += 0.01;
            longitude -= 0.01;

            Books books = Books.builder()
                    .name("A")
                    .price(testPrice)
                    .status(testStatus)
                    .description(testDescription)
                    .imgUrl(testImgUrl)
                    .build();
            Point testLocation = GeomUtil.createPoint(latitude, longitude);
            books.setLocation(testLocation);

            booksRepository.save(books);
        }

        for (int i = 0; i < 5; i++) {
            Books books = Books.builder()
                    .name("B")
                    .price(testPrice)
                    .status(testStatus)
                    .description(testDescription)
                    .imgUrl(testImgUrl)
                    .build();
            Point testLocation = GeomUtil.createPoint(50.0, 50.0);
            books.setLocation(testLocation);

            booksRepository.save(books);
        }

        List<EachBooksResponseDto> result = booksRepository.findBooksByNameContainingWithDistanceWithNoOffset(50, 50, 100000, "A", 0, 1L)
                .stream()
                .map(each -> new EachBooksResponseDto(each))
                .collect(Collectors.toList());

        assertThat(result.size()).isEqualTo(5L);
    }

    @Test
    void Repository_책리스트조회_noOffset_페이지네이션_테스트() {

        // 처음에는 조회하는 좌표와 좌표값이 같은 (distance 값이 0인) 책 15개 저장
        // 이후에는 조회하는 좌표와 좌표값이 점점 멀어지는 책 15개 저장
        // 이까지 저장한 다음 책 10개를 2번 조회, 이때 20번째 id를 저장해둠
        // 그다음에는 조회하는 좌표와 좌표값이 같은 (distance 값이 0인) 책 10개 다시 저장
        // 다시 책 10개를 조회했을 때, 처음으로 나오는 책의 아이디가 (20번째 id+1) 과 같은지 검증
        // 10개를 조회했지만 결과는 5개가 나오는지 검증

        double latitude = 50.0;
        double longitude = 50.0;

        for (int i = 0; i < 15; i++) {
            Books books = Books.builder()
                    .name("Same Distance")
                    .price(testPrice)
                    .status(testStatus)
                    .description(testDescription)
                    .imgUrl(testImgUrl)
                    .build();
            Point testLocation = GeomUtil.createPoint(50.0, 50.0);
            books.setLocation(testLocation);

            booksRepository.save(books);
        }

        for (int i = 0; i < 15; i++) {
            latitude += 0.001;
            longitude -= 0.001;

            Books books = Books.builder()
                    .name("Different Distance")
                    .price(testPrice)
                    .status(testStatus)
                    .description(testDescription)
                    .imgUrl(testImgUrl)
                    .build();
            Point testLocation = GeomUtil.createPoint(latitude, longitude);
            books.setLocation(testLocation);

            booksRepository.save(books);
        }


        List<EachBooksResponseDto> result = booksRepository.findBooksWithDistanceWithNoOffset(50, 50, 3000, 0, 1L)
                .stream()
                .map(each -> new EachBooksResponseDto(each))
                .collect(Collectors.toList());

        assertThat(result.size()).isEqualTo(10L);
        result.stream()
                .forEach(books -> {
                    log.info(books.getBooksId());
                    log.info(books.getName());
                });
        EachBooksResponseDto lastBooks = result.get(9);
        Long startId = lastBooks.getBooksId();
        int lastDistance = lastBooks.getDistance();

        result = booksRepository.findBooksWithDistanceWithNoOffset(50, 50, 100000, lastDistance, startId)
                .stream()
                .map(each -> new EachBooksResponseDto(each))
                .collect(Collectors.toList());

        // 리스트의 첫 번째 결과가 parameter 로 넣은 startId 보다 1 큰지 검증
        assertThat(startId + 1).isEqualTo(result.get(0).getBooksId());

        result.stream()
                .forEach(books -> {
                    log.info(books.getBooksId());
                    log.info(books.getName());
                });
        lastBooks = result.get(9);
        startId = lastBooks.getBooksId();
        lastDistance = lastBooks.getDistance();


        for (int i = 0; i < 10; i++) {
            Books duplicate = Books.builder()
                    .name("New Distance")
                    .price(testPrice)
                    .status(testStatus)
                    .description(testDescription)
                    .imgUrl(testImgUrl)
                    .build();
            Point testLocation = GeomUtil.createPoint(50.0, 50.0);
            duplicate.setLocation(testLocation);
            booksRepository.save(duplicate);
        }

        result = booksRepository.findBooksWithDistanceWithNoOffset(50, 50, 100000, lastDistance, startId)
                .stream()
                .map(each -> new EachBooksResponseDto(each))
                .collect(Collectors.toList());
        result.stream()
                .forEach(books -> {
                    log.info(books.getBooksId());
                    log.info(books.getName());
                });

        assertThat(startId + 1).isEqualTo(result.get(0).getBooksId());
    }

    @Test
    void Repository_책상세조회_테스트() {
        Books books = Books.builder()
                .name("testNameBook1")
                .price(testPrice)
                .status(testStatus)
                .description(testDescription)
                .imgUrl(testImgUrl)
                .build();

        Point testLocation = GeomUtil.createPoint(1.1, 2.2);
        books.setLocation(testLocation);

        booksRepository.save(books);

        Books result = booksRepository.findBooks(booksRepository.findAll().get(0).getBooksId())
                .orElseThrow(() -> new IllegalArgumentException(""));

        assertThat(result.getName()).isEqualTo("testNameBook1");
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
