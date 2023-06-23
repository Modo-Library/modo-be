package modo.domain.dto.users.Users;


import lombok.AllArgsConstructor;
import lombok.Builder;
import modo.domain.entity.Users;

@AllArgsConstructor
public class UsersResponseDto {
    // Users
    public String usersId;
    public String nickname;
    public double reviewScore;
    public Long reviewCount;
    // UsersHistory
    public Long rentingCount;
    public Long returningCount;
    public Long buyCount;
    public Long sellCount;

    @Builder
    public UsersResponseDto(Users users) {
        this.usersId = users.getUsersId();
        this.nickname = users.getNickname();
        this.reviewScore = users.getReviewScore();
        this.reviewCount = users.getReviewCount();
        this.rentingCount = users.getUsersHistory().getRentingCount();
        this.returningCount = users.getUsersHistory().getRentingCount();
        this.buyCount = users.getUsersHistory().getBuyCount();
        this.sellCount = users.getUsersHistory().getSellCount();
    }
}
