package modo.repository;

import modo.domain.entity.ChatRooms;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRoomsRepository extends JpaRepository<ChatRooms, Long> {
    @Query("SELECT cr FROM ChatRooms cr " +
            "LEFT JOIN FETCH cr.chatMessagesList cm " +
            "WHERE cr.chatRoomsId = :chatRoomsId")
    Optional<ChatRooms> findChatRoomsByIdFetchChatMessagesList(@Param("chatRoomsId") Long chatRoomsId);

    @Query("SELECT DISTINCT u.usersId FROM ChatRooms c JOIN c.usersList u WHERE c.chatRoomsId = :chatRoomsId")
    List<String> findUsersIdListByChatRoomsId(@Param("chatRoomsId") Long chatRoomsId);

    @Query("SELECT cr FROM ChatRooms cr " +
            "JOIN cr.usersList cu " +
            "WHERE cu.usersId = :usersId " +
            "ORDER BY cr.timeStamp DESC")
    List<ChatRooms> findChatRoomsWhereUsersBelongsTo(@Param("usersId") String usersId);

    @Query("SELECT cr FROM ChatRooms cr LEFT JOIN FETCH cr.usersList cu")
    List<ChatRooms> findAllChatRoomsFetchUsers();

}
