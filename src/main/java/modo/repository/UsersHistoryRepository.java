package modo.repository;

import modo.domain.entity.UsersHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersHistoryRepository extends JpaRepository<UsersHistory, String> {
}
