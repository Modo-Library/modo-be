package modo.Books;

import lombok.extern.log4j.Log4j2;
import modo.domain.dto.books.EachBooksResponseDto;
import modo.domain.entity.Books;
import modo.enums.BooksStatus;
import modo.repository.BooksRepository;
import modo.repository.UsersHistoryRepository;
import modo.repository.UsersRepository;
import modo.util.GeomUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.StopWatch;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("performanceTest")
@Log4j2
public class BooksRepositoryPerformanceTest {
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

    @Disabled
    @Test
    void Repository_Performance_만개저장되어있을때_10개조회_테스트() {

        double latitude = 50.0;
        double longitude = 50.0;

        for (int i = 0; i < 10000; i++) {
            latitude += 0.000001;
            longitude -= 0.000001;
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

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        List<EachBooksResponseDto> result = booksRepository.findBooksWithDistanceWithNoOffset(50, 50, 3000, 0, 1L)
                .stream()
                .map(each -> new EachBooksResponseDto(each))
                .collect(Collectors.toList());

        stopWatch.stop();
        log.info(stopWatch.getTotalTimeMillis());
    }

    @Disabled
    @Test
    void Repository_Performance_만개저장되어있을때_10개조회_특정이름포함_테스트() {

        double latitude = 50.0;
        double longitude = 50.0;

        for (int i = 0; i < 10000; i++) {
            latitude += 0.000001;
            longitude -= 0.000001;
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

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        List<EachBooksResponseDto> result = booksRepository.findBooksByNameContainingWithDistanceWithNoOffset(50, 50, 3000, "Distance", 0, 1L)
                .stream()
                .map(each -> new EachBooksResponseDto(each))
                .collect(Collectors.toList());

        stopWatch.stop();
        log.info(stopWatch.getTotalTimeMillis());
    }

    @Disabled
    @Test
    void Repository_Performance_십만개저장되어있을때_10개조회_테스트() {

        double latitude = 50.0;
        double longitude = 50.0;

        for (int i = 0; i < 100000; i++) {
            latitude += 0.000001;
            longitude -= 0.000001;
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

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        List<EachBooksResponseDto> result = booksRepository.findBooksWithDistanceWithNoOffset(50, 50, 3000, 0, 1L)
                .stream()
                .map(each -> new EachBooksResponseDto(each))
                .collect(Collectors.toList());

        stopWatch.stop();
        log.info(stopWatch.getTotalTimeMillis());
    }

    @Disabled
    @Test
    void Repository_Performance_십만개저장되어있을때_10개조회_특정이름포함_테스트() {

        double latitude = 50.0;
        double longitude = 50.0;

        for (int i = 0; i < 100000; i++) {
            latitude += 0.000001;
            longitude -= 0.000001;
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

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        List<EachBooksResponseDto> result = booksRepository.findBooksByNameContainingWithDistanceWithNoOffset(50, 50, 3000, "Distance", 0, 1L)
                .stream()
                .map(each -> new EachBooksResponseDto(each))
                .collect(Collectors.toList());

        stopWatch.stop();
        log.info(stopWatch.getTotalTimeMillis());
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
}
