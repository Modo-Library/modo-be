package modo.domain.dto.books;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import modo.domain.entity.Books;

@Getter
@AllArgsConstructor
@Builder
public class EachBooksPageResponseDto {
    private Long booksId;
    private String name;
    private Long price;
    private String status;
    private String description;
    private String imgUrl;
    private int distance;

    @Builder
    public EachBooksPageResponseDto(Books books, double lat, double lon) {
        this.booksId = books.getBooksId();
        this.name = books.getName();
        this.price = books.getPrice();
        this.status = books.getStatus().toString();
        this.description = books.getDescription();
        this.imgUrl = books.getImgUrl();
        this.distance = (int) books.calculateDistance(lat, lon);
    }
}
