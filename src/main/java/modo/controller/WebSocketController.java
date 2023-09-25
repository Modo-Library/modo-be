package modo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import modo.domain.dto.chat.ChatSendingMessages;
import modo.service.ChatService;
import modo.service.WebSocketService;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@Log4j2
@Controller
@RequiredArgsConstructor
public class WebSocketController extends BaseController {
    private final WebSocketService webSocketService;
    private final ChatService chatService;

    // client -> server sending endpoint : `/app/sendMessages`
    // server -> client sending endpoint : `/topic/$usersId`
    @MessageMapping("/sendMessages")
    public void sendMessages(ChatSendingMessages message) throws Exception {
        webSocketService.sendMessages(message);
    }

    @GetMapping("api/v1/findChatRoomsList")
    public ResponseEntity<?> findChatRoomsList(@RequestHeader("token") String accessToken) {
        return sendResponse(chatService.findChatRoomsList(accessToken));
    }

    @GetMapping("api/v1/findChatMessagesList")
    public ResponseEntity<?> findChatMessagesList(@RequestParam Long chatRoomsId) throws Exception {
        return sendResponse(chatService.findChatMessagesList(chatRoomsId));
    }
}