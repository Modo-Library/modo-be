package modo.repository;

import modo.domain.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

}
