package modo.controller;

import lombok.RequiredArgsConstructor;
import modo.auth.JwtTokenProvider;
import modo.service.KakaoLoginService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AuthController extends BaseController {

    private final JwtTokenProvider jwtTokenProvider;
    private final KakaoLoginService kakaoLoginService;

    @GetMapping("/oauth/kakao")
    public ResponseEntity<?> kakaoLogin(@RequestParam("code") String code) throws Exception {
        return sendResponse(kakaoLoginService.loginOrRegister(code));
    }

    @PostMapping("/api/v1/auth/reIssue")
    public ResponseEntity<?> reIssue(@RequestHeader("token") String token) throws Exception {
        return sendResponse(jwtTokenProvider.reIssue(token));
    }
}
