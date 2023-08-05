package modo.Books;

import com.fasterxml.jackson.databind.ObjectMapper;
import modo.auth.JwtTokenProvider;
import modo.domain.dto.books.BooksResponseDto;
import modo.domain.dto.books.BooksSaveRequestDto;
import modo.domain.dto.books.BooksUpdateRequestDto;
import modo.domain.dto.pictures.PicturesSaveRequestDto;
import modo.domain.dto.users.Users.UsersSaveRequestDto;
import modo.domain.entity.Books;
import modo.enums.BooksStatus;
import modo.repository.*;
import modo.service.BooksService;
import org.geolatte.geom.M;
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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(RestDocumentationExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
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
    private PicturesRepository picturesRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private String accessToken;
    private String refreshToken;
    private Books books;

    @BeforeEach
    void setUpMockMvcForRestDocs(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .apply(springSecurity())
                .build();
    }

    @BeforeEach
    void tearDown() {
        picturesRepository.deleteAllInBatch();
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
                        requestHeaders(
                                headerWithName("token").description("Access -token value")
                        ),
                        queryParameters(
                                parameterWithName("keyName").description("keyName for PreUrl")
                        )))
                .andDo(print());
    }

    @Test
    void Integration_책저장_테스트() throws Exception {
        saveNewTestUsersAndCreateNewToken();

        mockMvc.perform(post("/api/v1/books/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(booksSaveRequestDto))
                        .header("token", accessToken)
                )
                .andExpect(status().isOk())
                .andDo(document("Books-save",
                        Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                        requestFields(
                                fieldWithPath("name").description("Saving book's name").type(JsonFieldType.STRING),
                                fieldWithPath("price").description("Saving book's price").type(JsonFieldType.NUMBER),
                                fieldWithPath("status").description("Saving book's status").type(JsonFieldType.STRING),
                                fieldWithPath("description").description("Saving book's description").type(JsonFieldType.STRING),
                                fieldWithPath("imgUrl").description("Saving book's thumbnail image's URL").type(JsonFieldType.STRING),
                                fieldWithPath("usersId").description("Saving book's owner Id").type(JsonFieldType.STRING),
                                fieldWithPath("picturesSaveRequestDtoList").description("Saving Book's pictures list").type(JsonFieldType.ARRAY),
                                fieldWithPath("picturesSaveRequestDtoList.[].imgUrl").description("Each picture's imgUrl").type(JsonFieldType.STRING),
                                fieldWithPath("picturesSaveRequestDtoList.[].filename").description("Each picture's filename").type(JsonFieldType.STRING)
                        ),
                        requestHeaders(
                                headerWithName("token").description("Access token value")
                        ),
                        responseFields(
                                fieldWithPath("booksId").description("Saving book's booksId").type(JsonFieldType.NUMBER),
                                fieldWithPath("name").description("Saving book's name").type(JsonFieldType.STRING),
                                fieldWithPath("price").description("Saving book's price").type(JsonFieldType.NUMBER),
                                fieldWithPath("status").description("Saving book's status").type(JsonFieldType.STRING),
                                fieldWithPath("deadline").description("Saving book's deadline. If book is not on renting, this fields will be NULL").type(JsonFieldType.NULL),
                                fieldWithPath("description").description("Saving book's description").type(JsonFieldType.STRING),
                                fieldWithPath("imgUrl").description("Saving user's imgUrl").type(JsonFieldType.STRING),
                                fieldWithPath("createdAt").description("Saving user's created LocalDateTime").type(JsonFieldType.STRING),
                                fieldWithPath("modifiedAt").description("Saving user's modified LocalDateTime").type(JsonFieldType.STRING)
                        )))
                .andDo(print());
    }

    @Test
    void Integration_책업데이트_테스트() throws Exception {
        saveNewTestUsersAndCreateNewToken();
        saveNewBooksAndPictures();

        Long testBooksId = books.getBooksId();
        String testUpdateName = "update" + testName;
        Long testUpdatePrice = testPrice + 10000L;
        BooksStatus testUpdateStatus = BooksStatus.RENTING;
        String testUpdateDescription = "update" + testDescription;
        String testUpdateImgUrl = testImgUrl + "2";

        BooksUpdateRequestDto updateRequestDto = BooksUpdateRequestDto.builder()
                .booksId(testBooksId)
                .name(testUpdateName)
                .price(testUpdatePrice)
                .status(testUpdateStatus.toString())
                .description(testUpdateDescription)
                .imgUrl(testUpdateImgUrl)
                .build();

        mockMvc.perform(put("/api/v1/books/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequestDto))
                        .header("token", accessToken)
                )
                .andExpect(status().isOk())
                .andDo(document("Books-update",
                        Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                        requestFields(
                                fieldWithPath("booksId").description("Update target book's id").type(JsonFieldType.NUMBER),
                                fieldWithPath("name").description("Update target name").type(JsonFieldType.STRING),
                                fieldWithPath("price").description("Update target price").type(JsonFieldType.NUMBER),
                                fieldWithPath("status").description("Update target status").type(JsonFieldType.STRING),
                                fieldWithPath("description").description("Update target description").type(JsonFieldType.STRING),
                                fieldWithPath("imgUrl").description("Update target imgUrl").type(JsonFieldType.STRING)
                        ),
                        requestHeaders(
                                headerWithName("token").description("Access token value")
                        ),
                        responseFields(
                                fieldWithPath("booksId").description("Saving book's booksId").type(JsonFieldType.NUMBER),
                                fieldWithPath("name").description("Saving book's name").type(JsonFieldType.STRING),
                                fieldWithPath("price").description("Saving book's price").type(JsonFieldType.NUMBER),
                                fieldWithPath("status").description("Saving book's status").type(JsonFieldType.STRING),
                                fieldWithPath("deadline").description("Saving book's deadline. If book is not on renting, this fields will be NULL").type(JsonFieldType.NULL),
                                fieldWithPath("description").description("Saving book's description").type(JsonFieldType.STRING),
                                fieldWithPath("imgUrl").description("Saving user's imgUrl").type(JsonFieldType.STRING),
                                fieldWithPath("createdAt").description("Saving user's created LocalDateTime").type(JsonFieldType.STRING),
                                fieldWithPath("modifiedAt").description("Saving user's modified LocalDateTime").type(JsonFieldType.STRING)
                        )))
                .andDo(print());
    }

    @Test
    void Integration_책삭제_테스트() throws Exception {
        saveNewTestUsersAndCreateNewToken();
        saveNewBooksAndPictures();

        Long testBooksId = books.getBooksId();

        mockMvc.perform(delete("/api/v1/books/delete?booksId=" + testBooksId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("token", accessToken)
                )
                .andExpect(status().isOk())
                .andDo(document("Books-delete",
                        Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                        queryParameters(
                                parameterWithName("booksId").description("Target book's id for delete")
                        ),
                        requestHeaders(
                                headerWithName("token").description("Access token value")
                        )
                ))
                .andDo(print());

        assertThat(booksRepository.findAll().size()).isZero();
        assertThat(picturesRepository.findAll().size()).isZero();
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

    private void saveNewBooksAndPictures() throws Exception {
        mockMvc.perform(post("/api/v1/books/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(booksSaveRequestDto))
                        .header("token", accessToken)
                )
                .andExpect(status().isOk())
                .andDo(print());

        books = booksRepository.findAll().get(0);
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

    static final String testFilename = "testFilename.jpg";

    static final UsersSaveRequestDto testUsersSaveRequestDto = UsersSaveRequestDto.builder()
            .usersId(testUsersId)
            .nickname(testNickname)
            .latitude(testX)
            .longitude(testY)
            .build();

    static final PicturesSaveRequestDto picturesSaveRequestDto1 = PicturesSaveRequestDto.builder()
            .imgUrl(testImgUrl + "1")
            .filename(testFilename + "1")
            .build();

    static final PicturesSaveRequestDto picturesSaveRequestDto2 = PicturesSaveRequestDto.builder()
            .imgUrl(testImgUrl + "2")
            .filename(testFilename + "2")
            .build();

    static final List<PicturesSaveRequestDto> picturesSaveRequestDtoList = List.of(picturesSaveRequestDto1, picturesSaveRequestDto2);

    static final BooksSaveRequestDto booksSaveRequestDto = BooksSaveRequestDto.builder()
            .name(testName)
            .price(testPrice)
            .status(testStatus.toString())
            .description(testDescription)
            .imgUrl(testImgUrl + "1")
            .usersId(testUsersId)
            .picturesSaveRequestDtoList(picturesSaveRequestDtoList)
            .build();
}
