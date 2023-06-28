package modo.domain.dto.users.UsersHistory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import modo.enums.UsersHistoryAddRequestType;

@AllArgsConstructor
@Builder
@Getter
public class UsersHistoryAddRequestDto {
    private String usersId;
    private UsersHistoryAddRequestType type;
}
