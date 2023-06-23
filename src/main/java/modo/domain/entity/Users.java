package modo.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.Point;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Table(indexes = @Index(name = "idx_nickname", columnList = "nickname"))
public class Users {
    @Id
    private String usersId;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Column
    private Point location;

    @Column(nullable = false)
    private double reviewScore;

}
