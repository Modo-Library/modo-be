package modo.socket;

import lombok.extern.log4j.Log4j2;
import modo.StaticResources;
import modo.domain.dto.chat.ChatSendingMessages;
import modo.repository.BooksRepository;
import modo.repository.ChatMessagesRepository;
import modo.repository.ChatRoomsRepository;
import modo.repository.UsersRepository;
import modo.service.ChatService;
import modo.service.WebSocketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Log4j2
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class WebSocketServiceTest {

    /*
    캐시가 정상적으로 작동하는지 알기 위해선
    `CacheConfiguration`을 import 해야하기 때문에
    WebSocketServiceTest는 @SpringBootTest로 진행
     */

    @Autowired
    WebSocketService webSocketService;

    @Autowired
    ChatService chatService;

    @Autowired
    ChatRoomsRepository chatRoomsRepository;

    @Autowired
    ChatMessagesRepository chatMessagesRepository;

    @Autowired
    BooksRepository booksRepository;

    @Autowired
    UsersRepository usersRepository;

    @BeforeEach
    void saveTestSenderAndReceiverAndBook() {
        saveSender();
        saveReceiver();
        saveBooks();
    }

    @Test
    void WebSocketService_sendMessage_캐시테스트() throws InterruptedException {
        Long booksId = booksRepository.findAll().get(0).getBooksId();
        ChatSendingMessages messages = new ChatSendingMessages(booksId, StaticResources.senderId, StaticResources.receiverId, LocalDateTime.now().toString(), StaticResources.testMessages);

        long start = System.currentTimeMillis();
        webSocketService.sendMessages(messages);
        long finish = System.currentTimeMillis();
        log.info("Execution Time : {}", finish - start);

        ChatSendingMessages anotherMessages = new ChatSendingMessages(booksId, StaticResources.senderId, StaticResources.receiverId, LocalDateTime.now().toString(), StaticResources.testMessages);
        start = System.currentTimeMillis();
        webSocketService.sendMessages(anotherMessages);
        finish = System.currentTimeMillis();
        log.info("Execution Time : {}", finish - start);

        ChatSendingMessages theOtherMessages = new ChatSendingMessages(booksId, StaticResources.senderId, StaticResources.receiverId, LocalDateTime.now().toString(), StaticResources.testMessages);
        start = System.currentTimeMillis();
        webSocketService.sendMessages(theOtherMessages);
        finish = System.currentTimeMillis();
        log.info("Execution Time : {}", finish - start);

        assertThat(chatRoomsRepository.findAll().size()).isEqualTo(1);
        assertThat(chatMessagesRepository.findAll().size()).isEqualTo(3);
        Long targetChatRoomsId = chatRoomsRepository.findAll().get(0).getChatRoomsId();
        assertThat(chatRoomsRepository.findChatRoomsByIdFetchChatMessagesList(targetChatRoomsId).get().getChatMessagesList().size()).isEqualTo(3);
        assertThat(chatRoomsRepository.findAll().get(0).getTimeStamp()).isAfter(LocalDateTime.parse(messages.getTimeStamp()));
    }

    @Test
    void ChatService_findChatRooms_쿼리테스트() {
        Long booksId = booksRepository.findAll().get(0).getBooksId();
        ChatSendingMessages messages = new ChatSendingMessages(booksId, StaticResources.senderId, StaticResources.receiverId, LocalDateTime.now().toString(), StaticResources.testMessages);
        ChatSendingMessages reverseMessages = new ChatSendingMessages(booksId, StaticResources.receiverId, StaticResources.senderId, LocalDateTime.now().toString(), StaticResources.testMessages);

        webSocketService.sendMessages(messages);
        assertThrows(RuntimeException.class, () -> chatService.findChatRooms(reverseMessages));
    }

    private void saveSender() {
        usersRepository.save(StaticResources.senderSaveRequestDto.toEntity());
    }

    private void saveReceiver() {
        usersRepository.save(StaticResources.receiverSaveRequestDto.toEntity());
    }

    private void saveBooks() {
        booksRepository.save(StaticResources.booksSaveRequestDto.toEntity());
    }

}
