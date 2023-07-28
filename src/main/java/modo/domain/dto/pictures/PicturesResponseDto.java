package modo.domain.dto.pictures;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import modo.domain.entity.Pictures;

@Getter
@AllArgsConstructor
@Builder
public class PicturesResponseDto {
    private Long picturesId;
    private String filename;
    private String imgUrl;

    @Builder
    public PicturesResponseDto(Pictures pictures) {
        this.picturesId = pictures.getPicturesId();
        this.filename = pictures.getFilename();
        this.imgUrl = pictures.getImgUrl();
    }
}
