package modo.repository;

import modo.domain.entity.ChatRoomsUsers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ChatRoomsUsersRepository extends JpaRepository<ChatRoomsUsers, Long> {
    @Query("SELECT cru.chatRoomsId FROM ChatRoomsUsers cru " +
            "WHERE cru.senderId = :senderId " +
            "AND cru.receiverId = :receiverId " +
            "AND cru.booksId = :booksId")
    Optional<Long> findChatRoomsUsersIdBySenderIdAndReceiverIdAAndBooksId(String senderId, String receiverId, Long booksId);
}
