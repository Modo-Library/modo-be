package modo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import modo.domain.dto.users.Users.UsersSaveRequestDto;
import modo.domain.dto.users.UsersReview.UsersReviewSaveRequestDto;
import modo.service.KakaoLoginService;
import modo.service.UsersService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@Log4j2
public class UsersController extends BaseController {
    private final UsersService usersService;
    private final KakaoLoginService kakaoLoginService;

    @PostMapping("api/v1/users/save")
    public ResponseEntity<?> save(@RequestBody UsersSaveRequestDto requestDto) {
        return sendResponse(usersService.save(requestDto));
    }

    @GetMapping("api/v1/users/findUsers/{usersId}")
    public ResponseEntity<?> findUsers(@PathVariable String usersId) {
        return sendResponse(usersService.findUsers(usersId));
    }

    @GetMapping("api/v1/users/findUsersFetchReview/{usersId}")
    public ResponseEntity<?> findUsersFetchReview(@PathVariable String usersId) {
        return sendResponse(usersService.findUsersFetchReview(usersId));
    }

    @PutMapping("api/v1/users/addReview/{usersId}")
    public ResponseEntity<?> addReview(@RequestBody UsersReviewSaveRequestDto requestDto) {
        return sendResponse(usersService.addReview(requestDto));
    }

    @DeleteMapping("api/v1/users/removeReview/{usersReviewId}")
    public ResponseEntity<?> removeReview(@PathVariable Long usersReviewId) {
        return sendResponse(usersService.removeReview(usersReviewId));
    }

    @GetMapping("/oauth/kakao")
    public ResponseEntity<?> kakaoLogin(@RequestParam("code") String code) throws Exception {
        return sendResponse(kakaoLoginService.loginOrRegister(code));
    }

}
