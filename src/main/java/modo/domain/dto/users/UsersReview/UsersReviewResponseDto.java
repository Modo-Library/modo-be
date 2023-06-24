package modo.domain.dto.users.UsersReview;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import modo.domain.entity.Users;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Setter
@Getter
public class UsersReviewResponseDto {
    private String usersId;
    private String nickname;
    private double reviewScore;
    private Long reviewCount;
    private List<EachReviewResponseDto> reviewResponseDtoList;

    public UsersReviewResponseDto(Users users) {
        this.usersId = users.getUsersId();
        this.nickname = users.getNickname();
        this.reviewScore = users.getReviewScore();
        this.reviewCount = users.getReviewCount();
        this.reviewResponseDtoList = generateReviewResponseDtoList(users);
    }

    private List<EachReviewResponseDto> generateReviewResponseDtoList(Users users) {
        return users.getUsersReviewList().stream()
                .map(EachReviewResponseDto::new)
                .collect(Collectors.toList());
    }
}
