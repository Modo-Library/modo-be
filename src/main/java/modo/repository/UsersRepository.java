package modo.repository;

import modo.domain.entity.Users;
import org.locationtech.jts.geom.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users, String> {

    public boolean existsByNickname(String nickname);

    public boolean existsByUsersId(String usersId);

    public Optional<Users> findUsersBySub(String sub);

    @Override
    @Query("select u from Users u left join fetch u.usersHistory uh where u.usersId = :usersId")
    public Optional<Users> findById(@Param("usersId") String usersId);

    @Query("select u from Users u left join fetch u.usersHistory uh left join fetch u.usersReviewList ur where u.usersId = :usersId")
    public Optional<Users> findUsersByIdFetchUsersReviewList(@Param("usersId") String usersId);

    @Query("select u from Users u left join fetch u.usersHistory uh left join fetch u.chatRoomsList cr where u.usersId = :usersId")
    public Optional<Users> findUsersByIdFetchChatRoomsList(@Param("usersId") String usersId);

    @Query("select u.location from Users u where u.usersId = :usersId")
    public Point findPointById(@Param("usersId") String usersId);

    @Query("select u from Users u left join fetch u.chatRoomsList")
    public List<Users> findAllUsersFetchChatRooms();
}
