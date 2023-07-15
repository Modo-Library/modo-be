package modo.repository;

import modo.domain.entity.UsersBooksHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersBooksHistoryRepository extends JpaRepository<UsersBooksHistory, Long> {
}
