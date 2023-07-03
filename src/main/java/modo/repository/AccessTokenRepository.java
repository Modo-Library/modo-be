package modo.repository;

import modo.domain.entity.AccessToken;
import org.springframework.data.repository.CrudRepository;

public interface AccessTokenRepository extends CrudRepository<AccessToken, String> {
}
