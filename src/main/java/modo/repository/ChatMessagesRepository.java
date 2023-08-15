package modo.repository;

import modo.domain.entity.ChatMessages;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessagesRepository extends JpaRepository<ChatMessages, Long> {
}
