package modo.socket;

import lombok.extern.log4j.Log4j2;
import modo.StaticResources;
import modo.auth.JwtTokenProvider;
import modo.domain.dto.chat.ChatRoomsResponseDto;
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
import java.util.List;

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
    void WebSocketService_sendMessage_캐시테스트() throws InterruptedException {
        Long booksId = booksRepository.findAll().get(0).getBooksId();
        ChatSendingMessages messages = new ChatSendingMessages(booksId, StaticResources.senderId, StaticResources.receiverId, StaticResources.testMessages);
        LocalDateTime messagesSendingTime = LocalDateTime.now();

        long start = System.currentTimeMillis();
        webSocketService.sendMessages(messages);
        long finish = System.currentTimeMillis();
        log.info("Execution Time : {}", finish - start);

        ChatSendingMessages anotherMessages = new ChatSendingMessages(booksId, StaticResources.senderId, StaticResources.receiverId, StaticResources.testMessages);
        start = System.currentTimeMillis();
        webSocketService.sendMessages(anotherMessages);
        finish = System.currentTimeMillis();
        log.info("Execution Time : {}", finish - start);

        ChatSendingMessages theOtherMessages = new ChatSendingMessages(booksId, StaticResources.senderId, StaticResources.receiverId, StaticResources.testMessages);
        start = System.currentTimeMillis();
        webSocketService.sendMessages(theOtherMessages);
        finish = System.currentTimeMillis();
        log.info("Execution Time : {}", finish - start);

        assertThat(chatRoomsRepository.findAll().size()).isEqualTo(1);
        assertThat(chatMessagesRepository.findAll().size()).isEqualTo(3);
        Long targetChatRoomsId = chatRoomsRepository.findAll().get(0).getChatRoomsId();
        assertThat(chatRoomsUsersRepository.findAll().get(0).getSenderId()).isEqualTo(StaticResources.senderId);
        assertThat(chatRoomsUsersRepository.findAll().get(0).getReceiverId()).isEqualTo(StaticResources.receiverId);
        assertThat(chatRoomsRepository.findChatRoomsByIdFetchChatMessagesList(targetChatRoomsId).get().getChatMessagesList().size()).isEqualTo(3);
        assertThat(chatRoomsRepository.findAll().get(0).getTimeStamp()).isAfter(messagesSendingTime);
    }

    @Test
    void ChatService_findChatRooms_쿼리테스트() {
        Long booksId = booksRepository.findAll().get(0).getBooksId();
        ChatSendingMessages messages = new ChatSendingMessages(booksId, StaticResources.senderId, StaticResources.receiverId, StaticResources.testMessages);
        ChatSendingMessages reverseMessages = new ChatSendingMessages(booksId, StaticResources.receiverId, StaticResources.senderId, StaticResources.testMessages);

        webSocketService.sendMessages(messages);
        assertThrows(RuntimeException.class, () -> chatService.findChatRooms(reverseMessages));
    }

    @Test
    void ChatService_findChatRoomsList_테스트() throws InterruptedException {
        // Send first messages for books1
        // ChatRooms will be created
        Long booksId = booksRepository.findAll().get(0).getBooksId();
        ChatSendingMessages messages = new ChatSendingMessages(booksId, StaticResources.senderId, StaticResources.receiverId, StaticResources.testMessages);
        webSocketService.sendMessages(messages);

        Thread.sleep(1000);

        // Send second messages for books2
        // Another chatRooms will be created
        saveBooks();
        booksId = booksRepository.findAll().get(1).getBooksId();
        messages = new ChatSendingMessages(booksId, StaticResources.senderId, StaticResources.receiverId, StaticResources.testMessages);
        webSocketService.sendMessages(messages);

        // Create new accessToken
        String accessTokenForSender = jwtTokenProvider.createAccessToken(StaticResources.senderId);
        String accessTokenForReceiver = jwtTokenProvider.createAccessToken(StaticResources.receiverId);

        long start = System.currentTimeMillis();
        // Then, there should be two chatRooms for sender and receiver
        List<ChatRoomsResponseDto> chatRoomsResponseDtoListForSender = chatService.findChatRoomsList(accessTokenForSender);
        long finish = System.currentTimeMillis();
        log.info("Execution Time : {}", finish - start);

        assertThat(chatRoomsResponseDtoListForSender.size()).isEqualTo(2);
        assertThat(chatRoomsResponseDtoListForSender.get(0).getUsersIdList().size()).isEqualTo(2);
        assertThat(chatRoomsResponseDtoListForSender.get(0).getTimeStamp()).isBeforeOrEqualTo(LocalDateTime.now());
        assertThat(chatRoomsResponseDtoListForSender.get(1).getUsersIdList().size()).isEqualTo(2);
        assertThat(chatRoomsResponseDtoListForSender.get(1).getTimeStamp()).isBeforeOrEqualTo(LocalDateTime.now());
        assertThat(chatRoomsResponseDtoListForSender.get(1).getTimeStamp()).isAfter(chatRoomsResponseDtoListForSender.get(0).getTimeStamp());

        List<ChatRoomsResponseDto> chatRoomsResponseDtoListForReceiver = chatService.findChatRoomsList(accessTokenForReceiver);
        assertThat(chatRoomsResponseDtoListForReceiver.size()).isEqualTo(2);
        assertThat(chatRoomsResponseDtoListForReceiver.get(0).getUsersIdList().size()).isEqualTo(2);
        assertThat(chatRoomsResponseDtoListForReceiver.get(0).getTimeStamp()).isBeforeOrEqualTo(LocalDateTime.now());
        assertThat(chatRoomsResponseDtoListForReceiver.get(1).getUsersIdList().size()).isEqualTo(2);
        assertThat(chatRoomsResponseDtoListForReceiver.get(1).getTimeStamp()).isBeforeOrEqualTo(LocalDateTime.now());
        assertThat(chatRoomsResponseDtoListForReceiver.get(1).getTimeStamp()).isAfter(chatRoomsResponseDtoListForReceiver.get(0).getTimeStamp());
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
