package modo.repository;

import modo.domain.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersRepository extends JpaRepository<Users, String> {

    public boolean existsByNickname(String nickname);

    public boolean existsByUsersId(String usersId);
}
