package modo.controller;

import lombok.RequiredArgsConstructor;
import modo.auth.JwtTokenProvider;
import modo.service.AppleLoginService;
import modo.service.KakaoLoginService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController extends BaseController {

    private final JwtTokenProvider jwtTokenProvider;
    private final KakaoLoginService kakaoLoginService;
    private final AppleLoginService appleLoginService;

    @PostMapping("/oauth/kakao")
    public ResponseEntity<?> kakaoLogin(@RequestParam("code") String code) throws Exception {
        return sendResponse(kakaoLoginService.loginOrRegister(code));
    }

    @PostMapping("/api/v1/auth/reIssue")
    public ResponseEntity<?> reIssue(@RequestHeader("token") String token) throws Exception {
        return sendResponse(jwtTokenProvider.reIssue(token));
    }

    @PostMapping("/oauth/kakao/app")
    public ResponseEntity<?> kakaoLoginForApp(@RequestParam("token") String token) throws Exception {
        return sendResponse(kakaoLoginService.getKakaoUserInfo(token));
    }

    @PostMapping("/oauth/apple")
    public ResponseEntity<?> appleLogin(@RequestParam("code") String code) throws Exception {
        return sendResponse(appleLoginService.loginOrRegister(code));
    }

    @PostMapping("/oauth/apple/app")
    public ResponseEntity<?> appleLoginForApp(@RequestParam("idToken") String token) throws Exception {
        return sendResponse(appleLoginService.getEmailWithUsingIdToken(token));
    }
}
