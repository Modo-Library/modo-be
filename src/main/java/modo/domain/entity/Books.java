package modo.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Books {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @Builder
    public Books(String name, Long price, Long status, LocalDateTime deadline, String description, String imgUrl) {
        this.name = name;
        this.price = price;
        this.status = status;
        this.deadline = deadline;
        this.description = description;
        this.imgUrl = imgUrl;
        this.createdAt = LocalDateTime.now();
        this.modifiedAt = LocalDateTime.now();
    }

    @ManyToOne
    @JoinColumn(name = "usersId")
    private Users owner;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "books", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UsersBooksHistory> usersBooksHistoryList = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "books", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Likes> likesList = new ArrayList<>();

}