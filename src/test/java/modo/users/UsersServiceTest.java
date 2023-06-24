package modo.users;

import modo.domain.dto.users.Users.UsersResponseDto;
import modo.domain.dto.users.Users.UsersSaveRequestDto;
import modo.domain.dto.users.UsersHistory.UsersHistoryAddRequestDto;
import modo.domain.dto.users.UsersReview.UsersReviewResponseDto;
import modo.domain.dto.users.UsersReview.UsersReviewSaveRequestDto;
import modo.domain.entity.Users;
import modo.domain.entity.UsersHistory;
import modo.domain.entity.UsersReview;
import modo.enums.UsersHistoryAddRequestType;
import modo.repository.UsersHistoryRepository;
import modo.repository.UsersRepository;
import modo.repository.UsersReviewRepository;
import modo.service.UsersService;
import modo.util.GeomUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@AutoConfigureTestDatabase
public class UsersServiceTest {

    @Autowired
    UsersRepository usersRepository;

    @Autowired
    UsersReviewRepository usersReviewRepository;

    @Autowired
    UsersHistoryRepository usersHistoryRepository;

    UsersService usersService = new UsersService(usersRepository, usersHistoryRepository, usersReviewRepository);

    @BeforeEach
    void tearDown() {
        usersRepository.deleteAllInBatch();
    }

    @Test
    void Service_회원가입_테스트() {
        // When
        // Test Method : UsersService.save
        String resultUsersId = usersService.save(testUsersSaveRequestDto);

        // Then
        // Test Return Value
        assertThat(resultUsersId).isEqualTo(testUsersId);

        // Check Saved Users
        Users resultUsers = usersRepository.findById(resultUsersId).orElseThrow(() -> new IllegalArgumentException(""));
        assertThat(resultUsers.getUsersId()).isEqualTo(testUsersId);
        assertThat(resultUsers.getReviewScore()).isEqualTo(testReviewScore);
        assertThat(resultUsers.getNickname()).isEqualTo(testNickname);
        assertThat(resultUsers.getReviewCount()).isEqualTo(testReviewCount);
        assertThat(resultUsers.getReviewScore()).isEqualTo(testReviewCount);
        assertThat(resultUsers.getLocation().getX()).isEqualTo(testX);
        assertThat(resultUsers.getLocation().getY()).isEqualTo(testY);
        assertThat(resultUsers.getUsersReviewList().size()).isEqualTo(0);
        assertThat(resultUsers.getUsersHistory().getUsersId()).isEqualTo(testUsersId);

        // Check Saved UsersHistory
        UsersHistory resultUsersHistory = usersHistoryRepository.findById(resultUsersId).orElseThrow(() -> new IllegalArgumentException(""));
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

        // When
        // Test Method : usersService.findUsersReview
        UsersReviewResponseDto resultDto = usersService.findUsersReview(testUsersId);

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
    void Service_리뷰제거_테스트() {
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
    }

    static final String testUsersId = "testUsersId";
    static final String testPassword = "testPassword";
    static final String testNickname = "testNickname";
    static final Point testLocation = GeomUtil.createPoint(1.1, 2.2);
    static final double testX = 1.1;
    static final double testY = 2.2;
    static final double testReviewScore = 0.0;
    static final Long testReviewCount = 0L;
    static final String testDescription = "testDescription";

    static final UsersSaveRequestDto testUsersSaveRequestDto = UsersSaveRequestDto.builder()
            .usersId(testUsersId)
            .nickname(testNickname)
            .password(testPassword)
            .latitude(testX)
            .longitude(testY)
            .build();
    Users testUsers = testUsersSaveRequestDto.toEntity();
    UsersHistory testUsersHistory = UsersHistory.builder()
            .usersId(testUsersId)
            .buyCount(0L)
            .sellCount(0L)
            .rentingCount(0L)
            .returningCount(0L)
            .build();

}
