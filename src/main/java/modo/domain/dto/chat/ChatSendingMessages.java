package modo.domain.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import modo.domain.entity.ChatMessages;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ChatSendingMessages {
    private Long id;
    private String sender;
    private String receiver;
    private String timeStamp;

    public ChatMessages toEntity() {
        return ChatMessages.builder()
                .sender(sender)
                .receiver(receiver)
                .timeStamp(LocalDateTime.parse(timeStamp))
                .build();
    }
}
