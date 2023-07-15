package modo.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class Books {
    @Id
    private Long booksId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Long price;

    @Column(nullable = false)
    private Long status;

    @Column(nullable = false)
    private LocalDateTime deadline;

    @Column(nullable = true)
    private String description;

    @Column(nullable = true)
    private String imgUrl;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime modifiedAt;

    @ManyToOne
    @JoinColumn(name = "usersId")
    private Users owner;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "books", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UsersBooksHistory> usersBooksHistoryList = new ArrayList<>();

}
