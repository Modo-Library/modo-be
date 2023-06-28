package modo.domain.dto.users.UsersReview;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import modo.domain.entity.Users;
import modo.domain.entity.UsersReview;

@AllArgsConstructor
@Builder
@Getter
public class UsersReviewSaveRequestDto {
    private String usersId;
    private Long score;
    private String description;

    public UsersReview toEntity(Users users) {
        return UsersReview.builder()
                .reviewedUsers(this.usersId)
                .score(this.score)
                .description(this.description)
                .users(users)
                .build();
    }
}
