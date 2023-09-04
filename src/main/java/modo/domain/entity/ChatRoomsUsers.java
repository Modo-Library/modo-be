package modo.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(indexes = {
        @Index(name = "idx_users", columnList = "senderId, receiverId")
})
public class ChatRoomsUsers {
    @Id
    @Column(nullable = false)
    private Long chatRoomsId;

    @Column(nullable = false)
    private Long booksId;

    @Column(nullable = false)
    private String senderId;

    @Column(nullable = false)
    private String receiverId;
}
