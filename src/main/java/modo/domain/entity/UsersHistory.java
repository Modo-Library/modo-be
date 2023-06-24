package modo.domain.entity;

import jakarta.persistence.*;
import lombok.*;

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
}
