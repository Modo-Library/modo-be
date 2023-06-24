package modo.domain.dto.users.Users;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import modo.domain.entity.Users;

@Getter
@AllArgsConstructor
public class UsersResponseDto {
    // Users
    private String usersId;
    private String nickname;
    private double reviewScore;
    private Long reviewCount;
    // UsersHistory
    private Long rentingCount;
    private Long returningCount;
    private Long buyCount;
    private Long sellCount;

    @Builder
    public UsersResponseDto(Users users) {
        this.usersId = users.getUsersId();
        this.nickname = users.getNickname();
        this.reviewScore = users.getReviewScore();
        this.reviewCount = users.getReviewCount();
        this.rentingCount = users.getUsersHistory().getRentingCount();
        this.returningCount = users.getUsersHistory().getReturningCount();
        this.buyCount = users.getUsersHistory().getBuyCount();
        this.sellCount = users.getUsersHistory().getSellCount();
    }
}
