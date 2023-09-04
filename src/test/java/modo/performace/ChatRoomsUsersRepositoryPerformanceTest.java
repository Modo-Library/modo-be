package modo.performace;

import lombok.extern.log4j.Log4j2;
import modo.StaticResources;
import modo.auth.JwtTokenProvider;
import modo.domain.dto.chat.ChatSendingMessages;
import modo.repository.*;
import modo.service.ChatService;
import modo.service.WebSocketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

@Log4j2
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class ChatRoomsUsersRepositoryPerformanceTest {
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

    @Autowired
    UsersHistoryRepository usersHistoryRepository;

    @Autowired
    ChatRoomsUsersRepository chatRoomsUsersRepository;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void saveTestSenderAndReceiverAndBook() {
        saveSender();
        saveReceiver();
        saveThirdUsers();
        saveFourthUsers();
        saveBooks();
    }

    @BeforeEach
    void tearDown() {
        chatRoomsUsersRepository.deleteAllInBatch();
        chatMessagesRepository.deleteAllInBatch();
        chatRoomsRepository.deleteAllInBatch();
        booksRepository.deleteAllInBatch();
        usersHistoryRepository.deleteAllInBatch();
        usersRepository.deleteAllInBatch();
    }

    @Test
    void Repository_Performance_만개저장되어있을떄_아이디로채팅방조회_성능테스트() {

        saveBooks();
        Long booksId = booksRepository.findAll().get(1).getBooksId();
        for (int i = 0; i < 10000; i++) {
            ChatSendingMessages messages = new ChatSendingMessages(booksId, "thirdUsers", "fourthUsers", LocalDateTime.now().toString(), StaticResources.testMessages);
            webSocketService.sendMessages(messages);
        }

        booksId = booksRepository.findAll().get(0).getBooksId();
        ChatSendingMessages messages = new ChatSendingMessages(booksId, StaticResources.senderId, StaticResources.receiverId, LocalDateTime.now().toString(), StaticResources.testMessages);
        webSocketService.sendMessages(messages);

        long start = System.currentTimeMillis();
        chatRoomsUsersRepository.findChatRoomsByUsersId(StaticResources.senderId);
        long finish = System.currentTimeMillis();
        log.info("findChatRoomsByUsersId Execution Time : {}", finish - start);

        start = System.currentTimeMillis();
        chatRoomsUsersRepository.findChatRoomsByUsersId1(StaticResources.senderId);
        finish = System.currentTimeMillis();
        log.info("findChatRoomsByUsersId1 Execution Time : {}", finish - start);


    }

    private void saveSender() {
        usersRepository.save(StaticResources.senderSaveRequestDto.toEntity());
    }

    private void saveReceiver() {
        usersRepository.save(StaticResources.receiverSaveRequestDto.toEntity());
    }

    private void saveThirdUsers() {
        usersRepository.save(StaticResources.thirdUsersSaveRequestDto.toEntity());
    }

    private void saveFourthUsers() {
        usersRepository.save(StaticResources.fourthUsersSaveRequestDto.toEntity());
    }

    private void saveBooks() {
        booksRepository.save(StaticResources.booksSaveRequestDto.toEntity());
    }
}
