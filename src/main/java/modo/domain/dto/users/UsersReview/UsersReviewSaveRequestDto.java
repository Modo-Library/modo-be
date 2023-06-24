package modo.domain.dto.users.UsersReview;

import lombok.AllArgsConstructor;
import lombok.Builder;

@AllArgsConstructor
@Builder
public class UsersReviewSaveRequestDto {
    private String usersId;
    private Long score;
    private String description;

}
