package modo.domain.dto.books;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import modo.domain.entity.Books;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class BooksResponseDto {
    private Long booksId;
    private String name;
    private Long price;
    private String status;
    private String deadline = "";
    private String description;
    private String imgUrl;
    private String createdAt;
    private String modifiedAt;
    private double latitude;
    private double longitude;

    @Builder
    public BooksResponseDto(Books books) {
        this.booksId = books.getBooksId();
        this.name = books.getName();
        this.price = books.getPrice();
        this.status = books.getStatus().toString();

        // If Books.deadline is after LocalDateTime.now,
        // this books is on rent, so set dto's deadline to real value
        // else, make deadline with empty string
        if (books.getDeadline().isAfter(LocalDateTime.now()))
            this.deadline = books.getDeadline().toString();

        this.description = books.getDescription();
        this.imgUrl = books.getImgUrl();
        this.createdAt = books.getCreatedAt().toString();
        this.modifiedAt = books.getModifiedAt().toString();
        this.latitude = books.getLocation().getX();
        this.longitude = books.getLocation().getY();
    }
}
