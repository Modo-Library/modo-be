package modo.chat;

import lombok.extern.log4j.Log4j2;
import modo.StaticResources;
import modo.domain.dto.chat.ChatSendingMessages;
import modo.domain.entity.Books;
import modo.domain.entity.ChatMessages;
import modo.domain.entity.ChatRooms;
import modo.domain.entity.Users;
import modo.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Log4j2
public class ChatRoomsRepositoryTest {

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

    @BeforeEach
    void tearDown() {
        chatRoomsUsersRepository.deleteAllInBatch();
        chatMessagesRepository.deleteAllInBatch();
        chatRoomsRepository.deleteAllInBatch();
        booksRepository.deleteAllInBatch();
        usersHistoryRepository.deleteAllInBatch();
        usersRepository.deleteAllInBatch();
    }

    @BeforeEach
    void saveTestSenderAndRecevierAndBook() {
        saveSender();
        saveReceiver();
        saveBooks();
    }

    @Test
    void Repository_채팅방PK로_채팅메세지리스트조회() {
        // Given
        Users sender = usersRepository.findAll().get(0);
        Users receiver = usersRepository.findAll().get(1);
        Books books = booksRepository.findAll().get(0);

        ChatRooms chatRooms = ChatRooms.builder()
                .imgUrl(books.getImgUrl())
                .timeStamp(LocalDateTime.now())
                .build();

        chatRooms = chatRoomsRepository.save(chatRooms);

        for (int i = 0; i < 10; i++) {
            ChatSendingMessages chatSendingMessages = ChatSendingMessages.builder()
                    .booksId(books.getBooksId())
                    .sender(sender.getUsersId())
                    .receiver(receiver.getUsersId())
                    .content("This is " + i + " messages")
                    .build();

            ChatMessages chatMessages = chatSendingMessages.toEntity();
            chatMessages.setChatRooms(chatRooms);
            chatRooms.addChatMessages(chatMessages);

            chatMessagesRepository.save(chatMessages);
        }

        // When
        ChatRooms result = chatRoomsRepository.findChatRoomsByIdFetchChatMessagesList(chatRooms.getChatRoomsId())
                .orElseThrow(() -> new IllegalArgumentException());

        // Then
        assertThat(result.getChatMessagesList().size()).isEqualTo(10L);
        assertThat(result.getChatMessagesList().get(5).getContent()).isEqualTo("This is 5 messages");
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
