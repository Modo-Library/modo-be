package modo.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
public class UsersReview {
    @Id
    @Column(name = "usersReviewId")
    private String usersId;

    @Column(nullable = false)
    private Long score;

    @Column
    private String description;

    @ManyToOne
    @JoinColumn(name = "usersId")
    private Users users;
}
