package modo.books;

import lombok.extern.log4j.Log4j2;
import modo.auth.JwtTokenProvider;
import modo.domain.dto.books.BooksDetailResponseDto;
import modo.domain.dto.books.BooksSaveRequestDto;
import modo.domain.dto.books.BooksUpdateRequestDto;
import modo.domain.dto.books.EachBooksResponseDto;
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
        picturesRepository.deleteAllInBatch();
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
        assertThat(targetBooks.getDeadline()).isBeforeOrEqualTo(LocalDateTime.now());

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
    void Service_책리스트조회_특정이름포함_페이지네이션_테스트() {

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

        List<EachBooksResponseDto> result = booksService.findBooksList("dummyToken", 0, 1L, "스프링");
        assertThat(result.size()).isEqualTo(10);

        result.stream()
                .forEach(each -> {
                    log.info("id : {}, name : {}, distance : {}", each.getBooksId(), each.getName(), each.getDistance());
                });

        Long startId = result.get(9).getBooksId();
        int startDistance = result.get(9).getDistance();

        log.info("startId : {}, startDistance : {}", startId, startDistance);

        result = booksService.findBooksList("dummyToken", startDistance, startId, "스프링");
        result.stream()
                .forEach(each -> {
                    log.info("id : {}, name : {}, distance : {}", each.getBooksId(), each.getName(), each.getDistance());
                });
        assertThat(result.size()).isEqualTo(5);
    }


    @Test
    void Service_책리스트조회_특정이름미포함_페이지네이션_테스트() {

        // 아주대학교 앞 사용자
        final UsersSaveRequestDto testUsersSaveRequestDto = UsersSaveRequestDto.builder()
                .usersId(testUsersId)
                .nickname(testNickname)
                .latitude(37.275806)
                .longitude(127.044909)
                .build();

        // 아주대학교 정문 사용자
        final UsersSaveRequestDto anotherTestUsersSaveRequestDto = UsersSaveRequestDto.builder()
                .usersId("another" + testUsersId)
                .nickname(testNickname)
                .latitude(37.28016)
                .longitude(127.043705)
                .build();

        // 인계동 사용자
        final UsersSaveRequestDto otherTestUsersSaveRequestDto = UsersSaveRequestDto.builder()
                .usersId("other" + testUsersId)
                .nickname(testNickname)
                .latitude(37.261718)
                .longitude(127.031914)
                .build();

        usersRepository.save(testUsersSaveRequestDto.toEntity());
        usersRepository.save(anotherTestUsersSaveRequestDto.toEntity());
        usersRepository.save(otherTestUsersSaveRequestDto.toEntity());

        PicturesSaveRequestDto picturesSaveRequestDto = PicturesSaveRequestDto.builder()
                .imgUrl(testImgUrl + "1")
                .filename(testFilename + "1")
                .build();

        List<PicturesSaveRequestDto> picturesSaveRequestDtoList = List.of(picturesSaveRequestDto);

        BooksSaveRequestDto requestDto1 = BooksSaveRequestDto.builder()
                .name("아주대학교 앞에서 저장한 사용자의 책")
                .price(testPrice)
                .status(testStatus.toString())
                .description(testDescription)
                .imgUrl(testImgUrl)
                .usersId(testUsersId)
                .picturesSaveRequestDtoList(picturesSaveRequestDtoList)
                .build();

        BooksSaveRequestDto requestDto2 = BooksSaveRequestDto.builder()
                .name("아주대학교 정문에서 저장한 사용자의 책")
                .price(testPrice)
                .status(testStatus.toString())
                .description(testDescription)
                .imgUrl(testImgUrl)
                .usersId("another" + testUsersId)
                .picturesSaveRequestDtoList(picturesSaveRequestDtoList)
                .build();

        booksService.save(requestDto1);
        booksService.save(requestDto2);

        when(jwtTokenProvider.getUsersId(any())).thenReturn("other" + testUsersId);

        List<EachBooksResponseDto> result = booksService.findBooksList("dummyToken", 0, 1L, "");

        assertThat(result.get(0).getName()).isEqualTo("아주대학교 앞에서 저장한 사용자의 책");
        assertThat(result.get(1).getName()).isEqualTo("아주대학교 정문에서 저장한 사용자의 책");
    }

    @Test
    void Service_책상세조회_테스트() {
        saveTestBooksAndPicturesList();
        Long booksId = booksRepository.findAll().get(0).getBooksId();

        BooksDetailResponseDto target = booksService.findBooks(booksId);

        assertThat(target.getDescription()).isEqualTo(testDescription);
        assertThat(target.getImgUrl()).isEqualTo(testImgUrl);
        assertThat(target.getDeadline()).isEqualTo("");
        assertThat(target.getPicturesList().size()).isEqualTo(2);
        assertThat(target.getPicturesList().get(0).getImgUrl()).isEqualTo(testImgUrl + "1");
    }


    void saveTestBooksAndPicturesList() {
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
    static final BooksStatus testStatus = BooksStatus.AVAILABLE_RENT;
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
