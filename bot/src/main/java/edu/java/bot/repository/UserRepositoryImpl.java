package edu.java.bot.repository;

import edu.java.bot.model.User;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final Map<Long, User> users = new HashMap<>();

    @Override
    public User save(User user) {
        users.put(user.getChatId(), user);
        return user;
    }

    @Override
    public List<User> findAll() {
        return users.values().stream().toList();
    }

    @Override
    public Optional<User> findByChatId(Long chatId) {
        return Optional.ofNullable(users.get(chatId));
    }

    @Override
    public void delete(User user) {
        users.remove(user.getChatId());
    }
}
