package modo.socket;

import modo.StaticResources;
import modo.domain.dto.chat.ChatSendingMessages;
import modo.repository.*;
import modo.service.WebSocketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class WebSocketServiceTest {

    @Autowired
    ChatRoomsRepository chatRoomsRepository;

    @Autowired
    ChatMessagesRepository chatMessagesRepository;

    @Autowired
    BooksRepository booksRepository;

    @Autowired
    UsersRepository usersRepository;

    @Mock
    SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    PicturesRepository picturesRepository;

    @Autowired
    UsersHistoryRepository usersHistoryRepository;

    WebSocketService webSocketService;

    @BeforeEach
    void injectRepositoryToWebSocketService() {
        webSocketService = new WebSocketService(chatRoomsRepository, chatMessagesRepository, simpMessagingTemplate, booksRepository, usersRepository);
    }

    @BeforeEach
    void saveTestSenderAndReceiverAndBook() {
        saveSender();
        saveReceiver();
        saveBooks();
    }

    @BeforeEach
    void tearDown() {
        chatRoomsRepository.deleteAllInBatch();
        chatMessagesRepository.deleteAllInBatch();
        picturesRepository.deleteAllInBatch();
        booksRepository.deleteAllInBatch();
        usersHistoryRepository.deleteAllInBatch();
        usersRepository.deleteAllInBatch();
    }

    @Test
    void Service_새로운채팅방생성_웹소켓메세지수신_테스트() {
        //given
        Long booksId = booksRepository.findAll().get(0).getBooksId();
        ChatSendingMessages messages = new ChatSendingMessages(booksId, StaticResources.senderId, StaticResources.receiverId, LocalDateTime.now().toString());

        //when
        webSocketService.sendMessages(messages);

        //then
        assertThat(chatRoomsRepository.findAll().size()).isEqualTo(1);
        assertThat(chatMessagesRepository.findAll().size()).isEqualTo(1);
        assertThat(chatRoomsRepository.findAll().get(0).getTimeStamp()).isAfter(LocalDateTime.parse(messages.getTimeStamp()));
        verify(simpMessagingTemplate).convertAndSend("/topic/greetings", messages);
    }

    @Test
    void Service_이미있는채팅방_웹소켓메세지수신_테스트() {

        //given
        Long booksId = booksRepository.findAll().get(0).getBooksId();
        ChatSendingMessages messages = new ChatSendingMessages(booksId, StaticResources.senderId, StaticResources.receiverId, LocalDateTime.now().toString());
        webSocketService.saveChatRooms(messages);

        //when
        webSocketService.sendMessages(messages);

        //then
        assertThat(chatRoomsRepository.findAll().size()).isEqualTo(1);
        assertThat(chatMessagesRepository.findAll().size()).isEqualTo(1);
        assertThat(chatRoomsRepository.findAll().get(0).getTimeStamp()).isAfter(LocalDateTime.parse(messages.getTimeStamp()));
        verify(simpMessagingTemplate).convertAndSend("/topic/greetings", messages);
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
