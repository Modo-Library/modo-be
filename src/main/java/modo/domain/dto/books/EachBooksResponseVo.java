package modo.domain.dto.books;

public interface EachBooksResponseVo {
    Long getBooksId();

    String getName();

    Long getPrice();

    String getStatus();

    String getDescription();

    String getImgUrl();

    int getDistance();
}
