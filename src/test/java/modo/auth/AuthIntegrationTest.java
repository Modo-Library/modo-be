package modo.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import modo.domain.dto.ErrorJson;
import modo.domain.dto.users.Users.UsersSaveRequestDto;
import modo.enums.ErrorCode;
import modo.repository.AccessTokenRepository;
import modo.repository.UsersHistoryRepository;
import modo.repository.UsersRepository;
import org.json.JSONObject;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(RestDocumentationExtension.class)
public class AuthIntegrationTest {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private UsersHistoryRepository usersHistoryRepository;

    @Autowired
    private AccessTokenRepository accessTokenRepository;

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
    void resetTokenValidTime() {
        jwtTokenProvider.setAccessTokenValidTime(60 * 60L);
        jwtTokenProvider.setRefreshTokenValidTime(30 * 3600 * 60L);
    }


    @BeforeEach
    void tearDown() {
        usersHistoryRepository.deleteAllInBatch();
        usersRepository.deleteAllInBatch();
        accessTokenRepository.deleteAll();
    }

    @Test
    void reIssue_테스트() throws Exception {
        // Set Access Token valid time to 1s
        jwtTokenProvider.setAccessTokenValidTime(1L);

        // Create new testUser and new access, refresh token
        saveNewTestUsersAndCreateNewToken();

        // Wait until accessToken expired
        Thread.sleep(1001);

        mockMvc.perform(post("/api/v1/auth/reIssue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("token", refreshToken)
                )
                .andExpect(status().isOk())
                .andDo(document("Auth-ReIssue",
                        Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                        requestHeaders(
                                headerWithName("token").description("Refresh Token value")
                        ),
                        responseFields(
                                fieldWithPath("accessToken").description("New access token value").type(JsonFieldType.STRING),
                                fieldWithPath("refreshToken").description("Existing refresh token value").type(JsonFieldType.STRING)
                        )))
                .andDo(print())
                .andReturn();
    }

    @Test
    void 만료된Access토큰으로_메소드요청시도_테스트() throws Exception {
        // Set Access Token valid time to 1s
        jwtTokenProvider.setAccessTokenValidTime(1L);

        // Create new testUser and new access, refresh token
        saveNewTestUsersAndCreateNewToken();

        // Wait until accessToken expired
        Thread.sleep(1001);

        MvcResult testResult = mockMvc.perform(get("/api/v1/users/findUsers/{usersId}", testUsersId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("token", accessToken)
                )
                .andExpect(status().isUnauthorized())
                .andDo(document("Auth-ExpiredJwtException",
                        Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                        requestHeaders(
                                headerWithName("token").description("Access Token value")
                        ),
                        pathParameters(
                                parameterWithName("usersId").description("Finding user's usersId")
                        ),
                        responseFields(
                                fieldWithPath("message").description("Exception message").type(JsonFieldType.STRING),
                                fieldWithPath("errorCode").description("Exception code").type(JsonFieldType.NUMBER),
                                fieldWithPath("name").description("Exception name").type(JsonFieldType.STRING)
                        )))
                .andDo(print())
                .andReturn();

        JSONObject resultBody = new JSONObject(testResult.getResponse().getContentAsString());
        assertThat(resultBody.get("name")).isEqualTo(ErrorCode.ExpiredJwtException.name());

    }

    @Test
    void Access토큰만료전_reIssue요청시도_테스트() throws Exception {
        // Create new testUser and new access, refresh token
        saveNewTestUsersAndCreateNewToken();

        MvcResult testResult = mockMvc.perform(post("/api/v1/auth/reIssue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("token", refreshToken)
                )
                .andExpect(status().isBadRequest())
                .andDo(document("Auth-ReIssueBeforeAccessTokenExpiredException",
                        Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                        requestHeaders(
                                headerWithName("token").description("Refresh Token value")
                        ),
                        responseFields(
                                fieldWithPath("message").description("Exception message").type(JsonFieldType.STRING),
                                fieldWithPath("errorCode").description("Exception code").type(JsonFieldType.NUMBER),
                                fieldWithPath("name").description("Exception name").type(JsonFieldType.STRING)
                        )))
                .andDo(print())
                .andReturn();

        JSONObject resultBody = new JSONObject(testResult.getResponse().getContentAsString());
        assertThat(resultBody.get("name")).isEqualTo(ErrorCode.ReIssueBeforeAccessTokenExpiredException.name());

    }

    @Test
    void 만료된RefreshToken으로_reIssue요청시도_테스트() throws Exception {
        // Set Refresh Token valid time to 1s
        jwtTokenProvider.setRefreshTokenValidTime(1L);

        // Create new testUser and new access, refresh token
        saveNewTestUsersAndCreateNewToken();

        Thread.sleep(1000L);

        MvcResult testResult = mockMvc.perform(post("/api/v1/auth/reIssue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("token", refreshToken)
                )
                .andExpect(status().isUnauthorized())
                .andDo(print())
                .andReturn();

        JSONObject resultBody = new JSONObject(testResult.getResponse().getContentAsString());
        assertThat(resultBody.get("name")).isEqualTo(ErrorCode.ExpiredJwtException.name());

    }

    @Test
    void 잘못된Access토큰으로_메소드요청시도_테스트() throws Exception {
        // Create new testUser and new access, refresh token
        saveNewTestUsersAndCreateNewToken();

        MvcResult testResult = mockMvc.perform(get("/api/v1/users/findUsers/{usersId}", testUsersId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("token", accessToken + "wrong")
                )
                .andExpect(status().isBadRequest())
                .andDo(document("Auth-SignatureException",
                        Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                        requestHeaders(
                                headerWithName("token").description("Wrong access Token value")
                        ),
                        pathParameters(
                                parameterWithName("usersId").description("Finding user's usersId")
                        ),
                        responseFields(
                                fieldWithPath("message").description("Exception message").type(JsonFieldType.STRING),
                                fieldWithPath("errorCode").description("Exception code").type(JsonFieldType.NUMBER),
                                fieldWithPath("name").description("Exception name").type(JsonFieldType.STRING)
                        )))
                .andDo(print())
                .andReturn();

        JSONObject resultBody = new JSONObject(testResult.getResponse().getContentAsString());
        assertThat(resultBody.get("name")).isEqualTo(ErrorCode.SignatureException.name());

    }

    @Test
    void 잘못된Refresh토큰으로_메소드요청시도_테스트() throws Exception {
        // Create new testUser and new access, refresh token
        saveNewTestUsersAndCreateNewToken();

        MvcResult testResult = mockMvc.perform(post("/api/v1/auth/reIssue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("token", refreshToken + "wrong")
                )
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andReturn();

        JSONObject resultBody = new JSONObject(testResult.getResponse().getContentAsString());
        assertThat(resultBody.get("name")).isEqualTo(ErrorCode.SignatureException.name());

    }

    private void saveNewTestUsersAndCreateNewToken() throws Exception {
        mockMvc.perform(post("/api/v2/users/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUsersSaveRequestDto))
                )
                .andExpect(status().isOk());

        accessToken = jwtTokenProvider.createAccessToken(testUsersId);
        refreshToken = jwtTokenProvider.createRefreshToken(testUsersId);
    }


    static final String testUsersId = "testUsersId";
    static final String testNickname = "testNickname";
    static final double testX = 1.1;
    static final double testY = 2.2;
    static final double testReviewScore = 0.0;
    static final Long testReviewCount = 0L;
    static final String testDescription = "testDescription";

    static final UsersSaveRequestDto testUsersSaveRequestDto = UsersSaveRequestDto.builder()
            .usersId(testUsersId)
            .nickname(testNickname)
            .latitude(testX)
            .longitude(testY)
            .build();

}
