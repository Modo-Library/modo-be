package modo.domain.dto.pictures;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import modo.domain.entity.Pictures;

@AllArgsConstructor
@Builder
@Getter
public class PicturesSaveRequestDto {
    private String imgUrl;
    private String filename;

    public Pictures toEntity() {
        return Pictures.builder()
                .imgUrl(imgUrl)
                .filename(filename)
                .build();
    }
}
