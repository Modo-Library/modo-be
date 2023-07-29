package modo.Books;

import modo.domain.dto.books.BooksSaveRequestDto;
import modo.domain.dto.books.BooksUpdateRequestDto;
import modo.domain.dto.pictures.PicturesSaveRequestDto;
import modo.domain.dto.users.Users.UsersSaveRequestDto;
import modo.domain.entity.Books;
import modo.domain.entity.Pictures;
import modo.domain.entity.Users;
import modo.enums.BooksStatus;
import modo.repository.BooksRepository;
import modo.repository.PicturesRepository;
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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase
public class BooksServiceTest {

    @Autowired
    BooksRepository booksRepository;

    @Autowired
    UsersRepository usersRepository;

    @Autowired
    PicturesRepository picturesRepository;

    BooksService booksService;

    @BeforeEach
    void tearDown() {
        booksRepository.deleteAllInBatch();
    }

    @BeforeEach
    void injectRepositoryToUsersService() {
        booksService = new BooksService(booksRepository, usersRepository, picturesRepository);
    }

    @Test
    void 책저장_테스트() {
        //given
        PicturesSaveRequestDto requestDto1 = PicturesSaveRequestDto.builder()
                .imgUrl(testImgUrl + "1")
                .filename(testFilename + "1")
                .build();

        PicturesSaveRequestDto requestDto2 = PicturesSaveRequestDto.builder()
                .imgUrl(testImgUrl + "2")
                .filename(testFilename + "2")
                .build();

        List<PicturesSaveRequestDto> picturesSaveRequestDtoList = List.of(requestDto1, requestDto2);

        BooksSaveRequestDto requestDto = BooksSaveRequestDto.builder()
                .name(testName)
                .price(testPrice)
                .status(testStatus.toString())
                .description(testDescription)
                .imgUrl(testImgUrl)
                .usersId(testUsersId)
                .picturesSaveRequestDtoList(picturesSaveRequestDtoList)
                .build();

        usersRepository.save(testUsersSaveRequestDto.toEntity());

        //when
        booksService.save(requestDto);

        //then
        Books targetBooks = booksRepository.findAll().get(0);
        Users targetUsers = usersRepository.findAll().get(0);
        List<Pictures> targetPicturesList = picturesRepository.findAll();

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

        assertThat(targetPicturesList.size()).isEqualTo(2L);
        assertThat(targetPicturesList.get(0).getFilename()).isEqualTo(testFilename + "1");
        assertThat(targetPicturesList.get(0).getImgUrl()).isEqualTo(testImgUrl + "1");
        assertThat(targetPicturesList.get(1).getFilename()).isEqualTo(testFilename + "2");
        assertThat(targetPicturesList.get(1).getImgUrl()).isEqualTo(testImgUrl + "2");
    }

    @Test
    void 책업데이트_테스트() {
        saveTestBooksAndPicturesList();

        Long testBooksId = booksRepository.findAll().get(0).getBooksId();
        String testUpdateName = "update" + testName;
        Long testUpdatePrice = testPrice + 10000L;
        BooksStatus testUpdateStatus = BooksStatus.RENTING;
        String testUpdateDescription = "update" + testDescription;
        String testUpdateImgUrl = testImgUrl + "2";

        BooksUpdateRequestDto requestDto = BooksUpdateRequestDto.builder()
                .booksId(testBooksId)
                .name(testUpdateName)
                .price(testUpdatePrice)
                .status(testUpdateStatus.toString())
                .description(testUpdateDescription)
                .imgUrl(testUpdateImgUrl)
                .build();

        booksService.update(requestDto);

        Books target = booksRepository.findAll().get(0);

        assertThat(target.getName()).isEqualTo(testUpdateName);
        assertThat(target.getPrice()).isEqualTo(testUpdatePrice);
        assertThat(target.getStatus()).isEqualTo(testUpdateStatus);
        assertThat(target.getDescription()).isEqualTo(testUpdateDescription);
        assertThat(target.getImgUrl()).isEqualTo(testUpdateImgUrl);
        assertThat(target.getModifiedAt()).isNotEqualTo(target.getCreatedAt());

    }

    private void saveTestBooksAndPicturesList() {
        PicturesSaveRequestDto requestDto1 = PicturesSaveRequestDto.builder()
                .imgUrl(testImgUrl + "1")
                .filename(testFilename + "1")
                .build();

        PicturesSaveRequestDto requestDto2 = PicturesSaveRequestDto.builder()
                .imgUrl(testImgUrl + "2")
                .filename(testFilename + "2")
                .build();

        List<PicturesSaveRequestDto> picturesSaveRequestDtoList = List.of(requestDto1, requestDto2);

        BooksSaveRequestDto requestDto = BooksSaveRequestDto.builder()
                .name(testName)
                .price(testPrice)
                .status(testStatus.toString())
                .description(testDescription)
                .imgUrl(testImgUrl)
                .usersId(testUsersId)
                .picturesSaveRequestDtoList(picturesSaveRequestDtoList)
                .build();

        usersRepository.save(testUsersSaveRequestDto.toEntity());
        booksService.save(requestDto);
    }

    static final String testName = "스프링으로 하는 마이크로서비스 구축";
    static final Long testPrice = 40000L;
    static final BooksStatus testStatus = BooksStatus.AVAILABLE;
    static final String testDescription = "완전 새 책";
    static final String testImgUrl = "s3://testImgUrl.com";
    static final String testFilename = "testFilename.jpg";
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
