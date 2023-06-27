package modo.users;

import com.fasterxml.jackson.databind.ObjectMapper;
import modo.domain.dto.users.Users.UsersResponseDto;
import modo.domain.dto.users.Users.UsersSaveRequestDto;
import modo.domain.dto.users.UsersReview.EachReviewResponseDto;
import modo.domain.dto.users.UsersReview.UsersReviewResponseDto;
import modo.domain.dto.users.UsersReview.UsersReviewSaveRequestDto;
import modo.repository.UsersHistoryRepository;
import modo.repository.UsersRepository;
import modo.repository.UsersReviewRepository;
import modo.util.GeomUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Point;
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

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(RestDocumentationExtension.class)
public class UsersIntegrationTest {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private UsersHistoryRepository usersHistoryRepository;

    @Autowired
    private UsersReviewRepository usersReviewRepository;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();


    @BeforeEach
    void setUpMockMvcForRestDocs(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .build();
    }

    @BeforeEach
    void tearDown() {
        usersReviewRepository.deleteAllInBatch();
        usersHistoryRepository.deleteAllInBatch();
        usersRepository.deleteAllInBatch();
    }

    @Test
    void Integration_회원가입_테스트() throws Exception {

        UsersResponseDto expectedDto = UsersResponseDto.builder()
                .usersId(testUsersId)
                .nickname(testNickname)
                .reviewScore(0.0)
                .reviewCount(0L)
                .rentingCount(0L)
                .returningCount(0L)
                .buyCount(0L)
                .sellCount(0L)
                .build();

        mockMvc.perform(post("/api/v1/users/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUsersSaveRequestDto))
                )
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedDto)))
                .andDo(document("Users-save",
                        Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                        requestFields(
                                fieldWithPath("usersId").description("Saving user's usersId").type(JsonFieldType.STRING),
                                fieldWithPath("password").description("Saving user's password").type(JsonFieldType.STRING),
                                fieldWithPath("nickname").description("Saving user's nickname").type(JsonFieldType.STRING),
                                fieldWithPath("latitude").description("Saving user's latitude. Type : double").type(JsonFieldType.NUMBER),
                                fieldWithPath("longitude").description("Saving user's longitude. Type : double").type(JsonFieldType.NUMBER)
                        ),
                        responseFields(
                                fieldWithPath("usersId").description("Saving user's usersId").type(JsonFieldType.STRING),
                                fieldWithPath("nickname").description("Saving user's nickname").type(JsonFieldType.STRING),
                                fieldWithPath("reviewScore").description("Saving user's average reviewScore. Type : double. Default :0.0").type(JsonFieldType.NUMBER),
                                fieldWithPath("reviewCount").description("Saving user's total reviewCount. Default : 0L").type(JsonFieldType.NUMBER),
                                fieldWithPath("rentingCount").description("Saving user's history : total rentingCount. Default : 0L").type(JsonFieldType.NUMBER),
                                fieldWithPath("returningCount").description("Saving user's history : total returningCount. Default : 0L").type(JsonFieldType.NUMBER),
                                fieldWithPath("buyCount").description("Saving user's history : total buyCount. Default : 0L").type(JsonFieldType.NUMBER),
                                fieldWithPath("sellCount").description("Saving user's history : total sellCount. Default : 0L").type(JsonFieldType.NUMBER)

                        )))
                .andDo(print());

        assertThat(usersRepository.findAll().size()).isEqualTo(1L);
        assertThat(usersHistoryRepository.findAll().size()).isEqualTo(1L);
    }

    @Test
    void Integration_회원조회_테스트() throws Exception {

        // Save Users First
        mockMvc.perform(post("/api/v1/users/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUsersSaveRequestDto))
                )
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/users/findUsers/{usersId}", testUsersId)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(testUsersResponseDto)))
                .andDo(document("Users-findUsers", Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                        pathParameters(
                                parameterWithName("usersId").description("Finding user's usersId")
                        ),
                        responseFields(
                                fieldWithPath("usersId").description("Finding user's usersId").type(JsonFieldType.STRING),
                                fieldWithPath("nickname").description("Finding user's nickname").type(JsonFieldType.STRING),
                                fieldWithPath("reviewScore").description("Finding user's average reviewScore. Type : double. Default :0.0").type(JsonFieldType.NUMBER),
                                fieldWithPath("reviewCount").description("Finding user's total reviewCount. Default : 0L").type(JsonFieldType.NUMBER),
                                fieldWithPath("rentingCount").description("Finding user's history : total rentingCount. Default : 0L").type(JsonFieldType.NUMBER),
                                fieldWithPath("returningCount").description("Finding user's history : total returningCount. Default : 0L").type(JsonFieldType.NUMBER),
                                fieldWithPath("buyCount").description("Finding user's history : total buyCount. Default : 0L").type(JsonFieldType.NUMBER),
                                fieldWithPath("sellCount").description("Finding user's history : total sellCount. Default : 0L").type(JsonFieldType.NUMBER)

                        )))
                .andDo(print());
    }

    @Test
    void Integration_회원조회_리뷰페치_테스트() throws Exception {
        List<EachReviewResponseDto> testReviewResponseDtoList = new ArrayList<>();
        testReviewResponseDtoList.add(testEachReviewResponseDto);

        UsersReviewResponseDto testUsersReviewResponseDto = UsersReviewResponseDto.builder()
                .usersId(testUsersId)
                .nickname(testNickname)
                .reviewScore(5.0)
                .reviewCount(1L)
                .reviewResponseDtoList(testReviewResponseDtoList)
                .build();

        // Save Users First
        mockMvc.perform(post("/api/v1/users/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUsersSaveRequestDto))
                )
                .andExpect(status().isOk());

        // Add Review
        mockMvc.perform(put("/api/v1/users/addReview/{usersId}", testUsersId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(UsersReviewSaveRequestDto.builder().usersId(testUsersId).score(5L).description(testDescription).build()))
                )
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/users/findUsersFetchReview/{usersId}", testUsersId)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(testUsersReviewResponseDto)))
                .andDo(document("Users-findUsersFetchReview",
                        Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                        pathParameters(
                                parameterWithName("usersId").description("Finding user's usersId")
                        ),
                        responseFields(
                                fieldWithPath("usersId").description("Finding user's usersId").type(JsonFieldType.STRING),
                                fieldWithPath("nickname").description("Finding user's nickname").type(JsonFieldType.STRING),
                                fieldWithPath("reviewScore").description("Finding user's average reviewScore. Type : double. Default :0.0").type(JsonFieldType.NUMBER),
                                fieldWithPath("reviewCount").description("Finding user's total reviewCount. Default : 0L").type(JsonFieldType.NUMBER),
                                fieldWithPath("reviewResponseDtoList").description("Finding user's reviewResponseDtoList. List<EachReviewResponseDto>").type(JsonFieldType.ARRAY),
                                fieldWithPath("reviewResponseDtoList.[].usersId").description("Each Review's usersId").type(JsonFieldType.STRING),
                                fieldWithPath("reviewResponseDtoList.[].score").description("Each Review's score").type(JsonFieldType.NUMBER),
                                fieldWithPath("reviewResponseDtoList.[].description").description("Each Review's description").type(JsonFieldType.STRING)
                        )))
                .andDo(print());
    }

    @Test
    void Integration_리뷰추가_테스트() throws Exception {
        // Save Users First
        mockMvc.perform(post("/api/v1/users/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUsersSaveRequestDto))
                )
                .andExpect(status().isOk());

        List<EachReviewResponseDto> testReviewResponseDtoList = new ArrayList<>();
        testReviewResponseDtoList.add(testEachReviewResponseDto);

        UsersReviewResponseDto testUsersReviewResponseDto = UsersReviewResponseDto.builder()
                .usersId(testUsersId)
                .nickname(testNickname)
                .reviewScore(5.0)
                .reviewCount(1L)
                .reviewResponseDtoList(testReviewResponseDtoList)
                .build();

        mockMvc.perform(put("/api/v1/users/addReview/{usersId}", testUsersId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(UsersReviewSaveRequestDto.builder().usersId(testUsersId).score(5L).description(testDescription).build()))
                )
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(testUsersReviewResponseDto)))
                .andDo(document("Users-addReview",
                        Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                        pathParameters(
                                parameterWithName("usersId").description("Adding review target user's usersId")
                        ),
                        requestFields(
                                fieldWithPath("usersId").description("Author user's usersId").type(JsonFieldType.STRING),
                                fieldWithPath("score").description("Review score value").type(JsonFieldType.NUMBER),
                                fieldWithPath("description").description("Review description").type(JsonFieldType.STRING)
                        ),
                        responseFields(
                                fieldWithPath("usersId").description("Finding user's usersId").type(JsonFieldType.STRING),
                                fieldWithPath("nickname").description("Finding user's nickname").type(JsonFieldType.STRING),
                                fieldWithPath("reviewScore").description("Finding user's average reviewScore. Type : double. Default :0.0").type(JsonFieldType.NUMBER),
                                fieldWithPath("reviewCount").description("Finding user's total reviewCount. Default : 0L").type(JsonFieldType.NUMBER),
                                fieldWithPath("reviewResponseDtoList").description("Finding user's reviewResponseDtoList. List<EachReviewResponseDto>").type(JsonFieldType.ARRAY),
                                fieldWithPath("reviewResponseDtoList.[].usersId").description("Each Review's usersId").type(JsonFieldType.STRING),
                                fieldWithPath("reviewResponseDtoList.[].score").description("Each Review's score").type(JsonFieldType.NUMBER),
                                fieldWithPath("reviewResponseDtoList.[].description").description("Each Review's description").type(JsonFieldType.STRING)
                        )))
                .andDo(print());

    }

    static final String testUsersId = "testUsersId";
    static final String testPassword = "testPassword";
    static final String testNickname = "testNickname";
    static final Point testLocation = GeomUtil.createPoint(1.1, 2.2);
    static final double testX = 1.1;
    static final double testY = 2.2;
    static final double testReviewScore = 0.0;
    static final Long testReviewCount = 0L;
    static final String testDescription = "testDescription";

    static final UsersSaveRequestDto testUsersSaveRequestDto = UsersSaveRequestDto.builder()
            .usersId(testUsersId)
            .nickname(testNickname)
            .password(testPassword)
            .latitude(testX)
            .longitude(testY)
            .build();

    static final UsersResponseDto testUsersResponseDto = UsersResponseDto.builder()
            .usersId(testUsersId)
            .nickname(testNickname)
            .reviewScore(testReviewScore)
            .reviewCount(testReviewCount)
            .buyCount(0L)
            .sellCount(0L)
            .rentingCount(0L)
            .returningCount(0L)
            .build();

    static final UsersReviewResponseDto testUsersReviewResponseDto = UsersReviewResponseDto.builder()
            .usersId(testUsersId)
            .nickname(testNickname)
            .reviewScore(testReviewScore)
            .reviewCount(testReviewCount)
            .reviewResponseDtoList(new ArrayList<EachReviewResponseDto>())
            .build();

    static final EachReviewResponseDto testEachReviewResponseDto = EachReviewResponseDto.builder()
            .usersId(testUsersId)
            .score(5L)
            .description(testDescription)
            .build();
}