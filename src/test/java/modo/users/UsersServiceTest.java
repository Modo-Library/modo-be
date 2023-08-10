package modo.users;

import lombok.extern.log4j.Log4j2;
import modo.auth.JwtTokenProvider;
import modo.domain.dto.users.Users.UsersResponseDto;
import modo.domain.dto.users.Users.UsersSaveRequestDto;
import modo.domain.dto.users.UsersHistory.UsersHistoryAddRequestDto;
import modo.domain.dto.users.UsersReview.UsersReviewResponseDto;
import modo.domain.dto.users.UsersReview.UsersReviewSaveRequestDto;
import modo.domain.entity.Users;
import modo.domain.entity.UsersHistory;
import modo.domain.entity.UsersReview;
import modo.enums.UsersHistoryAddRequestType;
import modo.repository.*;
import modo.service.UsersService;
import modo.util.GeomUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Point;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Log4j2
public class UsersServiceTest {

    @Autowired
    UsersRepository usersRepository;

    @Autowired
    UsersReviewRepository usersReviewRepository;

    @Autowired
    UsersHistoryRepository usersHistoryRepository;

    @Autowired
    BooksRepository booksRepository;

    @Autowired
    PicturesRepository picturesRepository;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    UsersService usersService;

    @BeforeEach
    void tearDown() {
        picturesRepository.deleteAllInBatch();
        booksRepository.deleteAllInBatch();
        usersHistoryRepository.deleteAllInBatch();
        usersRepository.deleteAllInBatch();
    }

    @BeforeEach
    void injectRepositoryToUsersService() {
        usersService = new UsersService(usersRepository, usersHistoryRepository, usersReviewRepository, jwtTokenProvider);
    }

    @Test
    void Service_회원가입_테스트() {
        // When
        // Test Method : UsersService.save
        UsersResponseDto resultResponseDto = usersService.save(testUsersSaveRequestDto);

        // Then
        // Test Return Value
        assertThat(resultResponseDto.getUsersId()).isEqualTo(testUsersId);
        assertThat(resultResponseDto.getNickname()).isEqualTo(testNickname);
        assertThat(resultResponseDto.getBuyCount()).isEqualTo(0L);
        assertThat(resultResponseDto.getSellCount()).isEqualTo(0L);
        assertThat(resultResponseDto.getReviewCount()).isEqualTo(0L);
        assertThat(resultResponseDto.getRentingCount()).isEqualTo(0L);
        assertThat(resultResponseDto.getReviewScore()).isEqualTo(0.0);

        // Check Saved Users
        Users resultUsers = usersRepository.findById(testUsersId).orElseThrow(() -> new IllegalArgumentException(""));
        assertThat(resultUsers.getUsersId()).isEqualTo(testUsersId);
        assertThat(resultUsers.getReviewScore()).isEqualTo(testReviewScore);
        assertThat(resultUsers.getNickname()).isEqualTo(testNickname);
        assertThat(resultUsers.getReviewCount()).isEqualTo(testReviewCount);
        assertThat(resultUsers.getReviewScore()).isEqualTo(testReviewScore);
        assertThat(resultUsers.getLocation().getX()).isEqualTo(testY);
        assertThat(resultUsers.getLocation().getY()).isEqualTo(testX);
        assertThat(resultUsers.getUsersReviewList().size()).isEqualTo(0);
        assertThat(resultUsers.getUsersHistory().getUsersId()).isEqualTo(testUsersId);

        // Check Saved UsersHistory
        UsersHistory resultUsersHistory = usersHistoryRepository.findById(testUsersId).orElseThrow(() -> new IllegalArgumentException(""));
        assertThat(resultUsersHistory.getUsersId()).isEqualTo(testUsersId);
        assertThat(resultUsersHistory.getRentingCount()).isEqualTo(0L);
        assertThat(resultUsersHistory.getReturningCount()).isEqualTo(0L);
        assertThat(resultUsersHistory.getSellCount()).isEqualTo(0L);
        assertThat(resultUsersHistory.getBuyCount()).isEqualTo(0L);
        assertThat(resultUsersHistory.getUsers().getUsersId()).isEqualTo(testUsersId);
    }

    @Test
    void Service_회원조회_테스트() {
        // Given
        // Save testUsers and testUsersHistory through Repository.save
        Users testUsers = testUsersSaveRequestDto.toEntity();
        UsersHistory testUsersHistory = UsersHistory.builder()
                .usersId(testUsersId)
                .buyCount(0L)
                .sellCount(0L)
                .rentingCount(0L)
                .returningCount(0L)
                .build();
        testUsers.setUsersHistory(testUsersHistory);
        testUsersHistory.setUsers(testUsers);
        usersRepository.save(testUsers);
        usersHistoryRepository.save(testUsersHistory);

        // When
        // Test Method : usersService.findUsers
        UsersResponseDto resultDto = usersService.findUsers(testUsersId);

        // Then
        // Check Returned UsersResponseDto
        assertThat(resultDto.getUsersId()).isEqualTo(testUsersId);
        assertThat(resultDto.getNickname()).isEqualTo(testNickname);
        assertThat(resultDto.getBuyCount()).isEqualTo(0L);
        assertThat(resultDto.getSellCount()).isEqualTo(0L);
        assertThat(resultDto.getReviewCount()).isEqualTo(0L);
        assertThat(resultDto.getRentingCount()).isEqualTo(0L);
        assertThat(resultDto.getReviewScore()).isEqualTo(0.0);
    }

    @Test
    void Service_회원조회_리뷰와함께_테스트() {
        // Given
        // Set testUsers and testUsersHistory through Repository.save
        Users testUsers = testUsersSaveRequestDto.toEntity();
        testUsers.setReviewCount(2L);
        testUsers.setReviewScore(4.5);
        UsersHistory testUsersHistory = UsersHistory.builder()
                .users(testUsers)
                .usersId(testUsersId)
                .buyCount(0L)
                .sellCount(0L)
                .rentingCount(0L)
                .returningCount(0L)
                .build();
        testUsers.setUsersHistory(testUsersHistory);

        // Save testReview1 and testReview2 through Repository.save
        UsersReview testReview1 = UsersReview.builder()
                .users(testUsers)
                .reviewedUsers(testUsersId)
                .description(testDescription + "1")
                .score(5L)
                .build();
        UsersReview testReview2 = UsersReview.builder()
                .users(testUsers)
                .reviewedUsers(testUsersId)
                .description(testDescription + "2")
                .score(4L)
                .build();
        testUsers.getUsersReviewList().add(testReview1);
        testUsers.getUsersReviewList().add(testReview2);
        usersReviewRepository.save(testReview1);
        usersReviewRepository.save(testReview2);

        // Save testUsers and testUsersHistory through Repository.save
        usersRepository.save(testUsers);
        usersHistoryRepository.save(testUsersHistory);

        // When
        // Test Method : usersService.findUsersReview
        UsersReviewResponseDto resultDto = usersService.findUsersFetchReview(testUsersId);

        // Then
        // Check UsersReviewResponseDto
        assertThat(resultDto.getUsersId()).isEqualTo(testUsersId);
        assertThat(resultDto.getNickname()).isEqualTo(testNickname);
        assertThat(resultDto.getReviewCount()).isEqualTo(2L);
        assertThat(resultDto.getReviewScore()).isEqualTo(4.5);

        // Check UsersReviewResponseDto.reviewResponseDtoList
        assertThat(resultDto.getReviewResponseDtoList().size()).isEqualTo(2);
        assertThat(resultDto.getReviewResponseDtoList().get(0).getUsersId()).isEqualTo(testUsersId);
        assertThat(resultDto.getReviewResponseDtoList().get(0).getScore()).isEqualTo(5L);
        assertThat(resultDto.getReviewResponseDtoList().get(0).getDescription()).isEqualTo(testDescription + "1");
        assertThat(resultDto.getReviewResponseDtoList().get(1).getUsersId()).isEqualTo(testUsersId);
        assertThat(resultDto.getReviewResponseDtoList().get(1).getScore()).isEqualTo(4L);
        assertThat(resultDto.getReviewResponseDtoList().get(1).getDescription()).isEqualTo(testDescription + "2");
    }

    @Test
    void Service_리뷰추가_테스트() {
        // Given
        // Save testUsers through Repository.save
        Users testUsers = testUsersSaveRequestDto.toEntity();
        usersRepository.save(testUsers);

        // UsersReviewSaveRequestDto : requestDto1 and requestDto2
        UsersReviewSaveRequestDto requestDto1 = UsersReviewSaveRequestDto.builder()
                .usersId(testUsersId)
                .score(5L)
                .description(testDescription + "1")
                .build();

        UsersReviewSaveRequestDto requestDto2 = UsersReviewSaveRequestDto.builder()
                .usersId(testUsersId)
                .score(1L)
                .description(testDescription + "2")
                .build();

        // When
        // Test Method : usersService.addReview
        usersService.addReview(requestDto1);
        UsersReviewResponseDto responseDto = usersService.addReview(requestDto2);

        // Then
        // Check usersReview
        assertThat(responseDto.getUsersId()).isEqualTo(testUsersId);
        assertThat(responseDto.getReviewScore()).isEqualTo(3.0);
        assertThat(responseDto.getNickname()).isEqualTo(testNickname);
        assertThat(responseDto.getReviewCount()).isEqualTo(2L);

        // Check UsersReviewResponseDto.reviewResponseDtoList
        assertThat(responseDto.getReviewResponseDtoList().size()).isEqualTo(2);
        assertThat(responseDto.getReviewResponseDtoList().get(0).getUsersId()).isEqualTo(testUsersId);
        assertThat(responseDto.getReviewResponseDtoList().get(0).getScore()).isEqualTo(5L);
        assertThat(responseDto.getReviewResponseDtoList().get(0).getDescription()).isEqualTo(testDescription + "1");
        assertThat(responseDto.getReviewResponseDtoList().get(1).getUsersId()).isEqualTo(testUsersId);
        assertThat(responseDto.getReviewResponseDtoList().get(1).getScore()).isEqualTo(1L);
        assertThat(responseDto.getReviewResponseDtoList().get(1).getDescription()).isEqualTo(testDescription + "2");
    }

    @Test
    void Service_리뷰제거_리뷰1개일때제거_테스트() {
        // Given
        // Save testUsers, testReview through Repository.save
        Users testUsers = testUsersSaveRequestDto.toEntity();
        UsersReview usersReview = UsersReview.builder()
                .reviewedUsers(testUsersId)
                .description(testDescription)
                .score(5L)
                .users(testUsers)
                .build();
        testUsers.getUsersReviewList().add(usersReview);
        testUsers.setReviewCount(testUsers.getReviewCount() + 1);
        testUsers.setReviewScore(5.0);
        usersRepository.save(testUsers);
        usersReviewRepository.save(usersReview);

        // Find targetReview's id through usersReviewRepository.findAll.get(0)
        Long targetId = usersReviewRepository.findAll().get(0).getId();

        // When
        // Test Method : usersService.removeReview
        UsersReviewResponseDto responseDto = usersService.removeReview(targetId);

        // Then
        // Check responseDto
        assertThat(responseDto.getUsersId()).isEqualTo(testUsersId);
        assertThat(responseDto.getNickname()).isEqualTo(testNickname);
        assertThat(responseDto.getReviewScore()).isEqualTo(0.0);
        assertThat(responseDto.getReviewCount()).isEqualTo(0L);
        assertThat(responseDto.getReviewResponseDtoList().size()).isEqualTo(0L);

        // Check testUsers.getUsersReviewList
        testUsers = usersRepository.findById(testUsersId).get();
        assertThat(testUsers.getUsersReviewList().size()).isEqualTo(0L);

        // Check usersReviewRepository.findById(target) throw Exception
        assertThrows(IllegalArgumentException.class, () -> usersReviewRepository.findById(targetId).orElseThrow(() -> new IllegalArgumentException("")));
    }

    @Test
    void Service_리뷰제거_리뷰2개일때제거_테스트() {
        // Given
        // Save testUsers, testReview through Repository.save
        Users testUsers = testUsersSaveRequestDto.toEntity();
        UsersReview usersReview1 = UsersReview.builder()
                .reviewedUsers(testUsersId)
                .description(testDescription)
                .score(5L)
                .users(testUsers)
                .build();
        testUsers.getUsersReviewList().add(usersReview1);
        testUsers.setReviewCount(testUsers.getReviewCount() + 1);
        testUsers.setReviewScore(5.0);
        usersReviewRepository.save(usersReview1);

        UsersReview usersReview2 = UsersReview.builder()
                .reviewedUsers(testUsersId)
                .description(testDescription)
                .score(4L)
                .users(testUsers)
                .build();
        testUsers.getUsersReviewList().add(usersReview2);
        testUsers.setReviewCount(testUsers.getReviewCount() + 1);
        testUsers.setReviewScore(4.5);
        usersReviewRepository.save(usersReview2);
        usersRepository.save(testUsers);

        // Find targetReview's id through usersReviewRepository.findAll.get(0)
        Long targetId = usersReviewRepository.findAll().get(0).getId();

        // When
        // Test Method : usersService.removeReview
        UsersReviewResponseDto responseDto = usersService.removeReview(targetId);

        // Then
        // Check responseDto
        assertThat(responseDto.getUsersId()).isEqualTo(testUsersId);
        assertThat(responseDto.getNickname()).isEqualTo(testNickname);
        assertThat(responseDto.getReviewScore()).isEqualTo(4.0);
        assertThat(responseDto.getReviewCount()).isEqualTo(1L);
        assertThat(responseDto.getReviewResponseDtoList().size()).isEqualTo(1L);

        // Check testUsers.getUsersReviewList
        testUsers = usersRepository.findById(testUsersId).get();
        assertThat(testUsers.getUsersReviewList().size()).isEqualTo(1L);

        // Check usersReviewRepository.findById(target) throw Exception
        assertThrows(IllegalArgumentException.class, () -> usersReviewRepository.findById(targetId).orElseThrow(() -> new IllegalArgumentException("")));
    }

    @Test
    void Service_유저구매기록추가_테스트() {
        // Given
        // Save testUsers and testUsersHistory through Repository.save
        Users testUsers = testUsersSaveRequestDto.toEntity();
        UsersHistory testUsersHistory = UsersHistory.builder()
                .users(testUsers)
                .usersId(testUsersId)
                .buyCount(0L)
                .sellCount(0L)
                .rentingCount(0L)
                .returningCount(0L)
                .build();
        testUsers.setUsersHistory(testUsersHistory);
        usersRepository.save(testUsers);
        usersHistoryRepository.save(testUsersHistory);

        UsersHistoryAddRequestDto requestDto = UsersHistoryAddRequestDto.builder()
                .usersId(testUsersId)
                .type(UsersHistoryAddRequestType.ADD_BUY_COUNT)
                .build();

        // When
        // Test Method : UsersService.addUsersHistory
        UsersResponseDto responseDto = usersService.addUsersHistory(requestDto);

        // Then
        // Check responseDto
        assertThat(responseDto.getUsersId()).isEqualTo(testUsersId);
        assertThat(responseDto.getNickname()).isEqualTo(testNickname);
        assertThat(responseDto.getReviewScore()).isEqualTo(0.0);
        assertThat(responseDto.getReviewCount()).isEqualTo(0L);
        assertThat(responseDto.getBuyCount()).isEqualTo(1L);
        assertThat(responseDto.getSellCount()).isEqualTo(0L);
        assertThat(responseDto.getRentingCount()).isEqualTo(0L);
        assertThat(responseDto.getReturningCount()).isEqualTo(0L);

        // Check entity's value update
        assertThat(usersHistoryRepository.findAll().get(0).getBuyCount()).isEqualTo(1L);
        assertThat(usersRepository.findAll().get(0).getUsersHistory().getBuyCount()).isEqualTo(1L);
    }

    @Test
    void Service_유저판매기록추가_테스트() {
        // Given
        // Save testUsers and testUsersHistory through Repository.save
        Users testUsers = testUsersSaveRequestDto.toEntity();
        UsersHistory testUsersHistory = UsersHistory.builder()
                .users(testUsers)
                .usersId(testUsersId)
                .buyCount(0L)
                .sellCount(0L)
                .rentingCount(0L)
                .returningCount(0L)
                .build();
        testUsers.setUsersHistory(testUsersHistory);
        usersRepository.save(testUsers);
        usersHistoryRepository.save(testUsersHistory);

        UsersHistoryAddRequestDto requestDto = UsersHistoryAddRequestDto.builder()
                .usersId(testUsersId)
                .type(UsersHistoryAddRequestType.ADD_SELL_COUNT)
                .build();

        // When
        // Test Method : UsersService.addUsersHistory
        UsersResponseDto responseDto = usersService.addUsersHistory(requestDto);

        // Then
        // Check responseDto
        assertThat(responseDto.getUsersId()).isEqualTo(testUsersId);
        assertThat(responseDto.getNickname()).isEqualTo(testNickname);
        assertThat(responseDto.getReviewScore()).isEqualTo(0.0);
        assertThat(responseDto.getReviewCount()).isEqualTo(0L);
        assertThat(responseDto.getBuyCount()).isEqualTo(0L);
        assertThat(responseDto.getSellCount()).isEqualTo(1L);
        assertThat(responseDto.getRentingCount()).isEqualTo(0L);
        assertThat(responseDto.getReturningCount()).isEqualTo(0L);

        // Check entity's value update
        assertThat(usersHistoryRepository.findAll().get(0).getSellCount()).isEqualTo(1L);
        assertThat(usersRepository.findAll().get(0).getUsersHistory().getSellCount()).isEqualTo(1L);
    }

    @Test
    void Service_유저대여기록추가_테스트() {
        // Given
        // Save testUsers and testUsersHistory through Repository.save
        Users testUsers = testUsersSaveRequestDto.toEntity();
        UsersHistory testUsersHistory = UsersHistory.builder()
                .users(testUsers)
                .usersId(testUsersId)
                .buyCount(0L)
                .sellCount(0L)
                .rentingCount(0L)
                .returningCount(0L)
                .build();
        testUsers.setUsersHistory(testUsersHistory);
        usersRepository.save(testUsers);
        usersHistoryRepository.save(testUsersHistory);

        UsersHistoryAddRequestDto requestDto = UsersHistoryAddRequestDto.builder()
                .usersId(testUsersId)
                .type(UsersHistoryAddRequestType.ADD_RENTING_COUNT)
                .build();

        // When
        // Test Method : UsersService.addUsersHistory
        UsersResponseDto responseDto = usersService.addUsersHistory(requestDto);

        // Then
        // Check responseDto
        assertThat(responseDto.getUsersId()).isEqualTo(testUsersId);
        assertThat(responseDto.getNickname()).isEqualTo(testNickname);
        assertThat(responseDto.getReviewScore()).isEqualTo(0.0);
        assertThat(responseDto.getReviewCount()).isEqualTo(0L);
        assertThat(responseDto.getBuyCount()).isEqualTo(0L);
        assertThat(responseDto.getSellCount()).isEqualTo(0L);
        assertThat(responseDto.getRentingCount()).isEqualTo(1L);
        assertThat(responseDto.getReturningCount()).isEqualTo(0L);

        // Check entity's value update
        assertThat(usersHistoryRepository.findAll().get(0).getRentingCount()).isEqualTo(1L);
        assertThat(usersRepository.findAll().get(0).getUsersHistory().getRentingCount()).isEqualTo(1L);
    }

    @Test
    void Service_유저반납기록추가_테스트() {
        // Given
        // Save testUsers and testUsersHistory through Repository.save
        Users testUsers = testUsersSaveRequestDto.toEntity();
        UsersHistory testUsersHistory = UsersHistory.builder()
                .users(testUsers)
                .usersId(testUsersId)
                .buyCount(0L)
                .sellCount(0L)
                .rentingCount(0L)
                .returningCount(0L)
                .build();
        testUsers.setUsersHistory(testUsersHistory);
        usersRepository.save(testUsers);
        usersHistoryRepository.save(testUsersHistory);

        UsersHistoryAddRequestDto requestDto = UsersHistoryAddRequestDto.builder()
                .usersId(testUsersId)
                .type(UsersHistoryAddRequestType.ADD_RETURNING_COUNT)
                .build();

        // When
        // Test Method : UsersService.addUsersHistory
        UsersResponseDto responseDto = usersService.addUsersHistory(requestDto);

        // Then
        // Check responseDto
        assertThat(responseDto.getUsersId()).isEqualTo(testUsersId);
        assertThat(responseDto.getNickname()).isEqualTo(testNickname);
        assertThat(responseDto.getReviewScore()).isEqualTo(0.0);
        assertThat(responseDto.getReviewCount()).isEqualTo(0L);
        assertThat(responseDto.getBuyCount()).isEqualTo(0L);
        assertThat(responseDto.getSellCount()).isEqualTo(0L);
        assertThat(responseDto.getRentingCount()).isEqualTo(0L);
        assertThat(responseDto.getReturningCount()).isEqualTo(1L);

        // Check entity's value update
        assertThat(usersHistoryRepository.findAll().get(0).getReturningCount()).isEqualTo(1L);
        assertThat(usersRepository.findAll().get(0).getUsersHistory().getReturningCount()).isEqualTo(1L);
    }

    @Test
    void Service_sub으로usersId찾기_테스트() {
        // Given
        // Save testUsers and testUsersHistory through Repository.save
        Users testUsers = testUsersSaveRequestDto.toEntity();
        testUsers.setSub(testSub);
        UsersHistory testUsersHistory = UsersHistory.builder()
                .users(testUsers)
                .usersId(testUsersId)
                .buyCount(0L)
                .sellCount(0L)
                .rentingCount(0L)
                .returningCount(0L)
                .build();
        testUsers.setUsersHistory(testUsersHistory);
        usersRepository.save(testUsers);
        usersHistoryRepository.save(testUsersHistory);


        // When
        // Test Method : UsersService.findUsersIdBySub
        String result = usersService.findUsersIdBySub(testSub);

        // Then
        assertThat(result).isEqualTo(testUsersId);
    }

    static final String testUsersId = "testUsersId";
    static final String testNickname = "testNickname";
    static final String testSub = "testSub";
    static final Point testLocation = GeomUtil.createPoint(1.1, 2.2);
    static final double testX = 1.1;
    static final double testY = 2.2;
    static final double testReviewScore = 0.0;
    static final Long testReviewCount = 0L;
    static final String testDescription = "testDescription";

    static final UsersSaveRequestDto testUsersSaveRequestDto = UsersSaveRequestDto.builder()
            .usersId(testUsersId)
            .nickname(testNickname)
            .latitude(testX)
            .longitude(testY)
            .build();
}
