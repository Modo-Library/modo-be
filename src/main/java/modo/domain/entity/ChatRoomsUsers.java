package modo.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
public class ChatRoomsUsers {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ChatRoomsUsersId;

    @Column(nullable = false)
    private Long chatRoomsId;

    @Column(nullable = false)
    private Long booksId;

    @Column(nullable = false)
    private String senderId;

    @Column(nullable = false)
    private String receiverId;
}
