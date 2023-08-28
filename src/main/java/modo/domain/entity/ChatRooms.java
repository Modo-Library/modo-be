package modo.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
public class ChatRooms {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chatRoomsId;

    @Column(nullable = false)
    private String imgUrl;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime timeStamp;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "USERS_CHATROOMS")
    private List<Users> usersList = new ArrayList<>();

    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "chatRooms")
    private List<ChatMessages> chatMessagesList = new ArrayList<>();

    public void addChatMessages(ChatMessages messages) {
        this.chatMessagesList.add(messages);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatRooms chatRooms = (ChatRooms) o;
        return Objects.equals(chatRoomsId, chatRooms.chatRoomsId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chatRoomsId);
    }

}
