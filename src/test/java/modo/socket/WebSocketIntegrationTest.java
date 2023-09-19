package modo.socket;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import modo.StaticResources;
import modo.auth.JwtTokenProvider;
import modo.domain.dto.chat.ChatSendingMessages;
import modo.repository.*;
import modo.service.ChatService;
import modo.service.WebSocketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(RestDocumentationExtension.class)
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Log4j2
public class WebSocketIntegrationTest {
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

    @Autowired
    private PicturesRepository picturesRepository;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private String accessToken;

    @BeforeEach
    void saveTestSenderAndReceiverAndBook() throws Exception {
        saveSender();
        saveReceiver();
        saveBooks();
        createAccessToken();
    }

    @BeforeEach
    void setUpMockMvcForRestDocs(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .apply(springSecurity())
                .build();
    }

    @BeforeEach
    void tearDown() {
        chatRoomsUsersRepository.deleteAllInBatch();
        chatMessagesRepository.deleteAllInBatch();
        chatRoomsRepository.deleteAllInBatch();
        picturesRepository.deleteAllInBatch();
        booksRepository.deleteAllInBatch();
        usersHistoryRepository.deleteAllInBatch();
        usersRepository.deleteAllInBatch();
    }

    @Test
    void Integration_findChatRoomsList_테스트() throws Exception {
        Long booksId = booksRepository.findAll().get(0).getBooksId();
        ChatSendingMessages messages = new ChatSendingMessages(booksId, StaticResources.senderId, StaticResources.receiverId, StaticResources.testMessages);
        webSocketService.sendMessages(messages);

        mockMvc.perform(get("/api/v1/findChatRoomsList")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("token", accessToken)
                )
                .andExpect(status().isOk())
                .andDo(document("Chat-findChatRoomsList",
                        Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                        requestHeaders(
                                headerWithName("token").description("Wrong access Token value")
                        ),
                        responseFields(
                                fieldWithPath("[].chatRoomsId").description("Each ChatRooms Id").type(JsonFieldType.NUMBER),
                                fieldWithPath("[].usersIdList").description("Each ChatRooms usersIdList").type(JsonFieldType.ARRAY),
                                fieldWithPath("[].imgUrl").description("Each ChatRooms timeStamp").type(JsonFieldType.STRING),
                                fieldWithPath("[].timeStamp").description("Each ChatRooms imgUrl").type(JsonFieldType.STRING)
                        )))
                .andDo(print());
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

    private void createAccessToken() throws Exception {
        accessToken = jwtTokenProvider.createAccessToken(StaticResources.senderId);
    }

}
