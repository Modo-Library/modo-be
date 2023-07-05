package modo.controller;

import lombok.RequiredArgsConstructor;
import modo.auth.JwtTokenProvider;
import modo.service.KakaoLoginService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController extends BaseController {

    private final JwtTokenProvider jwtTokenProvider;
    private final KakaoLoginService kakaoLoginService;

    @GetMapping("/oauth/kakao")
    public ResponseEntity<?> kakaoLogin(@RequestParam("code") String code) throws Exception {
        return sendResponse(kakaoLoginService.loginOrRegister(code));
    }
}
