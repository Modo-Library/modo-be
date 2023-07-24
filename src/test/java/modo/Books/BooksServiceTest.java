package modo.Books;

import modo.domain.dto.books.BooksSaveRequestDto;
import modo.domain.dto.users.Users.UsersSaveRequestDto;
import modo.domain.entity.Books;
import modo.domain.entity.Users;
import modo.enums.BooksStatus;
import modo.repository.BooksRepository;
import modo.repository.UsersRepository;
import modo.service.BooksService;
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
public class BooksServiceTest {

    @Autowired
    BooksRepository booksRepository;

    @Autowired
    UsersRepository usersRepository;

    BooksService booksService;

    @BeforeEach
    void tearDown() {
        booksRepository.deleteAllInBatch();
    }

    @BeforeEach
    void injectRepositoryToUsersService() {
        booksService = new BooksService(booksRepository, usersRepository);
    }

    @Test
    void 책저장_테스트() {
        //given
        BooksSaveRequestDto requestDto = BooksSaveRequestDto.builder()
                .name(testName)
                .price(testPrice)
                .status(testStatus.toString())
                .description(testDescription)
                .imgUrl(testImgUrl)
                .usersId(testUsersId)
                .build();

        usersRepository.save(testUsersSaveRequestDto.toEntity());

        //when
        booksService.save(requestDto);

        //then
        Books targetBooks = booksRepository.findAll().get(0);
        Users targetUsers = usersRepository.findAll().get(0);

        assertThat(targetBooks.getName()).isEqualTo(testName);
        assertThat(targetBooks.getPrice()).isEqualTo(testPrice);
        assertThat(targetBooks.getStatus()).isEqualTo(testStatus);
        assertThat(targetBooks.getDescription()).isEqualTo(testDescription);
        assertThat(targetBooks.getImgUrl()).isEqualTo(testImgUrl);
        assertThat(targetBooks.getCreatedAt()).isBeforeOrEqualTo(LocalDateTime.now());
        assertThat(targetBooks.getModifiedAt()).isBeforeOrEqualTo(LocalDateTime.now());
        assertThat(targetBooks.getDeadline()).isNull();

        assertThat(targetUsers.getBooksList().size()).isEqualTo(1);
        assertThat(targetUsers.getBooksList().get(0).getName()).isEqualTo(testName);
    }

    static final String testName = "스프링으로 하는 마이크로서비스 구축";
    static final Long testPrice = 40000L;
    static final BooksStatus testStatus = BooksStatus.AVAILABLE;
    static final String testDescription = "완전 새 책";
    static final String testImgUrl = "s3://testImgUrl.com";
    static final String testUsersId = "testUsersId";
    static final String testNickname = "testNickname";
    static final Point testLocation = GeomUtil.createPoint(1.1, 2.2);
    static final double testX = 1.1;
    static final double testY = 2.2;
    static final double testReviewScore = 0.0;
    static final Long testReviewCount = 0L;

    static final UsersSaveRequestDto testUsersSaveRequestDto = UsersSaveRequestDto.builder()
            .usersId(testUsersId)
            .nickname(testNickname)
            .latitude(testX)
            .longitude(testY)
            .build();
}
