//package modo.service;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.nimbusds.jwt.SignedJWT;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.SignatureAlgorithm;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.log4j.Log4j2;
//import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
//import org.bouncycastle.openssl.PEMParser;
//import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.PropertySource;
//import org.springframework.stereotype.Service;
//
//import java.io.*;
//import java.security.PrivateKey;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Map;
//
//@RequiredArgsConstructor
//@Log4j2
//@Service
//@PropertySource("classpath:application.properties")
//public class AppleLoginService {
//
//    @Value("${apple.team.id}")
//    private String teamId;
//
//    @Value("${apple.client.id}")
//    private String clientId;
//
//    @Value("${apple.key.id}")
//    private String keyId;
//
//    @Value("${apple.key.path}")
//    private String keyPath;
//
//    private String appleUrl = "https://appleid.apple.com";
//
//    private static ObjectMapper objectMapper = new ObjectMapper();
//
//    public Object appleLogin() throws Exception {
//        /**
//         * appleKeyId를 이용하여 privateKey 생성
//         */
//
//        // appleKeyId에 담겨있는 정보 가져오기
//        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(keyPath);
//        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
//        String readLine = null;
//        StringBuilder stringBuilder = new StringBuilder();
//        while ((readLine = bufferedReader.readLine()) != null) {
//            stringBuilder.append(readLine);
//            stringBuilder.append("\n");
//        }
//        String keyPath = stringBuilder.toString();
//
//        // privateKey 생성하기
//        Reader reader = new StringReader(keyPath);
//        PEMParser pemParser = new PEMParser(reader);
//        JcaPEMKeyConverter jcaPEMKeyConverter = new JcaPEMKeyConverter();
//        PrivateKeyInfo privateKeyInfo = (PrivateKeyInfo) pemParser.readObject();
//        PrivateKey privateKey = jcaPEMKeyConverter.getPrivateKey(privateKeyInfo);
//
//        /**
//         * privateKey를 이용하여 clientSecretKey 생성
//         */
//
//        // headerParams 적재
//        Map<String, Object> headerParamsMap = new HashMap<>();
//        headerParamsMap.put("kid", keyId);
//        headerParamsMap.put("alg", "ES256");
//
//        // clientSecretKey 생성
//        String clientSecretKey = Jwts
//                .builder()
//                .setHeaderParams(headerParamsMap)
//                .setIssuer(teamId)
//                .setIssuedAt(new Date(System.currentTimeMillis()))
//                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 30)) // 만료 시간 (30초)
//                .setAudience(appleUrl)
//                .setSubject(clientId)
//                .signWith(SignatureAlgorithm.ES256, privateKey)
//                .compact();
//
//        /**
//         * code값을 이용하여 token정보 가져오기
//         */
//
//        // webClient 설정
////        WebClient webClient =
////                WebClient
////                        .builder()
////                        .baseUrl(appleUrl)
////                        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
////                        .build();
//
//        // token api 호출
////        Map<String, Object> tokenResponse =
////                webClient
////                        .post()
////                        .uri(uriBuilder -> uriBuilder
////                                .path("/auth/token")
////                                .queryParam("grant_type", "authorization_code")
////                                .queryParam("client_id", appleKey)
////                                .queryParam("client_secret", clientSecretKey)
////                                .queryParam("code", appleLogin.getCode())
////                                .build())
////                        .retrieve()
////                        .bodyToMono(Map.class)
////                        .block();
//
////        String idToken = (String) tokenResponse.get("id_token");
//
//        /**
//         * apple public key로 idToken을 복호화하여 사용자 이메일 정보 확인하기
//         */
//
//        // key api 호출
//        Map<String, Object> keyReponse =
//                webClient
//                        .get()
//                        .uri(uriBuilder -> uriBuilder
//                                .path("/auth/keys")
//                                .build())
//                        .retrieve()
//                        .bodyToMono(Map.class)
//                        .block();
//
//        List<Map<String, Object>> keys = (List<Map<String, Object>>) keyReponse.get("keys");
//
//        // 가져온 public key 중 idToken을 암호화한 key가 있는지 확인
//        SignedJWT signedJWT = SignedJWT.parse(idToken);
//        for (Map<String, Object> key : keys) {
//            RSAKey rsaKey = (RSAKey) JWK.parse(new ObjectMapper().writeValueAsString(key));
//            RSAPublicKey rsaPublicKey = rsaKey.toRSAPublicKey();
//            JWSVerifier jwsVerifier = new RSASSAVerifier(rsaPublicKey);
//
//            // idToken을 암호화한 key인 경우
//            if (signedJWT.verify(jwsVerifier)) {
//                // jwt를 .으로 나눴을때 가운데에 있는 payload 확인
//                String payload = idToken.split("[.]")[1];
//                // public key로 idToken 복호화
//                Map<String, Object> payloadMap = new ObjectMapper().readValue(new String(Base64.getDecoder().decode(payload)), Map.class);
//                // 사용자 이메일 정보 추출
//                String email = payloadMap.get("email").toString();
//
//                // 결과 반환
//                return ResponseEntity.ok(email);
//            }
//        }
//
//        // 결과 반환
//        return null;
//
//    }
//
//}
