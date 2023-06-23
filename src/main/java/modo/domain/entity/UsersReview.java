package modo.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
public class UsersReview {
    @Id
    private String usersId;

    @Column(nullable = false)
    private Long score;

    @Column
    private String description;
}
