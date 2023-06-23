package modo.domain.dto.users.UsersReview;

import lombok.AllArgsConstructor;
import lombok.Builder;

@AllArgsConstructor
public class UsersReviewSaveRequestDto {
    private String usersId;
    private Long score;
    private String description;

}
