package modo.socket;

import lombok.extern.log4j.Log4j2;
import modo.repository.UsersRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(RestDocumentationExtension.class)
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Log4j2
public class WebSocketIntegrationTest {
    @Autowired
    private UsersRepository usersRepository;


}
