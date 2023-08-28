package modo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import modo.domain.dto.chat.ChatSendingMessages;
import modo.service.WebSocketService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Log4j2
@Controller
@RequiredArgsConstructor
public class WebSocketController {
    private final WebSocketService webSocketService;

    // client -> server sending endpoint : `/app/sendMessages`
    // server -> client sending endpoint : `/topic/$usersId`
    @MessageMapping("/sendMessages")
    public void sendMessages(ChatSendingMessages message) throws Exception {
        webSocketService.sendMessages(message);
    }
}