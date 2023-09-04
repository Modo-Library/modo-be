package modo.repository;

import modo.domain.entity.ChatRooms;
import modo.domain.entity.ChatRoomsUsers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRoomsUsersRepository extends JpaRepository<ChatRoomsUsers, Long> {
    @Query("SELECT cru.chatRoomsId FROM ChatRoomsUsers cru " +
            "WHERE cru.senderId = :senderId " +
            "AND cru.receiverId = :receiverId " +
            "AND cru.booksId = :booksId")
    Optional<Long> findChatRoomsUsersIdBySenderIdAndReceiverIdAAndBooksId(@Param("senderId") String senderId, @Param("receiverId") String receiverId, @Param("booksId") Long booksId);

    @Query("SELECT cru.senderId, cru.receiverId FROM ChatRoomsUsers cru WHERE cru.chatRoomsId = :chatRoomsId")
    String findUsersIdListByChatRoomsId(@Param("chatRoomsId") Long chatRoomsId);

    @Query("SELECT cr FROM ChatRooms cr WHERE cr.chatRoomsId IN " +
            "(SELECT cru.chatRoomsId FROM ChatRoomsUsers cru WHERE cru.senderId = :usersId OR cru.receiverId = :usersId)")
    List<ChatRooms> findChatRoomsByUsersId1(@Param("usersId") String usersId);

    @Query("SELECT DISTINCT cr " +
            "FROM ChatRooms cr " +
            "INNER JOIN ChatRoomsUsers cru ON cr.chatRoomsId = cru.chatRoomsId " +
            "WHERE cru.senderId = :usersId OR cru.receiverId = :usersId")
    List<ChatRooms> findChatRoomsByUsersId(@Param("usersId") String usersId);
}
