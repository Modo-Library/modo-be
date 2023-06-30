package modo.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import modo.domain.dto.users.Users.UsersLoginResponseDto;
import modo.domain.dto.users.Users.UsersSaveRequestDto;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@RequiredArgsConstructor
@Service
@Log4j2
@PropertySource("classpath:application.properties")
public class KakaoLoginService {

    private final UsersService usersService;

    private RestTemplate restTemplate = new RestTemplate();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${kakao.restApiKey}")
    private String restApiKey;

    @Value("${kakao.redirectUrl}")
    private String redirectUrl;

    public UsersLoginResponseDto loginOrRegister(String code) throws Exception {
        return getKakaoToken(code);
    }

    public UsersLoginResponseDto getKakaoToken(String code) throws Exception {
        final String grantType = "authorization_code";
        final String tokenRequestUrl = "https://kauth.kakao.com/oauth/token";
        final String contentType = "Content-Type: application/x-www-form-urlencoded";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", contentType);
        HttpEntity<String> entity = new HttpEntity<String>(headers);

        UriComponents uri = UriComponentsBuilder.fromHttpUrl(tokenRequestUrl)
                .queryParam("grant_type", grantType)
                .queryParam("client_id", restApiKey)
                .queryParam("redirect_uri", redirectUrl)
                .queryParam("code", code)
                .build(false);

        ResponseEntity<Map> response = restTemplate.exchange(uri.toUriString(), HttpMethod.POST, entity, Map.class);
        String accessToken = response.getBody().get("access_token").toString();

        return getKakaoUserInfo(accessToken);
    }

    public UsersLoginResponseDto getKakaoUserInfo(String accessToken) throws Exception {
        final String userInfoRequestUrl = "https://kapi.kakao.com/v2/user/me";
        final JSONArray property_keys = new JSONArray();
        property_keys.put("kakao_account.email");
        property_keys.put("kakao_account.profile.nickname");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity<String> entity = new HttpEntity<String>(headers);

        UriComponents uri = UriComponentsBuilder.fromHttpUrl(userInfoRequestUrl)
                .queryParam("property_keys", property_keys)
                .build(false);

        ResponseEntity<Map> response = restTemplate.exchange(uri.toUriString(), HttpMethod.POST, entity, Map.class);
        JSONObject responseJson = new JSONObject(response.getBody());
        JSONObject kakao_acount = (JSONObject) responseJson.get("kakao_account");
        JSONObject profile = (JSONObject) kakao_acount.get("profile");

        String nickname = profile.getString("nickname");
        String email = kakao_acount.getString("email");

        return registerAndLogin(nickname, email);
    }

    public UsersLoginResponseDto registerAndLogin(String nickname, String email) throws Exception {
        if (!usersService.isExistsByUsersId(email)) {
            UsersSaveRequestDto requestDto = UsersSaveRequestDto.builder()
                    .usersId(email)
                    .nickname(nickname)
                    .latitude(latitude_ajou)
                    .longitude(longitude_ajou)
                    .build();

            usersService.save(requestDto);
        }
        return usersService.login(email);
    }

    private static final double latitude_ajou = 37.28016;
    private static final double longitude_ajou = 127.043705;

}
