package modo.util.socket;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Log4j2
@Controller
@RequiredArgsConstructor
public class GreetingController {

    private final SimpMessagingTemplate simpMessagingTemplate;

    // /app/hello 엔트포인트로 client 요청을 보내면
    // 서버에서 greeting 메소드 동작 이후
    // /topic/greetings 엔드포인트로 리턴을 보냄

    @MessageMapping("/hello")
    public void greeting(HelloMessage message) throws Exception {
        Thread.sleep(1000); // simulated delay
        log.info("greeting is called");
        simpMessagingTemplate.convertAndSend("/topic/greetings", new Greeting(message.getName()));
    }
}