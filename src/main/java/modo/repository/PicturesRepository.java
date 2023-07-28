package modo.repository;

import modo.domain.entity.Pictures;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PicturesRepository extends JpaRepository<Pictures, Long> {
}
