package modo.domain.dto.books;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class BooksPageResponseDto {
    private int maxPage;
    private int curPage;
    private List<EachBooksPageResponseDto> booksList;
}
