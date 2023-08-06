package modo.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import modo.domain.dto.books.BooksUpdateRequestDto;
import modo.enums.BooksStatus;
import modo.util.GeomUtil;
import org.locationtech.jts.geom.Point;

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
    private BooksStatus status;

    @Column(nullable = true)
    private LocalDateTime deadline;

    @Column(nullable = true)
    private String description;

    @Column(nullable = true)
    private String imgUrl;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime modifiedAt;

    @Column
    private Point location;

    @Builder
    public Books(String name, Long price, BooksStatus status, String description, String imgUrl) {
        this.name = name;
        this.price = price;
        this.status = status;
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

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "books", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Likes> likesList = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "books", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Pictures> picturesList = new ArrayList<>();

    public void update(BooksUpdateRequestDto requestDto) {
        setName(requestDto.getName());
        setPrice(requestDto.getPrice());
        setStatus(BooksStatus.valueOf(requestDto.getStatus()));
        setDescription(requestDto.getDescription());
        setImgUrl(requestDto.getImgUrl());
        setModifiedAt(LocalDateTime.now());
    }

    public double calculateDistance(double lat2, double lon2) {
        return GeomUtil.calculateDistance(this.getLocation(), lat2, lon2);
    }

    public boolean isNotOwnerId(String usersId) {
        return !owner.getUsersId().equals(usersId);
    }

}
