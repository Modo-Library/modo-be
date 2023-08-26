package modo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import modo.domain.dto.chat.ChatSendingMessages;
import modo.domain.entity.Books;
import modo.domain.entity.ChatRooms;
import modo.domain.entity.ChatRoomsUsers;
import modo.domain.entity.Users;
import modo.exception.chatException.ChatRoomsNotExistException;
import modo.repository.*;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Log4j2
@RequiredArgsConstructor
@Service
public class ChatService {
    private final ChatRoomsRepository chatRoomsRepository;
    private final ChatMessagesRepository chatMessagesRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final BooksRepository booksRepository;
    private final UsersRepository usersRepository;
    private final ChatRoomsUsersRepository chatRoomsUsersRepository;

    @Cacheable(value = "ChatRooms", key = "'BooksId' + #messages.getBooksId() + 'Sender'+ #messages.getSender() + 'Receiver'+ #messages.getReceiver()")
    public ChatRooms findChatRooms(ChatSendingMessages messages) {

        Long chatRoomsId = chatRoomsUsersRepository.findChatRoomsUsersIdBySenderIdAndReceiverIdAAndBooksId(messages.getSender(), messages.getReceiver(), messages.getBooksId())
                .orElseThrow(() -> new ChatRoomsNotExistException());

        return chatRoomsRepository.findChatRoomsByIdFetchChatMessagesList(chatRoomsId)
                .orElseThrow(() -> new IllegalArgumentException());
    }

    @Transactional
    public ChatRooms saveChatRooms(ChatSendingMessages messages) {
        Users sender = usersRepository.findUsersByIdFetchChatRoomsList(messages.getSender())
                .orElseThrow(() -> new IllegalArgumentException("Cannot find sender : " + messages.getSender()));

        Users receiver = usersRepository.findUsersByIdFetchChatRoomsList(messages.getReceiver())
                .orElseThrow(() -> new IllegalArgumentException("Cannot find receiver : " + messages.getReceiver()));

        Books books = booksRepository.findById(messages.getBooksId())
                .orElseThrow(() -> new IllegalArgumentException("Cannot find books : " + messages.getBooksId()));

        String imgUrl = books.getImgUrl();

        ChatRooms chatRooms = ChatRooms.builder()
                .imgUrl(imgUrl)
                .timeStamp(LocalDateTime.parse(messages.getTimeStamp()))
                .usersList(List.of(sender, receiver))
                .build();

        sender.addChatRooms(chatRooms);
        receiver.addChatRooms(chatRooms);

        chatRooms = chatRoomsRepository.save(chatRooms);

        ChatRoomsUsers chatRoomsUsers = ChatRoomsUsers.builder()
                .chatRoomsId(chatRooms.getChatRoomsId())
                .booksId(books.getBooksId())
                .senderId(messages.getSender())
                .receiverId(messages.getReceiver())
                .build();

        chatRoomsUsersRepository.save(chatRoomsUsers);

        return chatRooms;
    }
}
