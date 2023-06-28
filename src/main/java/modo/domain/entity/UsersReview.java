package modo.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ValueGenerationType;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
public class UsersReview {
    @Id
    @Column(name = "usersReviewId")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private String reviewedUsers;

    @Column(nullable = false)
    private Long score;

    @Column
    private String description;

    @ManyToOne
    @JoinColumn(name = "usersId")
    private Users users;
}
