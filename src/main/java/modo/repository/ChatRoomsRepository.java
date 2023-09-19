package modo.repository;

import modo.domain.entity.ChatRooms;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ChatRoomsRepository extends JpaRepository<ChatRooms, Long> {
    @Query("SELECT cr FROM ChatRooms cr " +
            "LEFT JOIN FETCH cr.chatMessagesList cm " +
            "WHERE cr.chatRoomsId = :chatRoomsId")
    Optional<ChatRooms> findChatRoomsByIdFetchChatMessagesList(@Param("chatRoomsId") Long chatRoomsId);
}
