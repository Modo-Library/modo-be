package modo.domain.dto.users.UsersReview;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Setter;
import modo.domain.entity.UsersReview;

@Setter
@AllArgsConstructor
public class EachReviewResponseDto {
    private String usersId;
    private Long score;
    private String description;

    @Builder
    public EachReviewResponseDto(UsersReview usersReview) {
        this.usersId = usersReview.getReviewedUsers();
        this.score = usersReview.getScore();
        this.description = usersReview.getDescription();
    }
}
