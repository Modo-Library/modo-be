package modo.users;

import modo.domain.entity.UsersReview;
import modo.repository.UsersReviewRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@AutoConfigureTestDatabase
public class UsersReviewRepositoryTest {

    @Autowired
    private UsersReviewRepository usersReviewRepository;

    @Test
    void Repository_사용자리뷰저장및조회_테스트() {
        UsersReview usersReview = UsersReview.builder()
                .usersId(testUsersId)
                .description(testDescription)
                .score(testScore)
                .build();

        usersReviewRepository.save(usersReview);
        UsersReview target = usersReviewRepository.findById(testUsersId).orElseThrow(() -> new IllegalArgumentException(""));

        assertThat(target.getUsersId()).isEqualTo(testUsersId);
        assertThat(target.getDescription()).isEqualTo(testDescription);
        assertThat(target.getScore()).isEqualTo(testScore);
    }

    @Test
    void Repository_사용자리뷰저장및조회_실패_테스트() {
        assertThrows(IllegalArgumentException.class, () -> usersReviewRepository.findById(testUsersId).orElseThrow(() -> new IllegalArgumentException("")));
    }

    static final String testUsersId = "testUsersId";
    static final Long testScore = 5L;
    static final String testDescription = "testDescription";
}
