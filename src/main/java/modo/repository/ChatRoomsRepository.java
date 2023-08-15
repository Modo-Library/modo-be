package modo.repository;

import modo.domain.entity.ChatRooms;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomsRepository extends JpaRepository<ChatRooms, Long> {
}
