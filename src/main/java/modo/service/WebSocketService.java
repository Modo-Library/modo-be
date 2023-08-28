package modo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import modo.domain.dto.chat.ChatSendingMessages;
import modo.domain.entity.ChatMessages;
import modo.domain.entity.ChatRooms;
import modo.exception.chatException.ChatRoomsNotExistException;
import modo.repository.BooksRepository;
import modo.repository.ChatMessagesRepository;
import modo.repository.ChatRoomsRepository;
import modo.repository.UsersRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Log4j2
@RequiredArgsConstructor
@Service
public class WebSocketService {
    private final ChatRoomsRepository chatRoomsRepository;
    private final ChatMessagesRepository chatMessagesRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final BooksRepository booksRepository;

    private final UsersRepository usersRepository;
    private final ChatService chatService;

    public void sendMessages(ChatSendingMessages messages) {
        // 보내려는 메세지가 속한 채팅방 조회, 채팅방이 없다면 새롭게 생성
        ChatRooms chatRooms;
        try {
            chatRooms = chatService.findChatRooms(messages);
        } catch (ChatRoomsNotExistException e) {
            chatRooms = chatService.saveChatRooms(messages);
        }
        // 메세지 저장
        saveMessages(messages, chatRooms);
        // 채팅방 내부 사용자 아이디 조회
        List<String> usersIdList = chatRoomsRepository.findUsersIdListByChatRoomsId(chatRooms.getChatRoomsId());
        // 소켓으로 메세지 전송
        usersIdList.stream()
                .forEach((String each) -> {
                    simpMessagingTemplate.convertAndSend("/topic/" + each, messages);
                });
    }

    @Transactional
    public void saveMessages(ChatSendingMessages messages, ChatRooms chatRooms) {
        // 새로운 채팅 메세지 생성
        ChatMessages chatMessages = messages.toEntity();
        chatMessages.setChatRooms(chatRooms);
        chatRooms.addChatMessages(chatMessages);

        // 메세지 저장
        chatMessagesRepository.save(chatMessages);
    }
}
