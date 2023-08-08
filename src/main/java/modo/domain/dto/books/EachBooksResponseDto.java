package modo.domain.dto.books;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class EachBooksResponseDto {
    private Long booksId;
    private String name;
    private Long price;
    private String status;
    private String description;
    private String imgUrl;
    private int distance;

    @Builder
    public EachBooksResponseDto(EachBooksResponseVo responseDto) {
        this.booksId = responseDto.getBooksId();
        this.name = responseDto.getName();
        this.price = responseDto.getPrice();
        this.status = responseDto.getStatus();
        this.description = responseDto.getDescription();
        this.imgUrl = responseDto.getImgUrl();
        this.distance = responseDto.getDistance();
    }
}
