package modo.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.SignedJWT;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import modo.domain.dto.users.Users.UsersLoginResponseDto;
import modo.domain.dto.users.Users.UsersSaveRequestDto;
import modo.domain.entity.Users;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.*;
import java.security.PrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;
import java.util.*;

@RequiredArgsConstructor
@Log4j2
@Service
@PropertySource("classpath:application.properties")
public class AppleLoginService {

    @Value("${apple.team.id}")
    private String teamId;

    @Value("${apple.client.id}")
    private String clientId;

    @Value("${apple.key.id}")
    private String keyId;

    @Value("${apple.key.path}")
    private String keyPath;

    private String appleUrl = "https://appleid.apple.com";
    private static ObjectMapper objectMapper = new ObjectMapper();
    private RestTemplate restTemplate = new RestTemplate();
    private final UsersService usersService;

    public UsersLoginResponseDto loginOrRegister(String code) throws Exception {

        log.info("Try to get information in appleKeyId");
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(keyPath);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String readLine = null;
        StringBuilder stringBuilder = new StringBuilder();
        while ((readLine = bufferedReader.readLine()) != null) {
            stringBuilder.append(readLine);
            stringBuilder.append("\n");
        }
        String keyPath = stringBuilder.toString();
        log.info("Success to get information in appleKeyId!");

        log.info("Try to make privateKey");
        Reader reader = new StringReader(keyPath);
        PEMParser pemParser = new PEMParser(reader);
        JcaPEMKeyConverter jcaPEMKeyConverter = new JcaPEMKeyConverter();
        PrivateKeyInfo privateKeyInfo = (PrivateKeyInfo) pemParser.readObject();
        PrivateKey privateKey = jcaPEMKeyConverter.getPrivateKey(privateKeyInfo);
        log.info("Success to make privateKey");

        Map<String, Object> headerParamsMap = new HashMap<>();
        headerParamsMap.put("kid", keyId);
        headerParamsMap.put("alg", "ES256");

        log.info("Try to create clientSecretKey");
        String clientSecretKey = Jwts
                .builder()
                .setHeaderParams(headerParamsMap)
                .setIssuer(teamId)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 30)) // 만료 시간 (30초)
                .setAudience(appleUrl)
                .setSubject(clientId)
                .signWith(SignatureAlgorithm.ES256, privateKey)
                .compact();
        log.info("Success to create clientSecretKey");

        log.info("Try to get idToken with apple server with code value");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf(MediaType.APPLICATION_JSON_VALUE));
        HttpEntity<String> entity = new HttpEntity<String>(headers);

        UriComponents uri = UriComponentsBuilder.fromHttpUrl(appleUrl + "/auth/token")
                .queryParam("grant_type", "authorization_code")
                .queryParam("client_id", clientId)
                .queryParam("client_secret", clientSecretKey)
//               TODO(appleLogin.getCode() Front-end와 상의해서 맞추기)
//              .queryParam("code", appleLogin.getCode())
                .build(false);

        ResponseEntity<Map> response = restTemplate.exchange(uri.toUriString(), HttpMethod.POST, entity, Map.class);
        String idToken = response.getBody().get("idToken").toString();
        log.info("Success to get idToken with apple server with code value");
        log.info("IdToken : {}", idToken);

        return getEmailWithUsingIdToken(idToken);
    }

    public UsersLoginResponseDto getEmailWithUsingIdToken(String idToken) throws Exception {
        log.info("Try to get Email with using IdToken : {}", idToken);

        log.info("Get Keys From Apple Server");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf(MediaType.APPLICATION_JSON_VALUE));
        HttpEntity<String> entity = new HttpEntity<String>(headers);

        UriComponents uri_key = UriComponentsBuilder.fromHttpUrl(appleUrl + "/auth/keys")
                .build(false);

        ResponseEntity<Map> response_key = restTemplate.exchange(uri_key.toUriString(), HttpMethod.GET, entity, Map.class);
        List<Map<String, Object>> keys = (List<Map<String, Object>>) response_key.getBody().get("keys");

        log.info("Find Keys From Apple Server");
        // 가져온 public key 중 idToken을 암호화한 key가 있는지 확인
        SignedJWT signedJWT = SignedJWT.parse(idToken);
        for (Map<String, Object> key : keys) {
            RSAKey rsaKey = (RSAKey) JWK.parse(new ObjectMapper().writeValueAsString(key));
            RSAPublicKey rsaPublicKey = rsaKey.toRSAPublicKey();
            JWSVerifier jwsVerifier = new RSASSAVerifier(rsaPublicKey);

            // idToken을 암호화한 key인 경우
            if (signedJWT.verify(jwsVerifier)) {
                // jwt를 .으로 나눴을때 가운데에 있는 payload 확인
                String payload = idToken.split("[.]")[1];
                // public key로 idToken 복호화
                Map<String, Object> payloadMap = new ObjectMapper().readValue(new String(Base64.getDecoder().decode(payload)), Map.class);

                // 사용자 이메일 정보 추출
                String sub = payloadMap.get("sub").toString();
                String usersId;
                try {
                    usersId = usersService.findUsersIdBySub(sub);
                } catch (IllegalArgumentException e) {
                    usersId = payloadMap.get("email").toString();
                }

                // 결과 반환
                return registerAndLogin("nickname", usersId, sub);
            }
        }
        // 결과 반환
        return null;
    }

    @Transactional
    public UsersLoginResponseDto registerAndLogin(String nickname, String email, String sub) throws Exception {
        if (!usersService.isExistsByUsersId(email)) {
            UsersSaveRequestDto requestDto = UsersSaveRequestDto.builder()
                    .usersId(email)
                    .nickname(nickname)
                    .latitude(latitude_ajou)
                    .longitude(longitude_ajou)
                    .build();

            usersService.save(requestDto, sub);
        }
        return usersService.login(email);
    }

    private static final double latitude_ajou = 37.28016;
    private static final double longitude_ajou = 127.043705;

}
