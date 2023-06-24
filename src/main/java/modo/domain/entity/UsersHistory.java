package modo.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import modo.enums.UsersHistoryAddRequestType;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
public class UsersHistory {
    @Id
    @Column(name = "usersHistoryId")
    private String usersId;

    @Column(nullable = false)
    private long rentingCount;

    @Column(nullable = false)
    private long returningCount;

    @Column(nullable = true)
    private long buyCount;

    @Column(nullable = false)
    private long sellCount;

    @OneToOne
    @JoinColumn(name = "usersId")
    private Users users;

    public UsersHistory(Users users) {
        this.usersId = users.getUsersId();
        this.rentingCount = 0L;
        this.returningCount = 0L;
        this.buyCount = 0L;
        this.sellCount = 0L;
        this.users = users;
    }

    public void addHistory(UsersHistoryAddRequestType type) {
        switch (type) {
            case ADD_BUY_COUNT -> this.buyCount++;
            case ADD_SELL_COUNT -> this.sellCount++;
            case ADD_RENTING_COUNT -> this.rentingCount++;
            case ADD_RETURNING_COUNT -> this.returningCount++;
        }
    }
}
