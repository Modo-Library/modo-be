package modo.domain.dto.books;

import lombok.Builder;
import lombok.Getter;
import modo.domain.dto.pictures.PicturesResponseDto;
import modo.domain.entity.Books;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class BooksDetailResponseDto {
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
    private List<PicturesResponseDto> picturesList = new ArrayList<>();

    @Builder
    public BooksDetailResponseDto(Books books) {
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
        this.picturesList = books.getPicturesList().stream()
                .map(each -> new PicturesResponseDto(each))
                .collect(Collectors.toList());
    }
}
