package modo.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Pictures {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long picturesId;

    @Column(nullable = false)
    private String filename;

    @Column(nullable = false)
    private String imgUrl;

    @ManyToOne
    @JoinColumn(name = "booksId")
    private Books books;

    @Builder
    public Pictures(String filename, String imgUrl) {
        this.filename = filename;
        this.imgUrl = imgUrl;
    }

}
