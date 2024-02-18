package edu.java.bot.repository;

import edu.java.bot.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;

public class UserRepositoryImplTest {

    private UserRepositoryImpl userRepository;
    private User user1;
    private User user2;

    @BeforeEach
    public void setup() {
        userRepository = new UserRepositoryImpl();
        user1 = new User(123L, new HashSet<>(List.of(
            "https://example.com",
            "https://example.ru"))
        );
        user2 = new User(456L, new HashSet<>());
    }

    @Test
    public void testSave() {
        userRepository.save(user1);
        Optional<User> retrievedUser = userRepository.findByChatId(user1.getChatId());

        assertThat(retrievedUser).isPresent();
        assertThat(retrievedUser.get()).isEqualTo(user1);
    }

    @Test
    public void testFindAll() {
        userRepository.save(user1);
        userRepository.save(user2);

        List<User> userList = userRepository.findAll();

        assertThat(userList).containsExactlyInAnyOrder(user1, user2);
    }

    @Test
    public void testFindByChatIdWhenUserExists() {
        userRepository.save(user1);

        Optional<User> retrievedUser = userRepository.findByChatId(123L);

        assertThat(retrievedUser).isPresent();
        assertThat(retrievedUser.get()).isEqualTo(user1);
    }

    @Test
    public void testFindByChatIdWhenUserDoesNotExist() {
        Optional<User> retrievedUser = userRepository.findByChatId(123L);

        assertThat(retrievedUser).isEmpty();
    }

    @Test
    public void testDelete() {
        userRepository.save(user1);

        userRepository.delete(user1);
        Optional<User>  retrievedUser = userRepository.findByChatId(user1.getChatId());

        assertThat(retrievedUser).isEmpty();
    }
}
