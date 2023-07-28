package modo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import modo.domain.dto.pictures.PicturesResponseDto;
import modo.domain.dto.pictures.PicturesSaveRequestDto;
import modo.repository.PicturesRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@RequiredArgsConstructor
@Service
public class PicturesService {
    private final PicturesRepository picturesRepository;

    @Transactional
    public PicturesResponseDto save(PicturesSaveRequestDto requestDto) {
        return new PicturesResponseDto(picturesRepository.save(requestDto.toEntity()));
    }
}
