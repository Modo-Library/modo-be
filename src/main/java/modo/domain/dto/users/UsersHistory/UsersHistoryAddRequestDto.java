package modo.domain.dto.users.UsersHistory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import modo.enums.UsersHistoryAddRequestType;

@AllArgsConstructor
@Builder
public class UsersHistoryAddRequestDto {
    private String usersId;
    private UsersHistoryAddRequestType type;
}
