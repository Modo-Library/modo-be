package modo.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@RedisHash("access")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccessToken {
    @Id
    private String usersId;
    private String tokenValue;

    @TimeToLive
    private Long expiredTime;
}