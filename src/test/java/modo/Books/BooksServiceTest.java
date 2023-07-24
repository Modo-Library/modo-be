package modo.Books;

import modo.domain.dto.books.BooksSaveRequestDto;
import modo.domain.entity.Books;
import modo.enums.BooksStatus;
import modo.repository.BooksRepository;
import modo.service.BooksService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

    BooksService booksService;

    @BeforeEach
    void tearDown() {
        booksRepository.deleteAllInBatch();
    }

    @BeforeEach
    void injectRepositoryToUsersService() {
        booksService = new BooksService(booksRepository);
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
                .build();

        //when
        booksService.save(requestDto);

        //then
        Books target = booksRepository.findAll().get(0);
        assertThat(target.getName()).isEqualTo(testName);
        assertThat(target.getPrice()).isEqualTo(testPrice);
        assertThat(target.getStatus()).isEqualTo(testStatus);
        assertThat(target.getDescription()).isEqualTo(testDescription);
        assertThat(target.getImgUrl()).isEqualTo(testImgUrl);
        assertThat(target.getCreatedAt()).isBeforeOrEqualTo(LocalDateTime.now());
        assertThat(target.getModifiedAt()).isBeforeOrEqualTo(LocalDateTime.now());
        assertThat(target.getDescription()).isNull();
    }

    static final String testName = "스프링으로 하는 마이크로서비스 구축";
    static final Long testPrice = 40000L;
    static final BooksStatus testStatus = BooksStatus.AVAILABLE;
    static final String testDescription = "완전 새 책";
    static final String testImgUrl = "s3://testImgUrl.com";

}
