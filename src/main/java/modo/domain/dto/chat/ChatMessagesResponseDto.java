package modo.domain.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import modo.domain.entity.ChatMessages;

import java.time.LocalDateTime;

@AllArgsConstructor
@Builder
@Getter
public class ChatMessagesResponseDto {

    private String sender;
    private String receiver;
    private String content;
    private LocalDateTime timeStamp;

    public ChatMessagesResponseDto(ChatMessages chatMessages) {
        this.sender = chatMessages.getSender();
        this.receiver = chatMessages.getReceiver();
        this.content = chatMessages.getContent();
        this.timeStamp = chatMessages.getTimeStamp();
    }
}
