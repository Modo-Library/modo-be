package modo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import modo.domain.dto.chat.ChatSendingMessages;
import modo.domain.entity.Books;
import modo.domain.entity.ChatMessages;
import modo.domain.entity.ChatRooms;
import modo.domain.entity.Users;
import modo.repository.BooksRepository;
import modo.repository.ChatMessagesRepository;
import modo.repository.ChatRoomsRepository;
import modo.repository.UsersRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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

    public void sendMessages(ChatSendingMessages messages) {
        // 보내려는 메세지가 새로운 채팅방이라면 새로운 채팅방 생성
        if (chatRoomsRepository.existsById(messages.getId()))
            saveChatRooms(messages);

        // 보내려는 메세지의 채팅방 조회
        ChatRooms chatRooms = chatRoomsRepository.findById(messages.getId())
                .orElseThrow(() -> new IllegalArgumentException("ChatRooms with id : " + messages.getId() + "is not exist!"));

        // 새로운 채팅 메세지 생성 및 저장
        ChatMessages chatMessages = messages.toEntity();
        chatMessages.setChatRooms(chatRooms);
        chatRooms.addChatMessages(chatMessages);
        chatMessagesRepository.save(chatMessages);

        // 채팅방 timeStamp 최신화
        chatRooms.setTimeStampToNow();

        // 소켓으로 메세지 전송
        simpMessagingTemplate.convertAndSend("/topic/greetings", messages);
    }

    public void saveChatRooms(ChatSendingMessages messages) {
        Users sender = usersRepository.findById(messages.getSender())
                .orElseThrow(() -> new IllegalArgumentException("Cannot find sender : " + messages.getSender()));

        Users receiver = usersRepository.findById(messages.getReceiver())
                .orElseThrow(() -> new IllegalArgumentException("Cannot find receiver : " + messages.getReceiver()));

        Books books = booksRepository.findById(messages.getId())
                .orElseThrow(() -> new IllegalArgumentException("Cannot find books : " + messages.getId()));

        ChatRooms chatRooms = ChatRooms.builder()
                .chatRoomsId(messages.getId())
                .imgUrl(books.getImgUrl())
                .timeStamp(LocalDateTime.parse(messages.getTimeStamp()))
                .usersList(List.of(sender, receiver))
                .build();

        sender.addChatRooms(chatRooms);
        receiver.addChatRooms(chatRooms);

        chatRoomsRepository.save(chatRooms);
    }

}
