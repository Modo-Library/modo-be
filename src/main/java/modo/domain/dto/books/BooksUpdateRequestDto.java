package modo.domain.dto.books;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class BooksUpdateRequestDto {
    private Long booksId;
    private String name;
    private Long price;
    private String status;
    private String description;
    private String imgUrl;
}
