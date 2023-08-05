package modo.users;

import modo.domain.entity.UsersHistory;
import modo.repository.UsersHistoryRepository;
import modo.repository.UsersRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class UsersHistoryRepositoryTest {
    @Autowired
    UsersHistoryRepository usersHistoryRepository;

    @Autowired
    UsersRepository usersRepository;

    @BeforeEach
    void tearDown() {
        usersHistoryRepository.deleteAllInBatch();
    }

    @Test
    void Repository_회원가입및조회_테스트() {
        UsersHistory usersHistory = UsersHistory.builder()
                .usersId(testUsersId)
                .rentingCount(testRentingCount)
                .returningCount(testReturningCount)
                .buyCount(testBuyCount)
                .sellCount(testSellCount)
                .build();

        usersHistoryRepository.save(usersHistory);
        UsersHistory target = usersHistoryRepository.findById(testUsersId).orElseThrow(
                () -> new IllegalArgumentException("")
        );
        assertThat(target.getBuyCount()).isEqualTo(testBuyCount);
        assertThat(target.getSellCount()).isEqualTo(testSellCount);
        assertThat(target.getRentingCount()).isEqualTo(testRentingCount);
        assertThat(target.getReturningCount()).isEqualTo(testReturningCount);
    }

    @Test
    void Repository_회원조회_실패_테스트() {
        assertThrows(IllegalArgumentException.class, () -> usersHistoryRepository.findById(testUsersId).orElseThrow(() -> new IllegalArgumentException("")));
    }

    static final String testUsersId = "testUsersId";
    static final long testRentingCount = 1L;
    static final long testReturningCount = 2L;
    static final long testBuyCount = 3L;
    static final long testSellCount = 4L;


}
