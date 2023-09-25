package modo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import modo.auth.JwtTokenProvider;
import modo.domain.dto.chat.ChatRoomsResponseDto;
import modo.domain.dto.chat.ChatSendingMessages;
import modo.domain.entity.*;
import modo.exception.chatException.ChatRoomsNotExistException;
import modo.repository.*;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
    private final JwtTokenProvider jwtTokenProvider;

    @Cacheable(value = "ChatRooms", key = "'BooksId' + #messages.getBooksId() + 'Sender'+ #messages.getSender() + 'Receiver'+ #messages.getReceiver()")
    public ChatRooms findChatRooms(ChatSendingMessages messages) {

        Long chatRoomsId = chatRoomsUsersRepository.findChatRoomsUsersIdBySenderIdAndReceiverIdAAndBooksId(messages.getSender(), messages.getReceiver(), messages.getBooksId())
                .orElseThrow(() -> new ChatRoomsNotExistException());

        // TODO : ChatRoomsUsers의 Sender, Receiver과 ChatSendingMessages의 Sender, Receiver 로직을 정리해야함
        // orElse로 다시 sender와 receiver의 위치를 바꿔서 charRoomsUsersRepository의 함수를 호출한다.
        // 그럼에도 불구하고 없을때는 Exception을 Raise한다.

        return chatRoomsRepository.findChatRoomsByIdFetchChatMessagesList(chatRoomsId)
                .orElseThrow(() -> new IllegalArgumentException());
    }

    @Transactional
    public ChatRooms saveChatRooms(ChatSendingMessages messages) {
        Users sender = usersRepository.findById(messages.getSender())
                .orElseThrow(() -> new IllegalArgumentException("Cannot find sender : " + messages.getSender()));

        Users receiver = usersRepository.findById(messages.getReceiver())
                .orElseThrow(() -> new IllegalArgumentException("Cannot find receiver : " + messages.getReceiver()));

        Books books = booksRepository.findById(messages.getBooksId())
                .orElseThrow(() -> new IllegalArgumentException("Cannot find books : " + messages.getBooksId()));

        String imgUrl = books.getImgUrl();

        ChatRooms chatRooms = ChatRooms.builder()
                .imgUrl(imgUrl)
                .timeStamp(LocalDateTime.now())
                .build();

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

    @Transactional(readOnly = true)
    public List<ChatRoomsResponseDto> findChatRoomsList(String token) {
        String usersId = jwtTokenProvider.getUsersId(token);

        List<ChatRooms> chatRoomsList =
                chatRoomsUsersRepository.findChatRoomsByUsersId(usersId);

        return chatRoomsList.stream()
                .map(each -> {
                    List<String> usersIdList = List.of(
                            chatRoomsUsersRepository.findUsersIdListByChatRoomsId(each.getChatRoomsId())
                                    .split(",")
                    );
                    return new ChatRoomsResponseDto(each, usersIdList);
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ChatMessages> findChatMessagesList(Long chatRoomsId) {
        ChatRooms chatRooms = chatRoomsRepository.findChatRoomsByIdFetchChatMessagesList(chatRoomsId)
                .orElseThrow(() -> new IllegalArgumentException("ChatRooms with id : " + chatRoomsId + " is not exist!"));

        return chatRooms.getChatMessagesList();
    }
}
