package modo.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.Point;

import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Table(indexes = @Index(name = "idx_nickname", columnList = "nickname"))
public class Users {
    @Id
    @Column
    private String usersId;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Column
    private Point location;

    @Column(nullable = false)
    private double reviewScore;

    @Column(nullable = false)
    private Long reviewCount;

    @OneToOne(fetch = FetchType.EAGER, mappedBy = "users", cascade = CascadeType.ALL, orphanRemoval = true)
    private UsersHistory usersHistory;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "users", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UsersReview> usersReviewList = new ArrayList<>();
}
