package modo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import modo.auth.JwtTokenProvider;
import modo.domain.dto.users.Users.UsersLoginResponseDto;
import modo.domain.dto.users.Users.UsersResponseDto;
import modo.domain.dto.users.Users.UsersSaveRequestDto;
import modo.domain.dto.users.UsersHistory.UsersHistoryAddRequestDto;
import modo.domain.dto.users.UsersReview.UsersReviewResponseDto;
import modo.domain.dto.users.UsersReview.UsersReviewSaveRequestDto;
import modo.domain.entity.Users;
import modo.domain.entity.UsersHistory;
import modo.domain.entity.UsersReview;
import modo.repository.UsersHistoryRepository;
import modo.repository.UsersRepository;
import modo.repository.UsersReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@RequiredArgsConstructor
@Service
public class UsersService {

    private final UsersRepository usersRepository;
    private final UsersHistoryRepository usersHistoryRepository;
    private final UsersReviewRepository usersReviewRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public UsersResponseDto save(UsersSaveRequestDto usersSaveRequestDto) {
        // Create users and usersHistory
        Users users = usersSaveRequestDto.toEntity();
        UsersHistory usersHistory = new UsersHistory(users);

        // Join users with usersHistory
        users.setUsersHistory(usersHistory);

        // Save users and usersHistory
        usersRepository.save(users);
        usersHistoryRepository.save(usersHistory);

        // Return usersId
        return findUsers(users.getUsersId());
    }

    @Transactional
    public UsersResponseDto save(UsersSaveRequestDto usersSaveRequestDto, String sub) {
        // Create users and usersHistory
        Users users = usersSaveRequestDto.toEntity();
        UsersHistory usersHistory = new UsersHistory(users);

        // Join users with usersHistory
        users.setUsersHistory(usersHistory);

        // Add sub value at users
        users.setSub(sub);

        // Save users and usersHistory
        usersRepository.save(users);
        usersHistoryRepository.save(usersHistory);

        // Return usersId
        return findUsers(users.getUsersId());
    }

    @Transactional
    public UsersLoginResponseDto login(String usersId) {
        return UsersLoginResponseDto.builder()
                .accessToken(jwtTokenProvider.createAccessToken(usersId))
                .refreshToken(jwtTokenProvider.createRefreshToken(usersId))
                .usersId(usersId)
                .build();
    }

    @Transactional(readOnly = true)
    public UsersResponseDto findUsers(String usersId) {
        Users users = findUsersInRepository(usersId);
        return new UsersResponseDto(users);
    }

    @Transactional(readOnly = true)
    public UsersReviewResponseDto findUsersFetchReview(String usersId) {
        Users users = findUsersFetchReviewInRepository(usersId);
        return new UsersReviewResponseDto(users);
    }

    @Transactional
    public UsersReviewResponseDto addReview(UsersReviewSaveRequestDto requestDto) {
        // Find target Users first
        String usersId = requestDto.getUsersId();
        Users users = findUsersFetchReviewInRepository(usersId);

        // Make UsersReview entity through DTO's toEntity method
        UsersReview usersReview = requestDto.toEntity(users);

        // Add UsersReview entity to Users
        users.addReview(usersReview);

        // Save new UsersReview Entity
        usersReviewRepository.save(usersReview);

        // Return UsersReviewResponseDto
        return new UsersReviewResponseDto(users);
    }

    @Transactional
    public UsersReviewResponseDto removeReview(Long usersReviewId) {
        // Find target Users first
        UsersReview usersReview = findUsersReviewInRepository(usersReviewId);
        Users users = usersReview.getUsers();

        // Remove UsersReview entity to Users
        // Due to the cascade and orphan-removal, usersReview will be removed automatically
        users.removeReview(usersReview);

        // Return UsersReviewResponseDto
        return new UsersReviewResponseDto(users);
    }

    @Transactional
    public UsersResponseDto addUsersHistory(UsersHistoryAddRequestDto requestDto) {
        // Find target UsersHistory first
        String usersId = requestDto.getUsersId();
        UsersHistory usersHistory = findUsersHistoryInRepository(usersId);

        // Add history
        usersHistory.addHistory(requestDto.getType());

        // Find Users and return UsersResponseDto
        return findUsers(usersId);
    }

    @Transactional
    public UsersResponseDto changeNickname(String usersId, String nickname) {
        Users users = findUsersInRepository(usersId);
        users.setNickname(nickname);
        return findUsers(usersId);
    }

    @Transactional(readOnly = true)
    public String findUsersIdBySub(String sub) {
        Users users = usersRepository.findUsersBySub(sub).orElseThrow(
                () -> new IllegalArgumentException("Users with sub : " + sub + " is not exist!")
        );
        return users.getUsersId();
    }

    @Transactional
    public UsersResponseDto changeLocation(String usersId, double latitude, double longitude) {
        Users users = findUsersInRepository(usersId);
        users.updateLocation(latitude, longitude);
        return findUsers(usersId);
    }

    @Transactional
    public void logout(String accessToken) {
        String usersId = jwtTokenProvider.getUsersId(accessToken);
        jwtTokenProvider.expireAllToken(usersId);
    }

    private Users findUsersInRepository(String usersId) {
        return usersRepository.findById(usersId).orElseThrow(
                () -> new IllegalArgumentException("Users with id : " + usersId + " is not exist")
        );
    }

    private Users findUsersFetchReviewInRepository(String usersId) {
        return usersRepository.findUsersByIdFetchUsersReviewList(usersId).orElseThrow(
                () -> new IllegalArgumentException("Users with id : " + usersId + " is not exist")
        );
    }

    private UsersHistory findUsersHistoryInRepository(String usersId) {
        return usersHistoryRepository.findById(usersId).orElseThrow(
                () -> new IllegalArgumentException("UsersHistory with id : " + usersId + " is not exist")
        );
    }

    private UsersReview findUsersReviewInRepository(Long usersReviewId) {
        return usersReviewRepository.findById(usersReviewId).orElseThrow(
                () -> new IllegalArgumentException("UsersReview with id : " + usersReviewId + " is not exist")
        );
    }

    public boolean isExistsByUsersId(String usersId) {
        return usersRepository.existsByUsersId(usersId);
    }

}
