package modo.domain.dto.books;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import modo.domain.dto.pictures.PicturesSaveRequestDto;
import modo.domain.entity.Books;
import modo.enums.BooksStatus;

import java.util.List;

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
    private List<PicturesSaveRequestDto> picturesSaveRequestDtoList;

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
