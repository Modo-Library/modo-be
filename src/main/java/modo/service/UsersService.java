package modo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
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
}
