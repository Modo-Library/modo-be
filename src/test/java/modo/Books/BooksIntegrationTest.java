package modo.Books;

import com.fasterxml.jackson.databind.ObjectMapper;
import modo.auth.JwtTokenProvider;
import modo.domain.dto.books.BooksResponseDto;
import modo.domain.dto.books.BooksSaveRequestDto;
import modo.domain.dto.users.Users.UsersSaveRequestDto;
import modo.domain.entity.Books;
import modo.enums.BooksStatus;
import modo.repository.AccessTokenRepository;
import modo.repository.BooksRepository;
import modo.repository.UsersHistoryRepository;
import modo.repository.UsersRepository;
import modo.service.BooksService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(RestDocumentationExtension.class)
public class BooksIntegrationTest {

    @Autowired
    private BooksRepository booksRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private UsersHistoryRepository usersHistoryRepository;

    @Autowired
    private AccessTokenRepository accessTokenRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private String accessToken;
    private String refreshToken;

    @BeforeEach
    void setUpMockMvcForRestDocs(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .apply(springSecurity())
                .build();
    }

    @BeforeEach
    void tearDown() {
        booksRepository.deleteAllInBatch();
        usersHistoryRepository.deleteAllInBatch();
        usersRepository.deleteAllInBatch();
        accessTokenRepository.deleteAll();
    }

    @Test
    void Integration_S3_PreSignedURL_발급_테스트() throws Exception {
        // Create new testUser and new access, refresh token
        saveNewTestUsersAndCreateNewToken();
        final String testKeyName = "testKeyName";

        mockMvc.perform(post("/api/v1/books/preUrl?keyName=" + testKeyName)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("token", accessToken)
                )
                .andExpect(status().isOk())
                .andDo(document("Books-createPreUrl",
                        Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                        queryParameters(
                                parameterWithName("keyName").description("keyName for PreUrl")
                        )))
                .andDo(print());
    }

    @Test
    void Integration_책저장_테스트() throws Exception {
        saveNewTestUsersAndCreateNewToken();
        BooksSaveRequestDto requestDto = BooksSaveRequestDto.builder()
                .name(testName)
                .price(testPrice)
                .status(testStatus.toString())
                .description(testDescription)
                .imgUrl(testImgUrl)
                .usersId(testUsersId)
                .build();

        mockMvc.perform(post("/api/v1/books/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .header("token", accessToken)
                )
                .andExpect(status().isOk())
                .andDo(document("Books-save",
                        Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                        requestFields(
                                fieldWithPath("name").description("Saving book's name"),
                                fieldWithPath("price").description("Saving book's price"),
                                fieldWithPath("status").description("Saving book's status"),
                                fieldWithPath("description").description(""),
                                fieldWithPath("imgUrl").description("Saving user's longitude. Type : double"),
                                fieldWithPath("usersId").description("Saving user's longitude. Type : double")
                        ),
                        responseFields(
                                fieldWithPath("booksId").description("Saving user's usersId"),
                                fieldWithPath("name").description("Saving user's nickname"),
                                fieldWithPath("price").description("Saving user's average reviewScore. Type : double. Default :0.0"),
                                fieldWithPath("status").description("Saving user's total reviewCount. Default : 0L"),
                                fieldWithPath("deadline").description("Saving user's history : total rentingCount. Default : 0L"),
                                fieldWithPath("description").description("Saving user's history : total returningCount. Default : 0L"),
                                fieldWithPath("imgUrl").description("Saving user's history : total buyCount. Default : 0L"),
                                fieldWithPath("createdAt").description("Saving user's history : total sellCount. Default : 0L"),
                                fieldWithPath("modifiedAt").description("Saving user's history : total sellCount. Default : 0L")
                        )))
                .andDo(print());
    }


    private void saveNewTestUsersAndCreateNewToken() throws Exception {
        mockMvc.perform(post("/api/v2/users/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUsersSaveRequestDto))
                )
                .andExpect(status().isOk())
                .andDo(print());

        accessToken = jwtTokenProvider.createAccessToken(testUsersId);
        refreshToken = jwtTokenProvider.createRefreshToken(testUsersId);
    }


    static final String testUsersId = "testUsersId";
    static final String testNickname = "testNickname";
    static final double testX = 1.1;
    static final double testY = 2.2;
    static final double testReviewScore = 0.0;
    static final Long testReviewCount = 0L;

    static final String testName = "스프링으로 하는 마이크로서비스 구축";
    static final Long testPrice = 40000L;
    static final BooksStatus testStatus = BooksStatus.AVAILABLE;
    static final String testDescription = "완전 새 책";
    static final String testImgUrl = "s3://testImgUrl.com";

    static final UsersSaveRequestDto testUsersSaveRequestDto = UsersSaveRequestDto.builder()
            .usersId(testUsersId)
            .nickname(testNickname)
            .latitude(testX)
            .longitude(testY)
            .build();
}
