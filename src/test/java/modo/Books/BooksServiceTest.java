package modo.Books;

import lombok.extern.log4j.Log4j2;
import modo.auth.JwtTokenProvider;
import modo.domain.dto.books.BooksPageResponseDto;
import modo.domain.dto.books.BooksSaveRequestDto;
import modo.domain.dto.books.BooksUpdateRequestDto;
import modo.domain.dto.pictures.PicturesSaveRequestDto;
import modo.domain.dto.users.Users.UsersSaveRequestDto;
import modo.domain.entity.Books;
import modo.domain.entity.Pictures;
import modo.domain.entity.Users;
import modo.enums.BooksStatus;
import modo.exception.booksException.UsersMismatchException;
import modo.repository.BooksRepository;
import modo.repository.PicturesRepository;
import modo.repository.UsersRepository;
import modo.service.BooksService;
import modo.util.GeomUtil;
import org.assertj.core.data.Percentage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Point;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Log4j2
public class BooksServiceTest {

    @Autowired
    BooksRepository booksRepository;

    @Autowired
    UsersRepository usersRepository;

    @Autowired
    PicturesRepository picturesRepository;

    @Mock
    JwtTokenProvider jwtTokenProvider;

    BooksService booksService;

    @BeforeEach
    void tearDown() {
        booksRepository.deleteAllInBatch();
    }

    @BeforeEach
    void injectRepositoryToUsersService() {
        booksService = new BooksService(booksRepository, usersRepository, picturesRepository, jwtTokenProvider);
    }

    @Test
    void Service_책저장_테스트() {
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
    void Service_책업데이트_테스트() {
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

    @Test
    void Service_책삭제_테스트() {
        // given
        saveTestBooksAndPicturesList();
        when(jwtTokenProvider.getUsersId(any())).thenReturn(testUsersId);
        Long testBooksId = booksRepository.findAll().get(0).getBooksId();
        String testAccessToken = "testAccessToken";

        // when
        booksService.delete(testBooksId, testAccessToken);

        // then
        assertThat(booksRepository.findAll().size()).isZero();
    }

    @Test
    void Service_책삭제_잘못된토큰으로_테스트() {
        // given
        saveTestBooksAndPicturesList();
        when(jwtTokenProvider.getUsersId(any())).thenReturn("wrong" + testUsersId);
        Long testBooksId = booksRepository.findAll().get(0).getBooksId();
        String testAccessToken = "testAccessToken";

        // when + then : assertThrows
        assertThrows(UsersMismatchException.class, () -> booksService.delete(testBooksId, testAccessToken));
    }

    @Test
    void Service_책거리조회_테스트() {

        final UsersSaveRequestDto testUsersSaveRequestDto = UsersSaveRequestDto.builder()
                .usersId(testUsersId)
                .nickname(testNickname)
                .latitude(37.28016)
                .longitude(127.043705)
                .build();

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
        assertThat(booksRepository.findAll().get(0).calculateDistance(37.275806, 127.044909)).isCloseTo(495.723031, Percentage.withPercentage(99));
    }

    @Test
    void Service_거리책조회_특정이름포함및미포함_페이지네이션_테스트() {

        final UsersSaveRequestDto testUsersSaveRequestDto = UsersSaveRequestDto.builder()
                .usersId(testUsersId)
                .nickname(testNickname)
                .latitude(37.275806)
                .longitude(127.044909)
                .build();

        final UsersSaveRequestDto anotherTestUsersSaveRequestDto = UsersSaveRequestDto.builder()
                .usersId("another" + testUsersId)
                .nickname(testNickname)
                .latitude(37.28016)
                .longitude(127.043705)
                .build();

        usersRepository.save(testUsersSaveRequestDto.toEntity());
        usersRepository.save(anotherTestUsersSaveRequestDto.toEntity());

        for (int i = 0; i < 15; i++) {
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

            booksService.save(requestDto);
        }

        for (int i = 0; i < 15; i++) {
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
                    .name(anotherTestName)
                    .price(testPrice)
                    .status(testStatus.toString())
                    .description(testDescription)
                    .imgUrl(testImgUrl)
                    .usersId(testUsersId)
                    .picturesSaveRequestDtoList(picturesSaveRequestDtoList)
                    .build();

            booksService.save(requestDto);
        }

        when(jwtTokenProvider.getUsersId(any())).thenReturn("another" + testUsersId);

        BooksPageResponseDto result = booksService.findBooksByNameContainingWithDistanceWithPaging("스프링", 0, "dummyToken");
        assertThat(result.getCurPage()).isEqualTo(0L);
        assertThat(result.getMaxPage()).isEqualTo(2L);
        assertThat(result.getBooksList().size()).isEqualTo(10);

        result = booksService.findBooksWithDistanceWithPaging(0, "dummyToken");
        assertThat(result.getCurPage()).isEqualTo(0L);
        assertThat(result.getMaxPage()).isEqualTo(3L);
        assertThat(result.getBooksList().size()).isEqualTo(10);
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
    static final String anotherTestName = "Real MySQL 8.0";
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
