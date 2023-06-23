package modo.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Entity
public class UsersHistory {
    @Id
    private String usersId;

    @Column(nullable = false)
    private long rentingCount;

    @Column(nullable = false)
    private long returningCount;

    @Column(nullable = true)
    private long buyCount;

    @Column(nullable = false)
    private long sellCount;
}
