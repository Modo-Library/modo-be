package modo.domain.entity;

import io.lettuce.core.dynamic.annotation.Key;
import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UsersBooksHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long historyId;

    private Long status;

    @ManyToOne
    @JoinColumn(name = "usersId")
    private Users users;

    @ManyToOne
    @JoinColumn(name = "booksId")
    private Books books;
}
