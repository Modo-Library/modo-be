package modo.domain.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import modo.domain.entity.ChatMessages;

import java.time.LocalDateTime;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ChatSendingMessages {
    private Long booksId;
    private String sender;
    private String receiver;
    private String content;

    public ChatMessages toEntity() {
        return ChatMessages.builder()
                .sender(sender)
                .receiver(receiver)
                .content(content)
                .timeStamp(LocalDateTime.now())
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatSendingMessages that = (ChatSendingMessages) o;
        return Objects.equals(booksId, that.booksId) &&
                Objects.equals(sender, that.sender) &&
                Objects.equals(receiver, that.receiver) &&
                Objects.equals(content, that.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(booksId, sender, receiver, content);
    }


}
