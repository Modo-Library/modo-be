package modo.domain.entity;

import io.lettuce.core.dynamic.annotation.Key;
import jakarta.persistence.*;
import lombok.*;
import modo.enums.BooksStatus;
import modo.service.BooksService;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class UsersBooksHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long historyId;

    private BooksStatus status;

    @ManyToOne
    @JoinColumn(name = "usersId")
    private Users users;

    @ManyToOne
    @JoinColumn(name = "booksId")
    private Books books;

    @Builder
    public UsersBooksHistory(BooksStatus status, Users users, Books books) {
        this.status = status;
        this.users = users;
        this.books = books;
    }
}
