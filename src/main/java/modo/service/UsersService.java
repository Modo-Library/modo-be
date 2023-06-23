package modo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import modo.domain.dto.users.Users.UsersResponseDto;
import modo.domain.dto.users.Users.UsersSaveRequestDto;
import modo.domain.dto.users.UsersHistory.UsersHistoryAddRequestDto;
import modo.domain.dto.users.UsersReview.UsersReviewResponseDto;
import modo.domain.dto.users.UsersReview.UsersReviewSaveRequestDto;
import modo.repository.UsersHistoryRepository;
import modo.repository.UsersRepository;
import modo.repository.UsersReviewRepository;
import org.springframework.stereotype.Service;

@Log4j2
@RequiredArgsConstructor
@Service
public class UsersService {

    private final UsersRepository usersRepository;
    private final UsersHistoryRepository usersHistoryRepository;
    private final UsersReviewRepository usersReviewRepository;

    public String save(UsersSaveRequestDto usersSaveRequestDto) {
        // TODO: 2023/06/23
    }

    public UsersResponseDto findUsers(String usersId) {
        // TODO: 2023/06/23
    }

    public UsersReviewResponseDto findUsersReview(String usersId) {
        // TODO: 2023/06/23
    }

    public UsersReviewResponseDto addReview(UsersReviewSaveRequestDto requestDto) {
        // TODO: 2023/06/23
    }

    public UsersReviewResponseDto removeReview(Long usersReviewId) {
        // TODO: 2023/06/23
    }

    public UsersResponseDto addUsersHistory(UsersHistoryAddRequestDto requestDto) {
        // TODO: 2023/06/23
    }
}
