package modo.domain.dto.books;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import modo.domain.entity.Books;

@Getter
@AllArgsConstructor
@Builder
public class BooksResponseDto {
    private Long booksId;
    private String name;
    private Long price;
    private String status;
    private String deadline;
    private String description;
    private String imgUrl;
    private String createdAt;
    private String modifiedAt;

    @Builder
    public BooksResponseDto(Books books) {
        this.booksId = books.getBooksId();
        this.name = books.getName();
        this.price = books.getPrice();
        this.status = books.getStatus().toString();
        if (deadline != null) this.deadline = books.getDeadline().toString();
        this.description = books.getDescription();
        this.imgUrl = books.getImgUrl();
        this.createdAt = books.getCreatedAt().toString();
        this.modifiedAt = books.getModifiedAt().toString();
    }
}
