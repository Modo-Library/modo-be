package modo.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
public class ChatRooms {
    @Id
    private Long chatRoomsId;

    @Column(nullable = false)
    private String imgUrl;

    @Column(nullable = false)
    private LocalDateTime timeStamp;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "USERS_CHATROOMS")
    private List<Users> usersList = new ArrayList<>();

    @Builder.Default
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "chatRooms")
    private List<ChatMessages> chatMessagesList = new ArrayList<>();

    public void setTimeStampToNow() {
        this.timeStamp = LocalDateTime.now();
    }

    public void addChatMessages(ChatMessages messages) {
        this.chatMessagesList.add(messages);
    }
}
