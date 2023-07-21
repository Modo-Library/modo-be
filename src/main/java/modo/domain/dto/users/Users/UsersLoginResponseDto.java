package modo.domain.dto.users.Users;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class UsersLoginResponseDto {
    private String accessToken;
    private String refreshToken;
    private String usersId;
}
