package modo.domain.dto.books;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import modo.domain.entity.Books;
import modo.enums.BooksStatus;

@AllArgsConstructor
@Builder
@Getter
public class BooksSaveRequestDto {
    private String name;
    private Long price;
    private String status;
    private String description;
    private String imgUrl;
    private String usersId;

    public Books toEntity() {
        return Books.builder()
                .name(name)
                .price(price)
                .status(BooksStatus.valueOf(status))
                .description(description)
                .imgUrl(imgUrl)
                .build();

    }
}
