package modo.domain.dto.users.Users;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import modo.domain.entity.Users;
import modo.util.GeomUtil;

import java.util.ArrayList;

@AllArgsConstructor
@Builder
@Getter
public class UsersSaveRequestDto {
    private String usersId;
    private String password;
    private String nickname;
    private double latitude;
    private double longitude;

    public Users toEntity() {
        return Users.builder()
                .usersId(usersId)
                .password(password)
                .nickname(nickname)
                .location(GeomUtil.createPoint(latitude, longitude))
                .reviewScore(0.0)
                .reviewCount(0L)
                .usersReviewList(new ArrayList<>())
                .build();
    }
}
