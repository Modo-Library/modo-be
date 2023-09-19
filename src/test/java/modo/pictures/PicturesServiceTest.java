package modo.pictures;

import modo.repository.BooksRepository;
import modo.repository.PicturesRepository;
import modo.repository.UsersRepository;
import modo.service.PicturesService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class PicturesServiceTest {
    @Autowired
    PicturesRepository picturesRepository;

    @Autowired
    UsersRepository usersRepository;

    @Autowired
    BooksRepository booksRepository;

    PicturesService picturesService;

    @BeforeEach
    void tearDown() {
        picturesRepository.deleteAllInBatch();
    }

    @BeforeEach
    void injectRepositoryToPicturesService() {

    }
}
